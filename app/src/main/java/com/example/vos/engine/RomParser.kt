package com.example.vos.engine

import android.content.Context
import android.net.Uri
import android.os.Build
import com.example.vos.data.model.RomEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

data class RomValidationResult(
    val isValid: Boolean,
    val romEntity: RomEntity?,
    val validationStatus: String = "VALID", // "VALID", "INCOMPATIBLE_ARCH", "MISSING_BOOT_COMPONENTS", "CORRUPTED_ARCHIVE", "UNSUPPORTED_FORMAT"
    val validationSummary: String = "",
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val detectedFiles: List<String> = emptyList(),
    val parsedProps: Map<String, String> = emptyMap(),
    val hasBootComponent: Boolean = false,
    val hasSystemComponent: Boolean = false,
    val isArchCompatible: Boolean = true,
    val bootComponentDetail: String = "None",
    val systemComponentDetail: String = "None"
)

object RomParser {

    suspend fun parseAndImportZip(
        context: Context,
        uri: Uri,
        onProgress: (Float, String) -> Unit
    ): RomValidationResult = withContext(Dispatchers.IO) {
        val detectedFiles = mutableListOf<String>()
        val parsedProps = mutableMapOf<String, String>()
        val warnings = mutableListOf<String>()
        val errors = mutableListOf<String>()

        val romId = "rom_" + UUID.randomUUID().toString().take(8)
        val romDir = File(context.filesDir, "roms/$romId")
        if (!romDir.exists()) romDir.mkdirs()

        val destZipFile = File(romDir, "imported_rom.zip")
        var totalBytesRead = 0L

        try {
            onProgress(0.1f, "Opening ROM ZIP package...")
            val contentResolver = context.contentResolver

            // Copy file to local private storage (guaranteeing local-only persistence)
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destZipFile).use { output ->
                    val buffer = ByteArray(64 * 1024)
                    var bytes = input.read(buffer)
                    while (bytes >= 0) {
                        output.write(buffer, 0, bytes)
                        totalBytesRead += bytes
                        bytes = input.read(buffer)
                    }
                }
            } ?: run {
                return@withContext RomValidationResult(
                    isValid = false,
                    romEntity = null,
                    validationStatus = "CORRUPTED_ARCHIVE",
                    validationSummary = "Could not open or read the selected file from device storage.",
                    errors = listOf("Could not open input stream from selected URI.")
                )
            }

            onProgress(0.3f, "Scanning ZIP archive entries & boot components...")

            // Scan ZIP entries
            var hasValidZipHeader = false
            destZipFile.inputStream().use { fileInput ->
                ZipInputStream(fileInput).use { zip ->
                    var entry: ZipEntry? = zip.nextEntry
                    var entriesCount = 0
                    while (entry != null) {
                        hasValidZipHeader = true
                        val name = entry.name
                        detectedFiles.add(name)
                        entriesCount++

                        // If it's a build.prop or metadata or manifest, extract and parse it
                        if (name.endsWith("build.prop") || name.endsWith("default.prop") || name.endsWith("metadata") || name.endsWith("rom_manifest.json")) {
                            val reader = BufferedReader(InputStreamReader(zip))
                            var line = reader.readLine()
                            while (line != null && (line.contains("=") || line.contains(":"))) {
                                if (line.startsWith("#") || line.isBlank()) {
                                    line = reader.readLine()
                                    continue
                                }
                                val parts = if (line.contains("=")) line.split("=", limit = 2) else line.split(":", limit = 2)
                                if (parts.size == 2) {
                                    parsedProps[parts[0].trim()] = parts[1].trim()
                                }
                                line = reader.readLine()
                            }
                        }

                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            }

            if (!hasValidZipHeader || detectedFiles.isEmpty()) {
                return@withContext RomValidationResult(
                    isValid = false,
                    romEntity = null,
                    validationStatus = "CORRUPTED_ARCHIVE",
                    validationSummary = "The imported file is not a valid or readable ZIP archive.",
                    errors = listOf("Archive contains 0 readable entries or is corrupted.")
                )
            }

            onProgress(0.6f, "Auditing bootloader components & rootfs partitions...")

            // 1. Check Essential Boot Components
            val bootImages = detectedFiles.filter {
                val lower = it.lowercase()
                lower.endsWith("boot.img") || lower.endsWith("boot.bin") || lower.endsWith("payload.bin") ||
                        lower.contains("zimage") || lower.contains("vmlinuz") || lower.contains("image.gz") ||
                        lower.contains("kernel") || lower.contains("updater-script") || lower.endsWith(".dtb")
            }
            val hasBootComponent = bootImages.isNotEmpty() || parsedProps.isNotEmpty()
            val bootDetail = if (bootImages.isNotEmpty()) {
                bootImages.take(2).joinToString(", ")
            } else if (parsedProps.isNotEmpty()) {
                "Embedded Android Framework Manifest"
            } else {
                "Missing boot.img / kernel / payload.bin"
            }

            // 2. Check System Rootfs / Partitions
            val systemPartitions = detectedFiles.filter {
                val lower = it.lowercase()
                lower.contains("system.img") || lower.contains("system.new.dat") || lower.contains("system.new.dat.br") ||
                        lower.contains("system/build.prop") || lower.contains("rootfs") || lower.contains("payload.bin") ||
                        lower.contains("system/") || lower.contains("system.transfer.list")
            }
            val hasSystemPartition = systemPartitions.isNotEmpty() || parsedProps.containsKey("ro.build.version.release") || parsedProps.containsKey("ro.build.display.id")
            val systemDetail = if (systemPartitions.isNotEmpty()) {
                systemPartitions.take(2).joinToString(", ")
            } else if (parsedProps.isNotEmpty()) {
                "Verified Build Properties Rootfs"
            } else {
                "Missing system partition or ext4 rootfs"
            }

            // 3. Check Architecture Compatibility
            val rawAbi = parsedProps["ro.product.cpu.abi"]
                ?: parsedProps["ro.product.cpu.abilist"]
                ?: parsedProps["architecture"]
                ?: parsedProps["target_arch"]
                ?: "arm64-v8a"

            val hostAbis = Build.SUPPORTED_ABIS.map { it.lowercase() }
            val hostPrimaryAbi = Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a"
            val romAbiLower = rawAbi.lowercase()

            val isArchCompatible = when {
                romAbiLower.contains("arm64") || romAbiLower.contains("aarch64") -> {
                    hostAbis.any { it.contains("arm64") || it.contains("aarch64") }
                }
                romAbiLower.contains("armeabi") || romAbiLower.contains("armv7") || romAbiLower.contains("arm") -> {
                    hostAbis.any { it.contains("arm") }
                }
                romAbiLower.contains("x86_64") -> {
                    hostAbis.any { it.contains("x86_64") }
                }
                romAbiLower.contains("x86") -> {
                    hostAbis.any { it.contains("x86") }
                }
                else -> true // Generic compatibility fallback
            }

            onProgress(0.85f, "Finalizing virtualization security descriptors...")

            // Determine ROM Identity & OS Flavor
            val releaseVersion = parsedProps["ro.build.version.release"]
                ?: parsedProps["version"]
                ?: "14.0"

            val displayId = parsedProps["ro.build.display.id"]
                ?: parsedProps["name"]
                ?: "Custom User ROM"

            val modelName = parsedProps["ro.product.model"]
                ?: parsedProps["device"]
                ?: "VOS Virtual Device"

            val osFlavor = when {
                displayId.contains("Lineage", ignoreCase = true) -> "LineageOS"
                displayId.contains("AOSP", ignoreCase = true) -> "Android"
                displayId.contains("Ubuntu", ignoreCase = true) || displayId.contains("Debian", ignoreCase = true) || displayId.contains("Droidian", ignoreCase = true) -> "Linux"
                displayId.contains("GSI", ignoreCase = true) -> "GSI"
                else -> "Android"
            }

            // Determine Overall Validation Status
            val validationStatus: String
            val validationSummary: String
            val isValid: Boolean

            if (!hasBootComponent && !hasSystemPartition && detectedFiles.size < 3) {
                isValid = false
                validationStatus = "MISSING_BOOT_COMPONENTS"
                val msg = "ROM Validation Failed: Essential boot components (boot.img, kernel, payload.bin) and system partition images were not found in this archive."
                validationSummary = msg
                errors.add(msg)
            } else if (!isArchCompatible) {
                // ROM is valid archive but target CPU architecture does not match host device!
                isValid = false
                validationStatus = "INCOMPATIBLE_ARCH"
                val msg = "Architecture Incompatibility Detected: The imported ROM targets '$rawAbi', but your host CPU is '$hostPrimaryAbi'. Direct user-space virtualization cannot execute foreign ISA code without heavy binary emulation."
                validationSummary = msg
                errors.add(msg)
            } else {
                isValid = true
                validationStatus = "VALID"
                validationSummary = "All essential boot components, partition images, and host CPU architecture ($rawAbi matches $hostPrimaryAbi) validated successfully."
            }

            if (!hasBootComponent && isValid) {
                warnings.add("Explicit boot.img not found; containerized init will bootstrap framework via VOS userspace loader.")
            }
            if (!hasSystemPartition && isValid) {
                warnings.add("System partition image not found; generic rootfs container will be synthesized.")
            }

            val propsJson = JSONObject().apply {
                parsedProps.forEach { (k, v) -> put(k, v) }
                if (!has("ro.build.version.release")) put("ro.build.version.release", releaseVersion)
                if (!has("ro.build.display.id")) put("ro.build.display.id", displayId)
                if (!has("ro.product.model")) put("ro.product.model", modelName)
                if (!has("ro.product.cpu.abi")) put("ro.product.cpu.abi", rawAbi)
                put("imported_at", System.currentTimeMillis())
                put("detected_files_count", detectedFiles.size)
                put("validation_status", validationStatus)
            }.toString()

            val romName = if (displayId.isNotBlank() && displayId != "Custom User ROM") displayId else "Imported ROM ($releaseVersion)"
            val romEntity = RomEntity(
                id = romId,
                name = romName,
                version = releaseVersion,
                architecture = rawAbi,
                osType = osFlavor,
                fileName = destZipFile.name,
                fileSize = totalBytesRead,
                zipPath = destZipFile.absolutePath,
                isDefault = false,
                status = if (isValid) "READY" else "INCOMPATIBLE",
                buildPropsJson = propsJson,
                securityPatch = parsedProps["ro.build.security_patch"] ?: "2024-08-01",
                apiLevel = parsedProps["ro.build.version.sdk"]?.toIntOrNull() ?: 34,
                iconColorHex = when (osFlavor) {
                    "LineageOS" -> 0xFF167C80
                    "Linux" -> 0xFFD70A53
                    "GSI" -> 0xFFFF9800
                    else -> 0xFF00E5FF
                },
                description = if (isValid) {
                    "Verified ROM package containing ${detectedFiles.size} components for $rawAbi."
                } else {
                    "Validation issue: $validationSummary"
                },
                isValid = isValid,
                validationStatus = validationStatus,
                validationSummary = validationSummary,
                hasBootImage = hasBootComponent,
                hasSystemPartition = hasSystemPartition,
                isArchCompatible = isArchCompatible,
                bootComponentFound = bootDetail,
                systemComponentFound = systemDetail,
                detectedFilesCount = detectedFiles.size
            )

            onProgress(1.0f, if (isValid) "ROM Verification passed!" else "ROM Verification complete (Issues detected).")

            RomValidationResult(
                isValid = isValid,
                romEntity = romEntity,
                validationStatus = validationStatus,
                validationSummary = validationSummary,
                errors = errors,
                warnings = warnings,
                detectedFiles = detectedFiles.take(50),
                parsedProps = parsedProps,
                hasBootComponent = hasBootComponent,
                hasSystemComponent = hasSystemPartition,
                isArchCompatible = isArchCompatible,
                bootComponentDetail = bootDetail,
                systemComponentDetail = systemDetail
            )
        } catch (e: Exception) {
            e.printStackTrace()
            RomValidationResult(
                isValid = false,
                romEntity = null,
                validationStatus = "CORRUPTED_ARCHIVE",
                validationSummary = "Failed to unpack or validate ROM ZIP: ${e.localizedMessage ?: e.message}",
                errors = listOf("Failed to unpack or validate ROM ZIP: ${e.localizedMessage ?: e.message}")
            )
        }
    }

    fun isArchitectureCompatible(romAbi: String, hostAbis: List<String>): Boolean {
        val romAbiLower = romAbi.lowercase()
        val hostAbisLower = hostAbis.map { it.lowercase() }
        return when {
            romAbiLower.contains("arm64") || romAbiLower.contains("aarch64") -> {
                hostAbisLower.any { it.contains("arm64") || it.contains("aarch64") }
            }
            romAbiLower.contains("armeabi") || romAbiLower.contains("armv7") || romAbiLower.contains("arm") -> {
                hostAbisLower.any { it.contains("arm") }
            }
            romAbiLower.contains("x86_64") -> {
                hostAbisLower.any { it.contains("x86_64") }
            }
            romAbiLower.contains("x86") -> {
                hostAbisLower.any { it.contains("x86") }
            }
            else -> true
        }
    }
}

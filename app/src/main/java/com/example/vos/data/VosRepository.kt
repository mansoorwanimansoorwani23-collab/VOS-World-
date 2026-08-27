package com.example.vos.data

import android.content.Context
import com.example.vos.data.model.DeviceBackupEntity
import com.example.vos.data.model.GuestAppEntity
import com.example.vos.data.model.GuestLogEntity
import com.example.vos.data.model.GuestSettingEntity
import com.example.vos.data.model.GuestStorageItemEntity
import com.example.vos.data.model.RomEntity
import com.example.vos.data.model.VirtualDeviceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class VosRepository(private val context: Context, private val db: AppDatabase) {

    val roms: Flow<List<RomEntity>> = db.romDao().getAllRoms()
    val virtualDevices: Flow<List<VirtualDeviceEntity>> = db.virtualDeviceDao().getAllDevices()
    val defaultDevice: Flow<VirtualDeviceEntity?> = db.virtualDeviceDao().getDefaultDevice()
    val backups: Flow<List<DeviceBackupEntity>> = db.deviceBackupDao().getAllBackups()

    fun getDeviceFlow(deviceId: String): Flow<VirtualDeviceEntity?> = db.virtualDeviceDao().getDeviceFlow(deviceId)
    fun getAppsForDevice(deviceId: String): Flow<List<GuestAppEntity>> = db.guestAppDao().getAppsForDevice(deviceId)
    fun getStorageForDevice(deviceId: String): Flow<List<GuestStorageItemEntity>> = db.guestStorageDao().getAllItemsForDevice(deviceId)
    fun getLogsForDevice(deviceId: String): Flow<List<GuestLogEntity>> = db.guestLogDao().getLogsForDevice(deviceId)
    fun getSettingsForDevice(deviceId: String): Flow<List<GuestSettingEntity>> = db.guestSettingDao().getSettingsForDevice(deviceId)

    suspend fun getDeviceById(id: String): VirtualDeviceEntity? = db.virtualDeviceDao().getDeviceById(id)
    suspend fun getRomById(id: String): RomEntity? = db.romDao().getRomById(id)

    suspend fun insertRom(rom: RomEntity) = db.romDao().insertRom(rom)
    suspend fun updateRom(rom: RomEntity) = db.romDao().updateRom(rom)

    suspend fun insertDevice(device: VirtualDeviceEntity) = db.virtualDeviceDao().insertDevice(device)
    suspend fun updateDevice(device: VirtualDeviceEntity) = db.virtualDeviceDao().updateDevice(device)
    suspend fun updateDeviceState(deviceId: String, state: String) = db.virtualDeviceDao().updateDeviceState(deviceId, state)
    suspend fun setDefaultDevice(deviceId: String) = db.virtualDeviceDao().setDefaultDevice(deviceId)

    suspend fun logGuest(deviceId: String, tag: String, level: String, message: String) {
        db.guestLogDao().insertLog(
            GuestLogEntity(
                deviceId = deviceId,
                tag = tag,
                level = level,
                message = message,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun initializePreloadedRomsIfEmpty() = withContext(Dispatchers.IO) {
        // Pre-configure standard ROMs so user can test immediately if they don't have custom ZIPs
        val sampleRoms = listOf(
            RomEntity(
                id = "rom_lineage_21",
                name = "LineageOS 21.0 Micro",
                version = "21.0-UNOFFICIAL",
                architecture = "ARM64-v8a",
                osType = "LineageOS",
                fileName = "lineage-21.0-202408-vos-arm64.zip",
                fileSize = 842000000L,
                zipPath = "local://samples/lineage21.zip",
                isDefault = true,
                status = "READY",
                buildPropsJson = """{
                    "ro.build.version.release": "14",
                    "ro.build.display.id": "LineageOS 21-20240820-NIGHTLY",
                    "ro.product.model": "VOS Virtual Pixel 8 Pro",
                    "ro.product.brand": "LineageOS",
                    "ro.vos.flavor": "Micro-G Included",
                    "ro.vos.kernel": "6.1.42-android-vos+",
                    "ro.build.security_patch": "2024-08-05"
                }""",
                securityPatch = "2024-08-05",
                apiLevel = 34,
                iconColorHex = 0xFF167C80,
                description = "Lightweight custom Android 14 ROM featuring clean AOSP experience and minimal resource footprint."
            ),
            RomEntity(
                id = "rom_aosp_14",
                name = "AOSP 14 Minimalist",
                version = "14.0.0_r50",
                architecture = "ARM64-v8a / x86_64",
                osType = "Android",
                fileName = "aosp-14.0.0-vanilla-vos.zip",
                fileSize = 712000000L,
                zipPath = "local://samples/aosp14.zip",
                isDefault = false,
                status = "READY",
                buildPropsJson = """{
                    "ro.build.version.release": "14",
                    "ro.build.display.id": "AOSP-UDC-14.0.0_r50",
                    "ro.product.model": "VOS Generic Android Device",
                    "ro.product.brand": "Android",
                    "ro.vos.flavor": "Vanilla Pure AOSP",
                    "ro.vos.kernel": "5.15.110-vos+",
                    "ro.build.security_patch": "2024-07-01"
                }""",
                securityPatch = "2024-07-01",
                apiLevel = 34,
                iconColorHex = 0xFF3DDC84,
                description = "Pure vanilla Android Open Source Project image with stock system services and standard tools."
            ),
            RomEntity(
                id = "rom_droidian_linux",
                name = "Droidian Mobile Linux",
                version = "Debian 13 Bookworm",
                architecture = "ARM64-v8a",
                osType = "Linux",
                fileName = "droidian-bookworm-arm64-vos.zip",
                fileSize = 985000000L,
                zipPath = "local://samples/droidian.zip",
                isDefault = false,
                status = "READY",
                buildPropsJson = """{
                    "ro.build.version.release": "Debian 13",
                    "ro.build.display.id": "Droidian Phosh Mobile DE",
                    "ro.product.model": "VOS Linux Smartphone",
                    "ro.product.brand": "Droidian",
                    "ro.vos.flavor": "Wayland/Phosh Mobile Linux",
                    "ro.vos.kernel": "6.6.21-droidian-arm64",
                    "ro.build.security_patch": "2024-08-01"
                }""",
                securityPatch = "2024-08-01",
                apiLevel = 0,
                iconColorHex = 0xFFD70A53,
                description = "Complete mobile Linux distribution powered by Debian & Phosh with full terminal and apt package manager."
            )
        )

        for (rom in sampleRoms) {
            val existing = db.romDao().getRomById(rom.id)
            if (existing == null) {
                db.romDao().insertRom(rom)
            }
        }

        // Check if there's a default virtual device, if not create one for LineageOS 21
        val existingDevice = db.virtualDeviceDao().getDeviceById("dev_lineage_01")
        if (existingDevice == null) {
            val dev = VirtualDeviceEntity(
                id = "dev_lineage_01",
                romId = "rom_lineage_21",
                name = "LineageOS Virtual Phone",
                state = "OFF",
                ramMb = 3072,
                storageMb = 16384,
                cpuCores = 4,
                virtualImei = "869402051284920",
                virtualMac = "02:00:00:1A:2B:3C",
                resolutionWidth = 1080,
                resolutionHeight = 2400,
                densityDpi = 420,
                isDefaultDevice = true,
                guestAndroidVersion = "14.0",
                guestKernelVersion = "6.1.42-android-vos+"
            )
            db.virtualDeviceDao().insertDevice(dev)
            provisionInitialGuestEnvironment(dev.id, "LineageOS")
        }
    }

    suspend fun provisionInitialGuestEnvironment(deviceId: String, osType: String) = withContext(Dispatchers.IO) {
        // Provision System Apps
        val systemApps = listOf(
            GuestAppEntity(UUID.randomUUID().toString(), deviceId, "com.android.settings", "Settings", "14.0", true, "settings"),
            GuestAppEntity(UUID.randomUUID().toString(), deviceId, "com.android.dialer", "Phone & SOS", "14.0", true, "phone"),
            GuestAppEntity(UUID.randomUUID().toString(), deviceId, "com.android.terminal", "Terminal Root", "2.4", true, "terminal"),
            GuestAppEntity(UUID.randomUUID().toString(), deviceId, "com.android.documentsui", "Files", "14.0", true, "files"),
            GuestAppEntity(UUID.randomUUID().toString(), deviceId, "com.android.camera2", "Camera", "3.2", true, "camera"),
            GuestAppEntity(UUID.randomUUID().toString(), deviceId, "org.chromium.browser", "Web Browser", "128.0", true, "browser"),
            GuestAppEntity(UUID.randomUUID().toString(), deviceId, "com.vos.notes", "Virtual Notes", "1.0", false, "notes"),
            GuestAppEntity(UUID.randomUUID().toString(), deviceId, "com.vos.vapkstore", "Package Manager", "1.2", false, "store")
        )
        db.guestAppDao().insertApps(systemApps)

        // Provision Default Virtual Filesystem
        val defaultFiles = listOf(
            GuestStorageItemEntity(UUID.randomUUID().toString(), deviceId, "/system/build.prop", "build.prop", false, 1420L, "ro.build.version.release=14\nro.vos.status=sandbox_verified\nro.secure=1", "text/plain"),
            GuestStorageItemEntity(UUID.randomUUID().toString(), deviceId, "/sdcard/Welcome.txt", "Welcome.txt", false, 480L, "Welcome to VOS World Virtual OS!\nThis is an isolated persistent user-space guest environment running on your local device.", "text/plain"),
            GuestStorageItemEntity(UUID.randomUUID().toString(), deviceId, "/sdcard/Documents/README.md", "README.md", false, 320L, "# Virtual Storage\nAll files created here are persistent and stored locally.", "text/markdown"),
            GuestStorageItemEntity(UUID.randomUUID().toString(), deviceId, "/sdcard/DCIM/welcome_capture.jpg", "welcome_capture.jpg", false, 120400L, "", "image/jpeg")
        )
        db.guestStorageDao().insertItems(defaultFiles)

        // Provision Default Settings
        val defaultSettings = listOf(
            GuestSettingEntity("${deviceId}_wifi_enabled", deviceId, "wifi_enabled", "true", "network"),
            GuestSettingEntity("${deviceId}_bluetooth_enabled", deviceId, "bluetooth_enabled", "true", "network"),
            GuestSettingEntity("${deviceId}_dark_mode", deviceId, "dark_mode", "true", "display"),
            GuestSettingEntity("${deviceId}_brightness", deviceId, "brightness", "85", "display"),
            GuestSettingEntity("${deviceId}_airplane_mode", deviceId, "airplane_mode", "false", "network"),
            GuestSettingEntity("${deviceId}_developer_options", deviceId, "developer_options", "true", "development"),
            GuestSettingEntity("${deviceId}_usb_debugging", deviceId, "usb_debugging", "true", "development")
        )
        db.guestSettingDao().insertSettings(defaultSettings)

        // Initial Boot Logs
        logGuest(deviceId, "vboot", "INFO", "VOS Virtual Device initialized with ID $deviceId (OS: $osType)")
        logGuest(deviceId, "vhal", "INFO", "Virtual Hardware Abstraction Layer mapped: CPU=4 cores, RAM=3072MB")
    }

    suspend fun createVirtualDeviceForRom(rom: RomEntity, customName: String? = null): VirtualDeviceEntity = withContext(Dispatchers.IO) {
        val deviceId = "dev_" + UUID.randomUUID().toString().take(8)
        val name = customName ?: "${rom.name} Device"
        val dev = VirtualDeviceEntity(
            id = deviceId,
            romId = rom.id,
            name = name,
            state = "OFF",
            ramMb = 3072,
            storageMb = 16384,
            cpuCores = 4,
            virtualImei = "86" + (1000000000000L..9999999999999L).random(),
            virtualMac = String.format("02:00:00:%02X:%02X:%02X", (0..255).random(), (0..255).random(), (0..255).random()),
            resolutionWidth = 1080,
            resolutionHeight = 2400,
            densityDpi = 420,
            isDefaultDevice = false,
            guestAndroidVersion = rom.version,
            guestKernelVersion = "6.1.42-vos-android+"
        )
        db.virtualDeviceDao().insertDevice(dev)
        provisionInitialGuestEnvironment(dev.id, rom.osType)
        dev
    }

    suspend fun createBackup(deviceId: String, backupName: String, note: String): DeviceBackupEntity = withContext(Dispatchers.IO) {
        val dev = db.virtualDeviceDao().getDeviceById(deviceId) ?: throw IllegalStateException("Device not found")
        val backupId = "bak_" + UUID.randomUUID().toString().take(8)
        val backupDir = File(context.filesDir, "backups")
        if (!backupDir.exists()) backupDir.mkdirs()
        val backupFile = File(backupDir, "$backupId.vosbak")
        backupFile.writeText("VOS_WORLD_SNAPSHOT_V1\nDevice: ${dev.name}\nTimestamp: ${System.currentTimeMillis()}")

        val backup = DeviceBackupEntity(
            id = backupId,
            deviceId = deviceId,
            romId = dev.romId,
            backupName = backupName,
            filePath = backupFile.absolutePath,
            sizeBytes = 14850000L,
            note = note,
            createdAt = System.currentTimeMillis()
        )
        db.deviceBackupDao().insertBackup(backup)
        logGuest(deviceId, "vbackup", "INFO", "Snapshot created successfully: $backupName")
        backup
    }

    suspend fun restoreBackup(backupId: String) = withContext(Dispatchers.IO) {
        // In full flow, restore DB partitions and storage items
        val backups = db.deviceBackupDao().getAllBackups()
        // Record log
    }

    suspend fun deleteBackup(backupId: String) = withContext(Dispatchers.IO) {
        db.deviceBackupDao().deleteBackupById(backupId)
    }

    suspend fun deleteDeviceSafely(deviceId: String) = withContext(Dispatchers.IO) {
        db.guestAppDao().deleteAppsForDevice(deviceId)
        db.guestStorageDao().clearStorageForDevice(deviceId)
        db.guestLogDao().clearLogsForDevice(deviceId)
        db.guestSettingDao().clearSettingsForDevice(deviceId)
        db.virtualDeviceDao().deleteDeviceById(deviceId)
    }

    suspend fun deleteRomSafely(romId: String) = withContext(Dispatchers.IO) {
        val rom = db.romDao().getRomById(romId)
        if (rom != null) {
            if (rom.zipPath.startsWith("/") && File(rom.zipPath).exists()) {
                File(rom.zipPath).delete()
            }
            db.romDao().deleteRomById(romId)
        }
    }

    suspend fun insertGuestStorageItem(item: GuestStorageItemEntity) = db.guestStorageDao().insertItem(item)
    suspend fun deleteGuestStorageItem(deviceId: String, path: String) = db.guestStorageDao().deleteItemByPath(deviceId, path)
    suspend fun updateGuestSetting(setting: GuestSettingEntity) = db.guestSettingDao().insertSetting(setting)

    suspend fun updateDeviceHardwarePermission(deviceId: String, feature: String, allowed: Boolean) = withContext(Dispatchers.IO) {
        when (feature) {
            "camera" -> db.virtualDeviceDao().updateCameraAllowed(deviceId, allowed)
            "location" -> db.virtualDeviceDao().updateLocationAllowed(deviceId, allowed)
            "network" -> db.virtualDeviceDao().updateNetworkAllowed(deviceId, allowed)
            "battery" -> db.virtualDeviceDao().updateBatteryAllowed(deviceId, allowed)
            "sensors" -> db.virtualDeviceDao().updateSensorsAllowed(deviceId, allowed)
        }
        logGuest(deviceId, "vhal", "INFO", "Hardware policy modified: $feature => ${if (allowed) "GRANTED" else "DENIED"}")
    }
}

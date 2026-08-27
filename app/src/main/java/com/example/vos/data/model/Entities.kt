package com.example.vos.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roms")
data class RomEntity(
    @PrimaryKey val id: String,
    val name: String,
    val version: String,
    val architecture: String, // e.g. "ARM64-v8a", "x86_64", "Generic AOSP"
    val osType: String, // "Android", "LineageOS", "Linux", "GSI"
    val fileName: String,
    val fileSize: Long,
    val zipPath: String,
    val isDefault: Boolean = false,
    val status: String = "READY", // "READY", "IMPORTING", "CORRUPTED"
    val buildPropsJson: String = "{}",
    val securityPatch: String = "2024-08-01",
    val apiLevel: Int = 34,
    val iconColorHex: Long = 0xFF00E5FF,
    val description: String = "",
    val isValid: Boolean = true,
    val validationStatus: String = "VALID", // "VALID", "INCOMPATIBLE_ARCH", "MISSING_BOOT_COMPONENTS", "CORRUPTED_ARCHIVE", "UNSUPPORTED_FORMAT"
    val validationSummary: String = "All essential bootloader components, kernel images, and CPU architecture validated.",
    val hasBootImage: Boolean = true,
    val hasSystemPartition: Boolean = true,
    val isArchCompatible: Boolean = true,
    val bootComponentFound: String = "boot.img (Verified)",
    val systemComponentFound: String = "system.img / rootfs (Ext4 Verified)",
    val detectedFilesCount: Int = 12,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "virtual_devices")
data class VirtualDeviceEntity(
    @PrimaryKey val id: String,
    val romId: String,
    val name: String,
    val state: String = "OFF", // "OFF", "BOOTING", "RUNNING", "PAUSED", "ERROR"
    val ramMb: Int = 3072, // 3 GB
    val storageMb: Int = 16384, // 16 GB
    val cpuCores: Int = 4,
    val virtualImei: String = "869402051284920",
    val virtualMac: String = "02:00:00:1A:2B:3C",
    val resolutionWidth: Int = 1080,
    val resolutionHeight: Int = 2400,
    val densityDpi: Int = 420,
    val isDefaultDevice: Boolean = false,
    val guestAndroidVersion: String = "14.0",
    val guestKernelVersion: String = "6.1.42-vos-android+",
    val cameraAllowed: Boolean = true,
    val locationAllowed: Boolean = true,
    val networkAllowed: Boolean = true,
    val batteryAllowed: Boolean = true,
    val sensorsAllowed: Boolean = true,
    val lastBootTime: Long = 0L,
    val uptimeSeconds: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "device_backups")
data class DeviceBackupEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val romId: String,
    val backupName: String,
    val filePath: String,
    val sizeBytes: Long,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "guest_apps")
data class GuestAppEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val packageName: String,
    val appName: String,
    val versionName: String = "1.0",
    val isSystemApp: Boolean = true,
    val iconType: String = "default",
    val isRunning: Boolean = false,
    val installedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "guest_storage_items")
data class GuestStorageItemEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val path: String, // e.g. "/sdcard/Documents/note.txt"
    val name: String,
    val isDirectory: Boolean = false,
    val sizeBytes: Long = 0L,
    val contentText: String = "",
    val mimeType: String = "text/plain",
    val modifiedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "guest_logs")
data class GuestLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val tag: String,
    val level: String, // "DEBUG", "INFO", "WARN", "ERROR"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "guest_settings")
data class GuestSettingEntity(
    @PrimaryKey val id: String, // deviceId + "_" + key
    val deviceId: String,
    val key: String,
    val value: String,
    val category: String = "system"
)

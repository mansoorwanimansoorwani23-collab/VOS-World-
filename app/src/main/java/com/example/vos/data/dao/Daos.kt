package com.example.vos.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vos.data.model.DeviceBackupEntity
import com.example.vos.data.model.GuestAppEntity
import com.example.vos.data.model.GuestLogEntity
import com.example.vos.data.model.GuestSettingEntity
import com.example.vos.data.model.GuestStorageItemEntity
import com.example.vos.data.model.RomEntity
import com.example.vos.data.model.VirtualDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RomDao {
    @Query("SELECT * FROM roms ORDER BY createdAt DESC")
    fun getAllRoms(): Flow<List<RomEntity>>

    @Query("SELECT * FROM roms WHERE id = :id")
    suspend fun getRomById(id: String): RomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRom(rom: RomEntity)

    @Update
    suspend fun updateRom(rom: RomEntity)

    @Delete
    suspend fun deleteRom(rom: RomEntity)

    @Query("DELETE FROM roms WHERE id = :id")
    suspend fun deleteRomById(id: String)
}

@Dao
interface VirtualDeviceDao {
    @Query("SELECT * FROM virtual_devices ORDER BY createdAt DESC")
    fun getAllDevices(): Flow<List<VirtualDeviceEntity>>

    @Query("SELECT * FROM virtual_devices WHERE id = :id")
    suspend fun getDeviceById(id: String): VirtualDeviceEntity?

    @Query("SELECT * FROM virtual_devices WHERE id = :id")
    fun getDeviceFlow(id: String): Flow<VirtualDeviceEntity?>

    @Query("SELECT * FROM virtual_devices WHERE isDefaultDevice = 1 LIMIT 1")
    fun getDefaultDevice(): Flow<VirtualDeviceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: VirtualDeviceEntity)

    @Update
    suspend fun updateDevice(device: VirtualDeviceEntity)

    @Query("UPDATE virtual_devices SET state = :state WHERE id = :id")
    suspend fun updateDeviceState(id: String, state: String)

    @Query("UPDATE virtual_devices SET isDefaultDevice = (CASE WHEN id = :selectedId THEN 1 ELSE 0 END)")
    suspend fun setDefaultDevice(selectedId: String)

    @Query("UPDATE virtual_devices SET cameraAllowed = :cameraAllowed, locationAllowed = :locationAllowed, networkAllowed = :networkAllowed, batteryAllowed = :batteryAllowed, sensorsAllowed = :sensorsAllowed WHERE id = :id")
    suspend fun updateHardwarePermissions(id: String, cameraAllowed: Boolean, locationAllowed: Boolean, networkAllowed: Boolean, batteryAllowed: Boolean, sensorsAllowed: Boolean)

    @Query("UPDATE virtual_devices SET cameraAllowed = :allowed WHERE id = :id")
    suspend fun updateCameraAllowed(id: String, allowed: Boolean)

    @Query("UPDATE virtual_devices SET locationAllowed = :allowed WHERE id = :id")
    suspend fun updateLocationAllowed(id: String, allowed: Boolean)

    @Query("UPDATE virtual_devices SET networkAllowed = :allowed WHERE id = :id")
    suspend fun updateNetworkAllowed(id: String, allowed: Boolean)

    @Query("UPDATE virtual_devices SET batteryAllowed = :allowed WHERE id = :id")
    suspend fun updateBatteryAllowed(id: String, allowed: Boolean)

    @Query("UPDATE virtual_devices SET sensorsAllowed = :allowed WHERE id = :id")
    suspend fun updateSensorsAllowed(id: String, allowed: Boolean)

    @Delete
    suspend fun deleteDevice(device: VirtualDeviceEntity)

    @Query("DELETE FROM virtual_devices WHERE id = :id")
    suspend fun deleteDeviceById(id: String)
}

@Dao
interface DeviceBackupDao {
    @Query("SELECT * FROM device_backups WHERE deviceId = :deviceId ORDER BY createdAt DESC")
    fun getBackupsForDevice(deviceId: String): Flow<List<DeviceBackupEntity>>

    @Query("SELECT * FROM device_backups ORDER BY createdAt DESC")
    fun getAllBackups(): Flow<List<DeviceBackupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackup(backup: DeviceBackupEntity)

    @Delete
    suspend fun deleteBackup(backup: DeviceBackupEntity)

    @Query("DELETE FROM device_backups WHERE id = :id")
    suspend fun deleteBackupById(id: String)
}

@Dao
interface GuestAppDao {
    @Query("SELECT * FROM guest_apps WHERE deviceId = :deviceId ORDER BY isSystemApp DESC, appName ASC")
    fun getAppsForDevice(deviceId: String): Flow<List<GuestAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<GuestAppEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: GuestAppEntity)

    @Delete
    suspend fun deleteApp(app: GuestAppEntity)

    @Query("DELETE FROM guest_apps WHERE deviceId = :deviceId")
    suspend fun deleteAppsForDevice(deviceId: String)
}

@Dao
interface GuestStorageDao {
    @Query("SELECT * FROM guest_storage_items WHERE deviceId = :deviceId AND path LIKE :parentPath || '%' ORDER BY isDirectory DESC, name ASC")
    fun getItemsInDirectory(deviceId: String, parentPath: String): Flow<List<GuestStorageItemEntity>>

    @Query("SELECT * FROM guest_storage_items WHERE deviceId = :deviceId ORDER BY path ASC")
    fun getAllItemsForDevice(deviceId: String): Flow<List<GuestStorageItemEntity>>

    @Query("SELECT * FROM guest_storage_items WHERE deviceId = :deviceId AND path = :path")
    suspend fun getItemByPath(deviceId: String, path: String): GuestStorageItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: GuestStorageItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<GuestStorageItemEntity>)

    @Delete
    suspend fun deleteItem(item: GuestStorageItemEntity)

    @Query("DELETE FROM guest_storage_items WHERE deviceId = :deviceId AND path = :path")
    suspend fun deleteItemByPath(deviceId: String, path: String)

    @Query("DELETE FROM guest_storage_items WHERE deviceId = :deviceId")
    suspend fun clearStorageForDevice(deviceId: String)
}

@Dao
interface GuestLogDao {
    @Query("SELECT * FROM guest_logs WHERE deviceId = :deviceId ORDER BY timestamp DESC LIMIT 300")
    fun getLogsForDevice(deviceId: String): Flow<List<GuestLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: GuestLogEntity)

    @Query("DELETE FROM guest_logs WHERE deviceId = :deviceId")
    suspend fun clearLogsForDevice(deviceId: String)
}

@Dao
interface GuestSettingDao {
    @Query("SELECT * FROM guest_settings WHERE deviceId = :deviceId")
    fun getSettingsForDevice(deviceId: String): Flow<List<GuestSettingEntity>>

    @Query("SELECT * FROM guest_settings WHERE deviceId = :deviceId AND `key` = :key")
    suspend fun getSetting(deviceId: String, key: String): GuestSettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: GuestSettingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: List<GuestSettingEntity>)

    @Query("DELETE FROM guest_settings WHERE deviceId = :deviceId")
    suspend fun clearSettingsForDevice(deviceId: String)
}

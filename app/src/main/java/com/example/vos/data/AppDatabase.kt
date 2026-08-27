package com.example.vos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vos.data.dao.DeviceBackupDao
import com.example.vos.data.dao.GuestAppDao
import com.example.vos.data.dao.GuestLogDao
import com.example.vos.data.dao.GuestSettingDao
import com.example.vos.data.dao.GuestStorageDao
import com.example.vos.data.dao.RomDao
import com.example.vos.data.dao.VirtualDeviceDao
import com.example.vos.data.model.DeviceBackupEntity
import com.example.vos.data.model.GuestAppEntity
import com.example.vos.data.model.GuestLogEntity
import com.example.vos.data.model.GuestSettingEntity
import com.example.vos.data.model.GuestStorageItemEntity
import com.example.vos.data.model.RomEntity
import com.example.vos.data.model.VirtualDeviceEntity

@Database(
    entities = [
        RomEntity::class,
        VirtualDeviceEntity::class,
        DeviceBackupEntity::class,
        GuestAppEntity::class,
        GuestStorageItemEntity::class,
        GuestLogEntity::class,
        GuestSettingEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun romDao(): RomDao
    abstract fun virtualDeviceDao(): VirtualDeviceDao
    abstract fun deviceBackupDao(): DeviceBackupDao
    abstract fun guestAppDao(): GuestAppDao
    abstract fun guestStorageDao(): GuestStorageDao
    abstract fun guestLogDao(): GuestLogDao
    abstract fun guestSettingDao(): GuestSettingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vos_world_database.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

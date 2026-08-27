package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.vos.data.AppDatabase
import com.example.vos.data.VosRepository
import com.example.vos.data.model.RomEntity
import com.example.vos.engine.HardwareTelemetry
import com.example.vos.engine.RomParser
import com.example.vos.engine.VirtualHal
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("VOS World", appName)
    }

    @Test
    fun `test repository initialization and rom creation`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val repository = VosRepository(context, db)

        repository.initializePreloadedRomsIfEmpty()
        val rom = repository.getRomById("rom_lineage_21")
        assertNotNull(rom)
        assertEquals("LineageOS 21.0 Micro", rom?.name)
        assertTrue(rom!!.isValid)
        assertTrue(rom.hasBootImage)
        assertTrue(rom.hasSystemPartition)

        val dev = repository.createVirtualDeviceForRom(rom, "Test Pixel 8 Pro")
        assertNotNull(dev)
        assertEquals("Test Pixel 8 Pro", dev.name)
        assertEquals(3072, dev.ramMb)
    }

    @Test
    fun `test virtual hal telemetry and permission enforcement`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val hal = VirtualHal(context)
        hal.refreshBatteryAndNetwork()

        val telemetry = hal.telemetry.value
        assertTrue(telemetry.batteryLevel in 0..100)
        assertNotNull(telemetry.hostAbi)

        // Test permission toggles
        hal.toggleBridgeFeature("camera", false)
        assertFalse(hal.bridgeStatus.value.cameraPassthrough)
        hal.toggleBridgeFeature("location", false)
        assertFalse(hal.bridgeStatus.value.locationBridge)
        hal.toggleBridgeFeature("network", false)
        assertFalse(hal.bridgeStatus.value.networkBridge)
    }

    @Test
    fun `test rom validation architecture compatibility check`() {
        val isCompatible = RomParser.isArchitectureCompatible("arm64-v8a", listOf("arm64-v8a", "armeabi-v7a"))
        assertTrue(isCompatible)

        val isX86Compatible = RomParser.isArchitectureCompatible("x86_64", listOf("arm64-v8a"))
        assertFalse(isX86Compatible)
    }

    @Test
    fun `test hardware permission update in repository`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val repository = VosRepository(context, db)

        repository.initializePreloadedRomsIfEmpty()
        val rom = repository.getRomById("rom_lineage_21")!!
        val dev = repository.createVirtualDeviceForRom(rom, "Hardware Test Device")

        repository.updateDeviceHardwarePermission(dev.id, "camera", false)
        val updatedDev = repository.getDeviceById(dev.id)
        assertNotNull(updatedDev)
        assertFalse(updatedDev!!.cameraAllowed)
    }
}


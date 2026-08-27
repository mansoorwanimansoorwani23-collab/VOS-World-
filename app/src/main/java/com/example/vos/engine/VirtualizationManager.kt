package com.example.vos.engine

import android.content.Context
import com.example.vos.data.VosRepository
import com.example.vos.data.model.RomEntity
import com.example.vos.data.model.VirtualDeviceEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class VmRuntimeState {
    object Off : VmRuntimeState()
    data class Booting(val stage: Int, val progress: Float, val statusText: String) : VmRuntimeState()
    data class Running(val uptimeSeconds: Long, val cpuPercent: Int, val ramUsedMb: Int) : VmRuntimeState()
    object Paused : VmRuntimeState()
    data class Error(val message: String) : VmRuntimeState()
}

class VirtualizationManager(
    private val context: Context,
    private val repository: VosRepository,
    val hal: VirtualHal,
    private val scope: CoroutineScope
) {
    private val _activeDevice = MutableStateFlow<VirtualDeviceEntity?>(null)
    val activeDevice: StateFlow<VirtualDeviceEntity?> = _activeDevice.asStateFlow()

    private val _activeRom = MutableStateFlow<RomEntity?>(null)
    val activeRom: StateFlow<RomEntity?> = _activeRom.asStateFlow()

    private val _vmState = MutableStateFlow<VmRuntimeState>(VmRuntimeState.Off)
    val vmState: StateFlow<VmRuntimeState> = _vmState.asStateFlow()

    private val _activeApp = MutableStateFlow<String?>(null) // null means Launcher / Home
    val activeApp: StateFlow<String?> = _activeApp.asStateFlow()

    private val _runningApps = MutableStateFlow<List<String>>(emptyList())
    val runningApps: StateFlow<List<String>> = _runningApps.asStateFlow()

    private val _isControlCenterOpen = MutableStateFlow(false)
    val isControlCenterOpen: StateFlow<Boolean> = _isControlCenterOpen.asStateFlow()

    private var uptimeJob: Job? = null
    private var shellEngine: GuestShellEngine? = null

    fun getShellEngine(): GuestShellEngine? = shellEngine

    fun startVirtualDevice(device: VirtualDeviceEntity, rom: RomEntity) {
        if (_vmState.value is VmRuntimeState.Running && _activeDevice.value?.id == device.id) {
            return
        }

        _activeDevice.value = device
        _activeRom.value = rom
        
        // Sync hardware bridge permissions configured for this device
        hal.toggleBridgeFeature("camera", device.cameraAllowed)
        hal.toggleBridgeFeature("location", device.locationAllowed)
        hal.toggleBridgeFeature("network", device.networkAllowed)
        hal.toggleBridgeFeature("battery", device.batteryAllowed)
        hal.toggleBridgeFeature("sensors", device.sensorsAllowed)
        
        hal.startTelemetryMonitoring()

        scope.launch(Dispatchers.IO) {
            try {
                repository.updateDeviceState(device.id, "BOOTING")
                repository.logGuest(device.id, "bootloader", "INFO", "VOS Virtual Bootloader v2.4 initialized. ABI=${hal.telemetry.value.hostAbi}")
                repository.logGuest(
                    device.id,
                    "vhal",
                    "INFO",
                    "Hardware Sandbox Policies: Camera=${if (device.cameraAllowed) "BRIDGED" else "DENIED"}, Network=${if (device.networkAllowed) "BRIDGED" else "DENIED"}, GPS=${if (device.locationAllowed) "BRIDGED" else "DENIED"}, Battery=${if (device.batteryAllowed) "BRIDGED" else "RESTRICTED"}, Sensors=${if (device.sensorsAllowed) "BRIDGED" else "DENIED"}"
                )

                _vmState.value = VmRuntimeState.Booting(1, 0.15f, "Allocating Virtual Memory (${device.ramMb} MB) & Sandboxed Storage...")
                delay(350)

                repository.logGuest(device.id, "vhal", "INFO", "Mounted loop virtual block devices: /system (ro), /data (rw, sqlite-backed)")
                _vmState.value = VmRuntimeState.Booting(2, 0.35f, "Mounting ext4 partitions & setting up Virtual V-HAL...")
                delay(400)

                repository.logGuest(device.id, "kernel", "INFO", "Linux kernel ${device.guestKernelVersion} booted on ${device.cpuCores} virtual cores")
                _vmState.value = VmRuntimeState.Booting(3, 0.60f, "Starting Virtual Linux Kernel & Init services...")
                delay(450)

                repository.logGuest(device.id, "zygote", "INFO", "Preloading ${rom.osType} framework classes & starting SurfaceFlinger")
                _vmState.value = VmRuntimeState.Booting(4, 0.85f, "Initializing SystemServer & SurfaceFlinger display...")
                delay(400)

                repository.logGuest(device.id, "systemui", "INFO", "System UI workspace initialized for ${rom.name}")
                _vmState.value = VmRuntimeState.Booting(5, 1.0f, "Guest OS ready!")
                delay(200)

                repository.updateDeviceState(device.id, "RUNNING")
                shellEngine = GuestShellEngine(repository, device, rom.buildPropsJson)
                _vmState.value = VmRuntimeState.Running(0L, 18, (device.ramMb * 0.38).toInt())

                startUptimeTracker(device.id)
            } catch (e: Exception) {
                repository.updateDeviceState(device.id, "ERROR")
                _vmState.value = VmRuntimeState.Error(e.localizedMessage ?: "Boot failed")
            }
        }
    }

    private fun startUptimeTracker(deviceId: String) {
        uptimeJob?.cancel()
        uptimeJob = scope.launch(Dispatchers.Default) {
            var uptime = 0L
            while (_vmState.value is VmRuntimeState.Running) {
                delay(1000)
                uptime++
                val current = _vmState.value
                if (current is VmRuntimeState.Running) {
                    _vmState.value = current.copy(
                        uptimeSeconds = uptime,
                        cpuPercent = (12..35).random(),
                        ramUsedMb = (_activeDevice.value?.ramMb?.times(0.40) ?: 1024.0).toInt() + (0..150).random()
                    )
                }
            }
        }
    }

    fun pauseVirtualDevice() {
        if (_vmState.value is VmRuntimeState.Running) {
            _vmState.value = VmRuntimeState.Paused
            _activeDevice.value?.let {
                scope.launch { repository.updateDeviceState(it.id, "PAUSED") }
            }
        }
    }

    fun resumeVirtualDevice() {
        if (_vmState.value is VmRuntimeState.Paused) {
            _vmState.value = VmRuntimeState.Running(
                uptimeSeconds = 0L,
                cpuPercent = 18,
                ramUsedMb = (_activeDevice.value?.ramMb?.times(0.38) ?: 1024.0).toInt()
            )
            _activeDevice.value?.let {
                scope.launch {
                    repository.updateDeviceState(it.id, "RUNNING")
                    startUptimeTracker(it.id)
                }
            }
        }
    }

    fun stopVirtualDevice() {
        uptimeJob?.cancel()
        _activeDevice.value?.let {
            scope.launch {
                repository.logGuest(it.id, "init", "INFO", "Sending SIGTERM to all virtual processes. Unmounting partitions.")
                repository.updateDeviceState(it.id, "OFF")
            }
        }
        _vmState.value = VmRuntimeState.Off
        _activeApp.value = null
        _runningApps.value = emptyList()
        _isControlCenterOpen.value = false
        hal.stopTelemetryMonitoring()
    }

    fun restartVirtualDevice() {
        val dev = _activeDevice.value
        val rom = _activeRom.value
        if (dev != null && rom != null) {
            stopVirtualDevice()
            scope.launch {
                delay(500)
                startVirtualDevice(dev, rom)
            }
        }
    }

    fun launchGuestApp(packageName: String) {
        _activeApp.value = packageName
        _isControlCenterOpen.value = false
        val current = _runningApps.value.toMutableList()
        if (!current.contains(packageName)) {
            current.add(0, packageName)
        } else {
            current.remove(packageName)
            current.add(0, packageName)
        }
        _runningApps.value = current
        hal.triggerVibration(25)
    }

    fun closeActiveAppToHome() {
        _activeApp.value = null
        _isControlCenterOpen.value = false
        hal.triggerVibration(25)
    }

    fun closeApp(packageName: String) {
        val current = _runningApps.value.toMutableList()
        current.remove(packageName)
        _runningApps.value = current
        if (_activeApp.value == packageName) {
            _activeApp.value = current.firstOrNull()
        }
    }

    fun toggleControlCenter() {
        _isControlCenterOpen.value = !_isControlCenterOpen.value
        hal.triggerVibration(30)
    }

    fun setControlCenter(open: Boolean) {
        _isControlCenterOpen.value = open
    }
}

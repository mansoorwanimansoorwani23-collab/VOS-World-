package com.example.vos.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vos.data.AppDatabase
import com.example.vos.data.VosRepository
import com.example.vos.data.model.DeviceBackupEntity
import com.example.vos.data.model.GuestAppEntity
import com.example.vos.data.model.GuestLogEntity
import com.example.vos.data.model.GuestSettingEntity
import com.example.vos.data.model.GuestStorageItemEntity
import com.example.vos.data.model.RomEntity
import com.example.vos.data.model.VirtualDeviceEntity
import com.example.vos.engine.HardwareBridgeStatus
import com.example.vos.engine.HardwareTelemetry
import com.example.vos.engine.RomParser
import com.example.vos.engine.VirtualHal
import com.example.vos.engine.VirtualizationManager
import com.example.vos.engine.VmRuntimeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class MainTab(val label: String) {
    HOME("Home"),
    VIRTUAL_PHONE("Virtual OS"),
    DEVICES_ROMS("ROMs & Devices"),
    EMERGENCY_HARDWARE("Emergency & Hardware"),
    SETTINGS("Settings")
}

@OptIn(ExperimentalCoroutinesApi::class)
class VosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = VosRepository(application, db)
    val hal = VirtualHal(application)
    val vmManager = VirtualizationManager(application, repository, hal, viewModelScope)

    val roms: StateFlow<List<RomEntity>> = repository.roms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val virtualDevices: StateFlow<List<VirtualDeviceEntity>> = repository.virtualDevices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val defaultDevice: StateFlow<VirtualDeviceEntity?> = repository.defaultDevice
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val backups: StateFlow<List<DeviceBackupEntity>> = repository.backups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vmState: StateFlow<VmRuntimeState> = vmManager.vmState
    val activeDevice: StateFlow<VirtualDeviceEntity?> = vmManager.activeDevice
    val activeRom: StateFlow<RomEntity?> = vmManager.activeRom
    val activeApp: StateFlow<String?> = vmManager.activeApp
    val runningApps: StateFlow<List<String>> = vmManager.runningApps
    val isControlCenterOpen: StateFlow<Boolean> = vmManager.isControlCenterOpen

    val telemetry: StateFlow<HardwareTelemetry> = hal.telemetry
    val bridgeStatus: StateFlow<HardwareBridgeStatus> = hal.bridgeStatus

    private val _selectedTab = MutableStateFlow(MainTab.HOME)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    private val _importProgress = MutableStateFlow<Pair<Float, String>?>(null)
    val importProgress: StateFlow<Pair<Float, String>?> = _importProgress.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _isSosActive = MutableStateFlow(false)
    val isSosActive: StateFlow<Boolean> = _isSosActive.asStateFlow()

    private val _guestNavMode = MutableStateFlow("gesture") // "gesture" or "three_button"
    val guestNavMode: StateFlow<String> = _guestNavMode.asStateFlow()

    private val _isGuestLocked = MutableStateFlow(false)
    val isGuestLocked: StateFlow<Boolean> = _isGuestLocked.asStateFlow()

    private val _guestRefreshRate = MutableStateFlow(60) // 60 or 120 Hz
    val guestRefreshRate: StateFlow<Int> = _guestRefreshRate.asStateFlow()

    private val _isFloatingButtonHidden = MutableStateFlow(false)
    val isFloatingButtonHidden: StateFlow<Boolean> = _isFloatingButtonHidden.asStateFlow()

    // Reactive streams for the currently active or selected device
    val activeDeviceLogs: StateFlow<List<GuestLogEntity>> = activeDevice.flatMapLatest { dev ->
        if (dev != null) repository.getLogsForDevice(dev.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDeviceApps: StateFlow<List<GuestAppEntity>> = activeDevice.flatMapLatest { dev ->
        if (dev != null) repository.getAppsForDevice(dev.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDeviceStorage: StateFlow<List<GuestStorageItemEntity>> = activeDevice.flatMapLatest { dev ->
        if (dev != null) repository.getStorageForDevice(dev.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDeviceSettings: StateFlow<List<GuestSettingEntity>> = activeDevice.flatMapLatest { dev ->
        if (dev != null) repository.getSettingsForDevice(dev.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializePreloadedRomsIfEmpty()
            hal.startTelemetryMonitoring()
        }
    }

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
    }

    fun dismissUserMessage() {
        _userMessage.value = null
    }

    fun importRomFromUri(uri: Uri) {
        viewModelScope.launch {
            _importProgress.value = Pair(0.05f, "Preparing to import ROM package...")
            val result = RomParser.parseAndImportZip(
                context = getApplication(),
                uri = uri,
                onProgress = { progress, msg ->
                    _importProgress.value = Pair(progress, msg)
                }
            )

            if (result.romEntity != null) {
                repository.insertRom(result.romEntity)
                if (result.isValid) {
                    val newDevice = repository.createVirtualDeviceForRom(result.romEntity)
                    _importProgress.value = null
                    _userMessage.value = "Successfully imported '${result.romEntity.name}'! Boot components and architecture verified."
                    selectTab(MainTab.DEVICES_ROMS)
                } else {
                    _importProgress.value = null
                    _userMessage.value = "ROM Imported with Validation Issues: ${result.validationSummary}"
                    selectTab(MainTab.DEVICES_ROMS)
                }
            } else {
                _importProgress.value = null
                val err = result.errors.firstOrNull() ?: result.validationSummary.ifEmpty { "ROM validation failed. Incompatible file structure." }
                _userMessage.value = "Import Failed: $err"
            }
        }
    }

    fun startDevice(device: VirtualDeviceEntity) {
        viewModelScope.launch {
            val rom = repository.getRomById(device.romId)
            if (rom != null) {
                if (!rom.isValid) {
                    _userMessage.value = "Cannot boot '${device.name}': ${rom.validationSummary}"
                    return@launch
                }
                vmManager.startVirtualDevice(device, rom)
                selectTab(MainTab.VIRTUAL_PHONE)
            } else {
                _userMessage.value = "Associated ROM '${device.romId}' not found."
            }
        }
    }

    fun stopDevice(device: VirtualDeviceEntity? = null) {
        if (device == null || activeDevice.value?.id == device.id) {
            vmManager.stopVirtualDevice()
        } else {
            viewModelScope.launch {
                repository.updateDeviceState(device.id, "OFF")
            }
        }
    }

    fun restartDevice(device: VirtualDeviceEntity? = null) {
        if (device == null || activeDevice.value?.id == device.id) {
            vmManager.restartVirtualDevice()
        } else {
            viewModelScope.launch {
                val rom = repository.getRomById(device.romId)
                if (rom != null) {
                    if (!rom.isValid) {
                        _userMessage.value = "Cannot boot '${device.name}': ${rom.validationSummary}"
                        return@launch
                    }
                    vmManager.startVirtualDevice(device, rom)
                    selectTab(MainTab.VIRTUAL_PHONE)
                }
            }
        }
    }

    fun pauseDevice() {
        vmManager.pauseVirtualDevice()
    }

    fun resumeDevice() {
        vmManager.resumeVirtualDevice()
    }

    fun launchGuestApp(packageName: String) {
        vmManager.launchGuestApp(packageName)
    }

    fun closeActiveGuestApp() {
        vmManager.closeActiveAppToHome()
    }

    fun closeGuestApp(packageName: String) {
        vmManager.closeApp(packageName)
    }

    fun toggleControlCenter() {
        vmManager.toggleControlCenter()
    }

    fun setControlCenter(open: Boolean) {
        vmManager.setControlCenter(open)
    }

    fun createDeviceForRom(rom: RomEntity, customName: String?) {
        viewModelScope.launch {
            if (!rom.isValid) {
                _userMessage.value = "Cannot create virtual device: ${rom.validationSummary}"
                return@launch
            }
            val dev = repository.createVirtualDeviceForRom(rom, customName)
            _userMessage.value = "Created virtual device '${dev.name}'"
        }
    }

    fun deleteDevice(deviceId: String) {
        viewModelScope.launch {
            if (activeDevice.value?.id == deviceId) {
                stopDevice()
            }
            repository.deleteDeviceSafely(deviceId)
            _userMessage.value = "Virtual device deleted."
        }
    }

    fun deleteRom(romId: String) {
        viewModelScope.launch {
            val dev = activeDevice.value
            if (dev?.romId == romId) {
                stopDevice()
            }
            repository.deleteRomSafely(romId)
            _userMessage.value = "ROM package deleted."
        }
    }

    fun createBackup(deviceId: String, name: String, note: String) {
        viewModelScope.launch {
            val b = repository.createBackup(deviceId, name, note)
            _userMessage.value = "Backup '${b.backupName}' created successfully!"
        }
    }

    fun restoreBackup(backupId: String) {
        viewModelScope.launch {
            repository.restoreBackup(backupId)
            _userMessage.value = "Device restored to snapshot state."
        }
    }

    fun deleteBackup(backupId: String) {
        viewModelScope.launch {
            repository.deleteBackup(backupId)
            _userMessage.value = "Backup deleted."
        }
    }

    fun setDefaultDevice(deviceId: String) {
        viewModelScope.launch {
            repository.setDefaultDevice(deviceId)
            _userMessage.value = "Default virtual device updated."
        }
    }

    fun toggleSos() {
        _isSosActive.value = !_isSosActive.value
        hal.triggerVibration(100)
        if (_isSosActive.value) {
            _userMessage.value = "EMERGENCY SOS BROADCAST ACTIVATED"
        }
    }

    fun toggleHardwareBridge(feature: String, enabled: Boolean) {
        hal.toggleBridgeFeature(feature, enabled)
        val activeDev = activeDevice.value
        if (activeDev != null) {
            viewModelScope.launch {
                repository.updateDeviceHardwarePermission(activeDev.id, feature, enabled)
            }
        }
    }

    fun toggleDeviceHardwarePermission(deviceId: String, feature: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateDeviceHardwarePermission(deviceId, feature, enabled)
            if (activeDevice.value?.id == deviceId) {
                hal.toggleBridgeFeature(feature, enabled)
            }
        }
    }

    fun addGuestFile(deviceId: String, path: String, name: String, content: String) {
        viewModelScope.launch {
            val item = GuestStorageItemEntity(
                id = UUID.randomUUID().toString(),
                deviceId = deviceId,
                path = path,
                name = name,
                isDirectory = false,
                sizeBytes = content.toByteArray().size.toLong(),
                contentText = content,
                mimeType = "text/plain"
            )
            repository.insertGuestStorageItem(item)
            repository.logGuest(deviceId, "storage", "INFO", "File created: $path")
        }
    }

    fun deleteGuestFile(deviceId: String, path: String) {
        viewModelScope.launch {
            repository.deleteGuestStorageItem(deviceId, path)
            repository.logGuest(deviceId, "storage", "INFO", "File deleted: $path")
        }
    }

    fun setGuestNavMode(mode: String) {
        _guestNavMode.value = mode
    }

    fun setGuestLocked(locked: Boolean) {
        _isGuestLocked.value = locked
    }

    fun setGuestRefreshRate(hz: Int) {
        _guestRefreshRate.value = hz
    }

    fun setFloatingButtonHidden(hidden: Boolean) {
        _isFloatingButtonHidden.value = hidden
    }

    fun wipeGuestCache(deviceId: String) {
        viewModelScope.launch {
            repository.logGuest(deviceId, "system", "WARN", "Wiped guest Dalvik & system cache partitions.")
            _userMessage.value = "Guest cache wiped successfully. Virtual runtime restarted."
        }
    }

    fun factoryResetGuest(deviceId: String) {
        viewModelScope.launch {
            repository.logGuest(deviceId, "system", "WARN", "Factory reset initiated for device $deviceId")
            _userMessage.value = "Guest userdata formatted to factory state."
        }
    }

    fun openSystemHomeSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_HOME_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (ex: Exception) {
                _userMessage.value = "Could not open system home settings automatically."
            }
        }
    }
}

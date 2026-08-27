package com.example.vos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoAmber
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoEmergencyBg
import com.example.ui.theme.BentoEmergencyText
import com.example.ui.theme.BentoGreen
import com.example.ui.theme.BentoOnPrimary
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceElevated
import com.example.ui.theme.BentoTextMuted
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.vos.data.model.GuestAppEntity
import com.example.vos.data.model.GuestLogEntity
import com.example.vos.data.model.GuestStorageItemEntity
import com.example.vos.data.model.RomEntity
import com.example.vos.data.model.VirtualDeviceEntity
import com.example.vos.engine.HardwareTelemetry
import com.example.vos.engine.VmRuntimeState
import com.example.vos.ui.components.GuestBrowserApp
import com.example.vos.ui.components.GuestCalculatorApp
import com.example.vos.ui.components.GuestCameraApp
import com.example.vos.ui.components.GuestClockApp
import com.example.vos.ui.components.GuestFilesApp
import com.example.vos.ui.components.GuestGalleryApp
import com.example.vos.ui.components.GuestLauncherHome
import com.example.vos.ui.components.GuestLockScreen
import com.example.vos.ui.components.GuestNotesApp
import com.example.vos.ui.components.GuestPhoneApp
import com.example.vos.ui.components.GuestQuickSettingsPanel
import com.example.vos.ui.components.GuestRomNavigationBar
import com.example.vos.ui.components.GuestRomStatusBar
import com.example.vos.ui.components.GuestSettingsApp
import com.example.vos.ui.components.GuestTerminalApp
import com.example.vos.ui.components.VosFloatingControlOverlay
import com.example.vos.viewmodel.MainTab
import com.example.vos.viewmodel.VosViewModel

@Composable
fun VirtualPhoneScreen(
    viewModel: VosViewModel,
    activeDevice: VirtualDeviceEntity?,
    activeRom: RomEntity?,
    devices: List<VirtualDeviceEntity>,
    vmState: VmRuntimeState,
    telemetry: HardwareTelemetry,
    activeApp: String?,
    runningApps: List<String>,
    guestApps: List<GuestAppEntity>,
    guestStorage: List<GuestStorageItemEntity>,
    guestLogs: List<GuestLogEntity>,
    isControlCenterOpen: Boolean,
    onNavigateTab: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var isRecentsOverviewOpen by remember { mutableStateOf(false) }

    val guestNavMode by viewModel.guestNavMode.collectAsState()
    val isGuestLocked by viewModel.isGuestLocked.collectAsState()
    val guestRefreshRate by viewModel.guestRefreshRate.collectAsState()
    val bridgeStatus by viewModel.bridgeStatus.collectAsState()
    val backups by viewModel.backups.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
    ) {
        when (vmState) {
            is VmRuntimeState.Off -> {
                // Device Offline Standby Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BentoBackground)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(BentoSurfaceElevated)
                            .border(1.dp, BentoBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Off",
                            tint = BentoTextSecondary,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "HYPERVISOR L3 • STANDBY",
                        color = BentoPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Virtual Device Powered Off",
                        color = BentoTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Select a virtual instance below to boot into user-space sandbox",
                        color = BentoTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    // Device Selection Bento Cards
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(devices) { dev ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.startDevice(dev) }
                                    .border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(BentoPrimaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Smartphone, contentDescription = "Device", tint = BentoPrimary, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(dev.name, color = BentoTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text("${dev.cpuCores} Cores • ${dev.ramMb}MB RAM", color = BentoTextSecondary, fontSize = 11.sp)
                                        }
                                    }

                                    Button(
                                        onClick = { viewModel.startDevice(dev) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = BentoPrimary,
                                            contentColor = BentoOnPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Boot OS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is VmRuntimeState.Booting -> {
                // Hypervisor Virtual Bootloader Sequence
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BentoBackground)
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = activeRom?.name ?: "VOS Virtual Bootloader",
                        color = BentoTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "STAGE ${(vmState as VmRuntimeState.Booting).stage} OF 5 • GUEST KERNEL INIT",
                        color = BentoPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(BentoSurface)
                            .border(1.dp, BentoBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { (vmState as VmRuntimeState.Booting).progress },
                            color = BentoPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(76.dp)
                        )
                        Icon(Icons.Default.Smartphone, contentDescription = "Booting", tint = BentoPrimary, modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = (vmState as VmRuntimeState.Booting).statusText,
                        color = BentoTextPrimary,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { (vmState as VmRuntimeState.Booting).progress },
                        color = BentoPrimary,
                        trackColor = BentoSurfaceElevated,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                }
            }

            is VmRuntimeState.Running, is VmRuntimeState.Paused -> {
                val currentDevice = activeDevice ?: devices.firstOrNull()
                val currentRom = activeRom ?: RomEntity(
                    id = "unknown",
                    name = "Generic Guest OS",
                    version = "14.0",
                    architecture = "ARM64",
                    osType = "Android",
                    fileName = "os.zip",
                    fileSize = 0L,
                    zipPath = ""
                )

                if (currentDevice != null) {
                    // FULL SCREEN GUEST OS VIEWPORT (ROM CONTROLS ENTIRE SCREEN)
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // 1. Guest ROM Status Bar (Carrier, Time, Wifi, Battery)
                            GuestRomStatusBar(
                                telemetry = telemetry,
                                carrierName = if (currentRom.name.contains("Lineage", true)) "LineageOS" else if (currentRom.name.contains("Pixel", true)) "Pixel Net" else "V-Cellular",
                                onPullDownQuickSettings = { viewModel.setControlCenter(true) }
                            )

                            // 2. Guest ROM Content Area
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                if (isGuestLocked) {
                                    // Guest Lock Screen
                                    GuestLockScreen(
                                        romName = currentRom.name,
                                        onUnlock = { viewModel.setGuestLocked(false) }
                                    )
                                } else if (isRecentsOverviewOpen) {
                                    // Guest Task Switcher Overview
                                    RecentsTaskSwitcher(
                                        runningApps = runningApps,
                                        allApps = guestApps,
                                        onSelectApp = { pkg ->
                                            viewModel.launchGuestApp(pkg)
                                            isRecentsOverviewOpen = false
                                        },
                                        onCloseApp = { pkg -> viewModel.closeGuestApp(pkg) },
                                        onClearAll = {
                                            runningApps.forEach { viewModel.closeGuestApp(it) }
                                            isRecentsOverviewOpen = false
                                        }
                                    )
                                } else if (activeApp == null) {
                                    // Guest OS Launcher Home
                                    GuestLauncherHome(
                                        device = currentDevice,
                                        rom = currentRom,
                                        apps = guestApps,
                                        onLaunchApp = { pkg -> viewModel.launchGuestApp(pkg) }
                                    )
                                } else {
                                    // Active Guest App
                                    when (activeApp) {
                                        "com.android.settings" -> GuestSettingsApp(
                                            device = currentDevice,
                                            rom = currentRom,
                                            telemetry = telemetry,
                                            onBack = { viewModel.closeActiveGuestApp() }
                                        )
                                        "com.android.terminal" -> GuestTerminalApp(
                                            shellEngine = viewModel.vmManager.getShellEngine(),
                                            onBack = { viewModel.closeActiveGuestApp() }
                                        )
                                        "com.android.documentsui" -> GuestFilesApp(
                                            storageItems = guestStorage,
                                            onAddFile = { path, name, content -> viewModel.addGuestFile(currentDevice.id, path, name, content) },
                                            onDeleteFile = { path -> viewModel.deleteGuestFile(currentDevice.id, path) },
                                            onBack = { viewModel.closeActiveGuestApp() }
                                        )
                                        "com.android.camera2" -> GuestCameraApp(
                                            onBack = { viewModel.closeActiveGuestApp() }
                                        )
                                        "org.chromium.browser" -> GuestBrowserApp(
                                            onBack = { viewModel.closeActiveGuestApp() }
                                        )
                                        "com.android.dialer" -> GuestPhoneApp(
                                            onBack = { viewModel.closeActiveGuestApp() }
                                        )
                                        "com.android.gallery3d" -> GuestGalleryApp(
                                            onBack = { viewModel.closeActiveGuestApp() }
                                        )
                                        "com.android.calculator2" -> GuestCalculatorApp(
                                            onBack = { viewModel.closeActiveGuestApp() }
                                        )
                                        "com.android.deskclock" -> GuestClockApp(
                                            onBack = { viewModel.closeActiveGuestApp() }
                                        )
                                        "com.android.notes" -> GuestNotesApp(
                                            onBack = { viewModel.closeActiveGuestApp() }
                                        )
                                        else -> GuestSettingsApp(
                                            device = currentDevice,
                                            rom = currentRom,
                                            telemetry = telemetry,
                                            onBack = { viewModel.closeActiveGuestApp() }
                                        )
                                    }
                                }
                            }

                            // 3. Guest ROM Navigation Bar (Gesture Pill or 3-Button Bar)
                            GuestRomNavigationBar(
                                navMode = guestNavMode,
                                onBack = {
                                    if (isControlCenterOpen) {
                                        viewModel.setControlCenter(false)
                                    } else if (isRecentsOverviewOpen) {
                                        isRecentsOverviewOpen = false
                                    } else if (activeApp != null) {
                                        viewModel.closeActiveGuestApp()
                                    }
                                },
                                onHome = {
                                    viewModel.setControlCenter(false)
                                    isRecentsOverviewOpen = false
                                    viewModel.closeActiveGuestApp()
                                },
                                onRecents = {
                                    viewModel.setControlCenter(false)
                                    isRecentsOverviewOpen = !isRecentsOverviewOpen
                                }
                            )
                        }

                        // 4. Guest Notification & Quick Settings Shade
                        GuestQuickSettingsPanel(
                            isOpen = isControlCenterOpen,
                            onClose = { viewModel.setControlCenter(false) },
                            telemetry = telemetry,
                            onOpenSettingsApp = {
                                viewModel.launchGuestApp("com.android.settings")
                            },
                            onRestartDevice = { viewModel.restartDevice() },
                            onStopDevice = { viewModel.stopDevice() }
                        )

                        // 5. VOS WORLD FLOATING BUTTON OVERLAY (Host Hypervisor Manager)
                        VosFloatingControlOverlay(
                            device = currentDevice,
                            rom = currentRom,
                            vmState = vmState,
                            telemetry = telemetry,
                            bridgeStatus = bridgeStatus,
                            backups = backups,
                            guestNavMode = guestNavMode,
                            guestRefreshRate = guestRefreshRate,
                            isGuestLocked = isGuestLocked,
                            onToggleHardwareBridge = { feature, enabled ->
                                viewModel.toggleHardwareBridge(feature, enabled)
                            },
                            onRestart = { viewModel.restartDevice() },
                            onStop = { viewModel.stopDevice() },
                            onPauseResume = {
                                if (vmState is VmRuntimeState.Paused) viewModel.resumeDevice() else viewModel.pauseDevice()
                            },
                            onCreateBackup = { name, note ->
                                viewModel.createBackup(currentDevice.id, name, note)
                            },
                            onRestoreBackup = { backupId ->
                                viewModel.restoreBackup(backupId)
                            },
                            onDeleteBackup = { backupId ->
                                viewModel.deleteBackup(backupId)
                            },
                            onWipeCache = {
                                viewModel.wipeGuestCache(currentDevice.id)
                            },
                            onFactoryReset = {
                                viewModel.factoryResetGuest(currentDevice.id)
                            },
                            onSetNavMode = { mode ->
                                viewModel.setGuestNavMode(mode)
                            },
                            onSetRefreshRate = { hz ->
                                viewModel.setGuestRefreshRate(hz)
                            },
                            onSetLocked = { locked ->
                                viewModel.setGuestLocked(locked)
                            },
                            onExitToHost = {
                                onNavigateTab(MainTab.HOME)
                            }
                        )
                    }
                }
            }

            is VmRuntimeState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BentoBackground)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Virtual Machine Boot Error", color = BentoEmergencyText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (vmState as VmRuntimeState.Error).message,
                        color = BentoEmergencyText,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.stopDevice() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BentoSurfaceElevated,
                            contentColor = BentoTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Dismiss & Reset")
                    }
                }
            }
        }
    }
}

@Composable
fun RecentsTaskSwitcher(
    runningApps: List<String>,
    allApps: List<GuestAppEntity>,
    onSelectApp: (String) -> Unit,
    onCloseApp: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A).copy(alpha = 0.96f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Apps", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            if (runningApps.isNotEmpty()) {
                Text(
                    text = "Clear All",
                    color = Color(0xFF38BDF8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onClearAll() }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (runningApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No recent apps in memory", color = Color(0xFF64748B), fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(runningApps) { pkg ->
                    val appEntity = allApps.find { it.packageName == pkg }
                    val appName = appEntity?.appName ?: pkg.substringAfterLast('.')

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                            .clickable { onSelectApp(pkg) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(appName, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(pkg, color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                            IconButton(onClick = { onCloseApp(pkg) }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                            }
                        }
                    }
                }
            }
        }
    }
}

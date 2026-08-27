package com.example.vos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoActivePill
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
import com.example.vos.data.model.DeviceBackupEntity
import com.example.vos.data.model.RomEntity
import com.example.vos.data.model.VirtualDeviceEntity
import com.example.vos.engine.HardwareBridgeStatus
import com.example.vos.engine.HardwareTelemetry
import com.example.vos.engine.VmRuntimeState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VosFloatingControlOverlay(
    device: VirtualDeviceEntity,
    rom: RomEntity,
    vmState: VmRuntimeState,
    telemetry: HardwareTelemetry,
    bridgeStatus: HardwareBridgeStatus,
    backups: List<DeviceBackupEntity>,
    guestNavMode: String,
    guestRefreshRate: Int,
    isGuestLocked: Boolean,
    onToggleHardwareBridge: (String, Boolean) -> Unit,
    onRestart: () -> Unit,
    onStop: () -> Unit,
    onPauseResume: () -> Unit,
    onCreateBackup: (String, String) -> Unit,
    onRestoreBackup: (String) -> Unit,
    onDeleteBackup: (String) -> Unit,
    onWipeCache: () -> Unit,
    onFactoryReset: () -> Unit,
    onSetNavMode: (String) -> Unit,
    onSetRefreshRate: (Int) -> Unit,
    onSetLocked: (Boolean) -> Unit,
    onExitToHost: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isControlPanelOpen by remember { mutableStateOf(false) }
    // Display modes: 0 = sleek capsule pill, 1 = tiny edge dot (26dp), 2 = completely hidden
    var overlayDisplayMode by remember { mutableIntStateOf(0) }

    // Draggable position coordinates
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(120f) }

    Box(modifier = modifier.fillMaxSize()) {
        if (overlayDisplayMode != 2) {
            // Floating Button Anchor
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount.x).coerceIn(-900f, 0f)
                            offsetY = (offsetY + dragAmount.y).coerceIn(20f, 2000f)
                        }
                    }
                    .testTag("vos_world_floating_button")
            ) {
                if (overlayDisplayMode == 0) {
                    // Sleek, minimal floating pill
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = BentoSurface.copy(alpha = 0.88f),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .border(1.dp, BentoBorder.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                            .clickable { isControlPanelOpen = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            // Live Hypervisor LED
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (vmState is VmRuntimeState.Running) BentoGreen else BentoAmber)
                            )

                            Text(
                                text = "VOS",
                                color = BentoPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            // Quick minimize to dot icon
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .clip(CircleShape)
                                    .background(BentoSurfaceElevated)
                                    .clickable { overlayDisplayMode = 1 },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Minimize Floating Pill",
                                    tint = BentoTextSecondary,
                                    modifier = Modifier.size(9.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Ultra-compact mini disc (26dp)
                    Surface(
                        shape = CircleShape,
                        color = BentoSurface.copy(alpha = 0.82f),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .size(26.dp)
                            .border(1.dp, BentoPrimary.copy(alpha = 0.6f), CircleShape)
                            .clickable { isControlPanelOpen = true }
                            .testTag("vos_floating_pill_collapsed")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (vmState is VmRuntimeState.Running) BentoGreen else BentoAmber)
                            )
                        }
                    }
                }
            }
        } else {
            // Discreet edge tab to unhide
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(width = 6.dp, height = 48.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                    .background(BentoPrimary.copy(alpha = 0.5f))
                    .clickable { overlayDisplayMode = 0 }
            )
        }

        // VOS World Full Control Panel (Host Overlay)
        if (isControlPanelOpen) {
            VosWorldControlPanelSheet(
                device = device,
                rom = rom,
                vmState = vmState,
                telemetry = telemetry,
                bridgeStatus = bridgeStatus,
                backups = backups,
                guestNavMode = guestNavMode,
                guestRefreshRate = guestRefreshRate,
                isGuestLocked = isGuestLocked,
                onDismiss = { isControlPanelOpen = false },
                onToggleHardwareBridge = onToggleHardwareBridge,
                onRestart = onRestart,
                onStop = onStop,
                onPauseResume = onPauseResume,
                onCreateBackup = onCreateBackup,
                onRestoreBackup = onRestoreBackup,
                onDeleteBackup = onDeleteBackup,
                onWipeCache = onWipeCache,
                onFactoryReset = onFactoryReset,
                onSetNavMode = onSetNavMode,
                onSetRefreshRate = onSetRefreshRate,
                onSetLocked = onSetLocked,
                onExitToHost = onExitToHost,
                onMinimizeButton = { mode ->
                    overlayDisplayMode = mode
                    isControlPanelOpen = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VosWorldControlPanelSheet(
    device: VirtualDeviceEntity,
    rom: RomEntity,
    vmState: VmRuntimeState,
    telemetry: HardwareTelemetry,
    bridgeStatus: HardwareBridgeStatus,
    backups: List<DeviceBackupEntity>,
    guestNavMode: String,
    guestRefreshRate: Int,
    isGuestLocked: Boolean,
    onDismiss: () -> Unit,
    onToggleHardwareBridge: (String, Boolean) -> Unit,
    onRestart: () -> Unit,
    onStop: () -> Unit,
    onPauseResume: () -> Unit,
    onCreateBackup: (String, String) -> Unit,
    onRestoreBackup: (String) -> Unit,
    onDeleteBackup: (String) -> Unit,
    onWipeCache: () -> Unit,
    onFactoryReset: () -> Unit,
    onSetNavMode: (String) -> Unit,
    onSetRefreshRate: (Int) -> Unit,
    onSetLocked: (Boolean) -> Unit,
    onExitToHost: () -> Unit,
    onMinimizeButton: (Int) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedSection by remember { mutableIntStateOf(0) }

    val sections = listOf(
        Pair("Device Settings", Icons.Default.Settings),
        Pair("ROM Info", Icons.Default.Info),
        Pair("Lifecycle", Icons.Default.PowerSettingsNew),
        Pair("Backups", Icons.Default.Backup),
        Pair("Storage", Icons.Default.Storage),
        Pair("Display & Perf", Icons.Default.DisplaySettings),
        Pair("Exit", Icons.Default.ExitToApp)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BentoBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(48.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(BentoBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("vos_world_control_panel")
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VOS WORLD HOST HYPERVISOR",
                        color = BentoPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Guest OS Control Panel",
                        color = BentoTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BentoSurface)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = BentoTextPrimary, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedSection,
                containerColor = BentoBackground,
                contentColor = BentoPrimary,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                        color = BentoPrimary,
                        height = 2.dp
                    )
                },
                divider = { Divider(color = BentoBorder.copy(alpha = 0.5f)) }
            ) {
                sections.forEachIndexed { index, (title, icon) ->
                    Tab(
                        selected = selectedSection == index,
                        onClick = { selectedSection = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(icon, contentDescription = title, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(title, fontSize = 12.sp, fontWeight = if (selectedSection == index) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Active Tab Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedSection) {
                    0 -> VirtualDeviceSettingsTab(
                        device = device,
                        telemetry = telemetry,
                        bridgeStatus = bridgeStatus,
                        onToggleHardwareBridge = onToggleHardwareBridge
                    )
                    1 -> RomInfoTab(rom = rom, telemetry = telemetry)
                    2 -> LifecycleControlsTab(
                        vmState = vmState,
                        onRestart = { onRestart(); onDismiss() },
                        onStop = { onStop(); onDismiss() },
                        onPauseResume = onPauseResume
                    )
                    3 -> BackupRestoreTab(
                        device = device,
                        backups = backups,
                        onCreateBackup = onCreateBackup,
                        onRestoreBackup = { onRestoreBackup(it); onDismiss() },
                        onDeleteBackup = onDeleteBackup
                    )
                    4 -> StorageManagementTab(
                        device = device,
                        onWipeCache = onWipeCache,
                        onFactoryReset = { onFactoryReset(); onDismiss() }
                    )
                    5 -> DisplayPerformanceTab(
                        guestNavMode = guestNavMode,
                        guestRefreshRate = guestRefreshRate,
                        isGuestLocked = isGuestLocked,
                        onSetNavMode = onSetNavMode,
                        onSetRefreshRate = onSetRefreshRate,
                        onSetLocked = onSetLocked,
                        onMinimizeButton = onMinimizeButton
                    )
                    6 -> ExitVirtualOsTab(
                        onExitToHost = onExitToHost,
                        onDismiss = onDismiss
                    )
                }
            }
        }
    }
}

// 1. VIRTUAL DEVICE SETTINGS TAB
@Composable
fun VirtualDeviceSettingsTab(
    device: VirtualDeviceEntity,
    telemetry: HardwareTelemetry,
    bridgeStatus: HardwareBridgeStatus,
    onToggleHardwareBridge: (String, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Virtual Hardware Allocation", color = BentoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Device Instance:", color = BentoTextSecondary, fontSize = 13.sp)
                        Text(device.name, color = BentoTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("vCPU Cores:", color = BentoTextSecondary, fontSize = 13.sp)
                        Text("${device.cpuCores} Cores Allocated", color = BentoTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("vRAM Size:", color = BentoTextSecondary, fontSize = 13.sp)
                        Text("${device.ramMb} MB (${device.ramMb / 1024} GB)", color = BentoTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Display Resolution:", color = BentoTextSecondary, fontSize = 13.sp)
                        Text("${device.resolutionWidth} x ${device.resolutionHeight} (${device.densityDpi} dpi)", color = BentoTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        item {
            Text("Hardware Bridge Access Permissions", color = BentoTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    BridgeToggleRow(
                        title = "Camera Optical Bridge",
                        desc = "Pass real host camera frames to guest Camera HAL",
                        isEnabled = bridgeStatus.cameraPassthrough,
                        onToggle = { onToggleHardwareBridge("camera", it) }
                    )
                    Divider(color = BentoBorder.copy(alpha = 0.5f))
                    BridgeToggleRow(
                        title = "GNSS / GPS Location",
                        desc = "Forward GPS telemetry and coordinates to guest location manager",
                        isEnabled = bridgeStatus.locationBridge,
                        onToggle = { onToggleHardwareBridge("location", it) }
                    )
                    Divider(color = BentoBorder.copy(alpha = 0.5f))
                    BridgeToggleRow(
                        title = "Network Virtual TAP/TUN",
                        desc = "Route Internet traffic through host Wi-Fi/Cellular stack",
                        isEnabled = bridgeStatus.networkBridge,
                        onToggle = { onToggleHardwareBridge("network", it) }
                    )
                    Divider(color = BentoBorder.copy(alpha = 0.5f))
                    BridgeToggleRow(
                        title = "Battery Telemetry Passthrough",
                        desc = "Sync host battery level and charging state with guest OS",
                        isEnabled = bridgeStatus.batteryTelemetry,
                        onToggle = { onToggleHardwareBridge("battery", it) }
                    )
                    Divider(color = BentoBorder.copy(alpha = 0.5f))
                    BridgeToggleRow(
                        title = "Sensors & IMU Gyroscope",
                        desc = "Stream accelerometer and rotation data into guest sensor subsystem",
                        isEnabled = bridgeStatus.sensorsPassthrough,
                        onToggle = { onToggleHardwareBridge("sensors", it) }
                    )
                }
            }
        }
    }
}

@Composable
fun BridgeToggleRow(
    title: String,
    desc: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, color = BentoTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(desc, color = BentoTextSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BentoOnPrimary,
                checkedTrackColor = BentoPrimary,
                uncheckedThumbColor = BentoTextMuted,
                uncheckedTrackColor = BentoSurfaceElevated
            )
        )
    }
}

// 2. ROM INFORMATION TAB
@Composable
fun RomInfoTab(rom: RomEntity, telemetry: HardwareTelemetry) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active Guest ROM", color = BentoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (rom.isValid) BentoPrimaryContainer else BentoEmergencyBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (rom.isValid) "VERIFIED BOOTABLE" else "VALIDATION ISSUES",
                                color = if (rom.isValid) BentoPrimary else BentoEmergencyText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(rom.name, color = BentoTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Target Architecture: ${rom.architecture} • ${rom.osType} ${rom.version}", color = BentoTextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = BentoBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Architecture Compatibility Check", color = BentoTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (rom.isArchCompatible) "ROM ABI (${rom.architecture}) matches host native instruction set."
                        else "Incompatible ABI (${rom.architecture}) against host (${telemetry.hostAbi}).",
                        color = if (rom.isArchCompatible) BentoGreen else BentoEmergencyText,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = BentoBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Bootloader Components Checklist", color = BentoTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    ComponentCheckRow("Linux Kernel / Boot Image (boot.img)", rom.hasBootImage)
                    ComponentCheckRow("RootFS / System Partition (system.img / payload.bin)", rom.hasSystemPartition)
                    ComponentCheckRow("Framework Properties (build.prop)", rom.buildPropsJson.isNotEmpty())
                    ComponentCheckRow("Host ABI Hardware Compatibility", rom.isArchCompatible)
                }
            }
        }

        if (rom.validationSummary.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BentoSurfaceElevated),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, BentoBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Hypervisor Diagnostics Summary", color = BentoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(rom.validationSummary, color = BentoTextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun ComponentCheckRow(label: String, isPresent: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = BentoTextSecondary, fontSize = 12.sp)
        Text(
            text = if (isPresent) "PRESENT" else "MISSING",
            color = if (isPresent) BentoGreen else BentoEmergencyText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// 3. START / STOP / RESTART LIFECYCLE CONTROLS TAB
@Composable
fun LifecycleControlsTab(
    vmState: VmRuntimeState,
    onRestart: () -> Unit,
    onStop: () -> Unit,
    onPauseResume: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Virtual Hypervisor State", color = BentoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (vmState is VmRuntimeState.Running) BentoGreen else BentoAmber)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (vmState) {
                                is VmRuntimeState.Running -> "GUEST OS RUNNING • KERNEL ACTIVE"
                                is VmRuntimeState.Paused -> "GUEST CPU PAUSED / SUSPENDED"
                                is VmRuntimeState.Booting -> "BOOTLOADER STAGE ${(vmState as VmRuntimeState.Booting).stage}"
                                else -> "OFFLINE / STANDBY"
                            },
                            color = BentoTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Text("Lifecycle Operations", color = BentoTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        item {
            // Restart Device
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(16.dp))
                    .clickable { onRestart() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = BentoPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Restart Virtual OS", color = BentoTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Execute clean warm reboot of the guest Android kernel", color = BentoTextSecondary, fontSize = 11.sp)
                        }
                    }
                    Button(
                        onClick = onRestart,
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = BentoOnPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reboot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // Pause / Resume Device
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(16.dp))
                    .clickable { onPauseResume() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (vmState is VmRuntimeState.Paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = BentoGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (vmState is VmRuntimeState.Paused) "Resume Virtual OS" else "Suspend / Pause Virtual CPU",
                                color = BentoTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Freeze guest execution state in vRAM", color = BentoTextSecondary, fontSize = 11.sp)
                        }
                    }
                    Button(
                        onClick = onPauseResume,
                        colors = ButtonDefaults.buttonColors(containerColor = BentoGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (vmState is VmRuntimeState.Paused) "Resume" else "Pause", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // Power Off Device
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoEmergencyBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(16.dp))
                    .clickable { onStop() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PowerSettingsNew, contentDescription = "Power Off", tint = BentoEmergencyText, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Power Off Virtual Device", color = BentoEmergencyText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Send ACPI power down signal to hypervisor", color = BentoEmergencyText.copy(alpha = 0.8f), fontSize = 11.sp)
                        }
                    }
                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.buttonColors(containerColor = BentoEmergencyText, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Power Off", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 4. BACKUP & RESTORE TAB
@Composable
fun BackupRestoreTab(
    device: VirtualDeviceEntity,
    backups: List<DeviceBackupEntity>,
    onCreateBackup: (String, String) -> Unit,
    onRestoreBackup: (String) -> Unit,
    onDeleteBackup: (String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var backupName by remember { mutableStateOf("Snapshot ${java.text.SimpleDateFormat("MMM dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}") }
    var backupNote by remember { mutableStateOf("Manual guest system snapshot") }

    val deviceBackups = backups.filter { it.deviceId == device.id }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Instant Snapshot Engine", color = BentoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Capture a byte-exact local snapshot of the virtual userdata and installed applications.",
                        color = BentoTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = BentoOnPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create New Snapshot Backup", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        item {
            Text("Saved Device Snapshots (${deviceBackups.size})", color = BentoTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        if (deviceBackups.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No snapshots created for this device yet.", color = BentoTextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            items(deviceBackups) { b ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, BentoBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(b.backupName, color = BentoTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(b.note, color = BentoTextSecondary, fontSize = 11.sp)
                            }
                            IconButton(onClick = { onDeleteBackup(b.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BentoEmergencyText, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${b.sizeBytes / (1024 * 1024)} MB Snapshot", color = BentoPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Button(
                                onClick = { onRestoreBackup(b.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = BentoSurfaceElevated, contentColor = BentoPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Restore, contentDescription = "Restore", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Restore", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = BentoSurface,
            title = { Text("Create Snapshot", color = BentoTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = backupName,
                        onValueChange = { backupName = it },
                        label = { Text("Backup Label") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoPrimary,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoTextPrimary,
                            unfocusedTextColor = BentoTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = backupNote,
                        onValueChange = { backupNote = it },
                        label = { Text("Note / Description") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoPrimary,
                            unfocusedBorderColor = BentoBorder,
                            focusedTextColor = BentoTextPrimary,
                            unfocusedTextColor = BentoTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCreateBackup(backupName, backupNote)
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = BentoOnPrimary)
                ) {
                    Text("Save Snapshot", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = BentoTextSecondary)
                }
            }
        )
    }
}

// 5. STORAGE MANAGEMENT TAB
@Composable
fun StorageManagementTab(
    device: VirtualDeviceEntity,
    onWipeCache: () -> Unit,
    onFactoryReset: () -> Unit
) {
    var showResetConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Virtual Storage Allocation", color = BentoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("System Partition (/system):", color = BentoTextSecondary, fontSize = 12.sp)
                        Text("1.8 GB (Read-Only RootFS)", color = BentoTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Userdata Partition (/data):", color = BentoTextSecondary, fontSize = 12.sp)
                        Text("4.2 GB Allocated", color = BentoTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Dalvik & Shader Cache:", color = BentoTextSecondary, fontSize = 12.sp)
                        Text("340 MB", color = BentoTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Maintenance & Clean-up", color = BentoTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoBorder, RoundedCornerShape(16.dp)).clickable { onWipeCache() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Wipe Dalvik & System Cache", color = BentoTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Cleans temporary runtime caches to resolve guest UI lags", color = BentoTextSecondary, fontSize = 11.sp)
                    }
                    Button(
                        onClick = onWipeCache,
                        colors = ButtonDefaults.buttonColors(containerColor = BentoSurfaceElevated, contentColor = BentoPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Wipe", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoEmergencyBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoBorder, RoundedCornerShape(16.dp)).clickable { showResetConfirm = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Factory Reset Userdata", color = BentoEmergencyText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Erases guest user files, apps, and preferences", color = BentoEmergencyText.copy(alpha = 0.8f), fontSize = 11.sp)
                    }
                    Button(
                        onClick = { showResetConfirm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoEmergencyText, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Reset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            containerColor = BentoSurface,
            title = { Text("Confirm Factory Reset", color = BentoEmergencyText, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to format guest userdata? This operation is permanent.", color = BentoTextPrimary) },
            confirmButton = {
                Button(
                    onClick = {
                        onFactoryReset()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoEmergencyText, contentColor = Color.White)
                ) {
                    Text("Erase Userdata", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel", color = BentoTextSecondary)
                }
            }
        )
    }
}

// 6. DISPLAY & PERFORMANCE SETTINGS TAB
@Composable
fun DisplayPerformanceTab(
    guestNavMode: String,
    guestRefreshRate: Int,
    isGuestLocked: Boolean,
    onSetNavMode: (String) -> Unit,
    onSetRefreshRate: (Int) -> Unit,
    onSetLocked: (Boolean) -> Unit,
    onMinimizeButton: (Int) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Floating VOS Host Control Style", color = BentoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NavOptionCard(
                            title = "Sleek Capsule",
                            desc = "Mini status pill",
                            isSelected = false,
                            onClick = { onMinimizeButton(0) },
                            modifier = Modifier.weight(1f)
                        )
                        NavOptionCard(
                            title = "Edge Dot (26dp)",
                            desc = "Ultra-small dot",
                            isSelected = false,
                            onClick = { onMinimizeButton(1) },
                            modifier = Modifier.weight(1f)
                        )
                        NavOptionCard(
                            title = "Hide Overlay",
                            desc = "Edge tap to restore",
                            isSelected = false,
                            onClick = { onMinimizeButton(2) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("System Navigation Style", color = BentoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        NavOptionCard(
                            title = "Gesture Navigation",
                            desc = "Bottom pill bar with edge swipe gestures",
                            isSelected = guestNavMode == "gesture",
                            onClick = { onSetNavMode("gesture") },
                            modifier = Modifier.weight(1f)
                        )
                        NavOptionCard(
                            title = "3-Button Navigation",
                            desc = "Classic Back, Home, and Recents buttons",
                            isSelected = guestNavMode == "three_button",
                            onClick = { onSetNavMode("three_button") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Display Refresh Rate Target", color = BentoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        RefreshRateCard(
                            label = "60 Hz",
                            desc = "Standard battery-efficient rendering",
                            isSelected = guestRefreshRate == 60,
                            onClick = { onSetRefreshRate(60) },
                            modifier = Modifier.weight(1f)
                        )
                        RefreshRateCard(
                            label = "120 Hz",
                            desc = "Smooth ultra-low latency rendering",
                            isSelected = guestRefreshRate == 120,
                            onClick = { onSetRefreshRate(120) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Lock Screen on Standby", color = BentoTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Present guest ROM lock screen when resuming", color = BentoTextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isGuestLocked,
                        onCheckedChange = { onSetLocked(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BentoOnPrimary,
                            checkedTrackColor = BentoPrimary,
                            uncheckedThumbColor = BentoTextMuted,
                            uncheckedTrackColor = BentoSurfaceElevated
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun NavOptionCard(
    title: String,
    desc: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isSelected) BentoPrimaryContainer else BentoSurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .border(1.dp, if (isSelected) BentoPrimary else BentoBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = if (isSelected) BentoPrimary else BentoTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, color = BentoTextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun RefreshRateCard(
    label: String,
    desc: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isSelected) BentoPrimaryContainer else BentoSurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .border(1.dp, if (isSelected) BentoPrimary else BentoBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, color = if (isSelected) BentoPrimary else BentoTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, color = BentoTextSecondary, fontSize = 10.sp)
        }
    }
}

// 7. EXIT VIRTUAL OS TAB
@Composable
fun ExitVirtualOsTab(
    onExitToHost: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(BentoSurfaceElevated)
                .border(1.dp, BentoBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Exit", tint = BentoPrimary, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Exit Virtual OS", color = BentoTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(
            "Return back to the VOS World host manager. The guest OS will remain safely suspended in the background.",
            color = BentoTextSecondary,
            fontSize = 12.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
        )

        Button(
            onClick = onExitToHost,
            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = BentoOnPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(0.85f).height(48.dp)
        ) {
            Text("Exit to VOS World Manager", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onDismiss) {
            Text("Stay in Guest OS", color = BentoTextSecondary)
        }
    }
}

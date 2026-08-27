package com.example.vos.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoActiveIcon
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
import com.example.vos.engine.VmRuntimeState
import com.example.vos.viewmodel.VosViewModel

@Composable
fun RomManagerScreen(
    viewModel: VosViewModel,
    roms: List<RomEntity>,
    devices: List<VirtualDeviceEntity>,
    backups: List<DeviceBackupEntity>,
    activeDevice: VirtualDeviceEntity?,
    vmState: VmRuntimeState,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Devices, 1: ROMs, 2: Backups
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.importRomFromUri(it) }
    }

    var deviceToDelete by remember { mutableStateOf<VirtualDeviceEntity?>(null) }
    var romToDelete by remember { mutableStateOf<RomEntity?>(null) }
    var romToInspect by remember { mutableStateOf<RomEntity?>(null) }
    var deviceToConfigureHw by remember { mutableStateOf<VirtualDeviceEntity?>(null) }
    var deviceToBackup by remember { mutableStateOf<VirtualDeviceEntity?>(null) }
    var backupNameInput by remember { mutableStateOf("") }
    var validationAlertMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Title Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "VIRTUAL SYSTEM MANAGER",
                    color = BentoPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Devices & ROMs",
                    color = BentoTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = { filePicker.launch("application/zip") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BentoPrimary,
                    contentColor = BentoOnPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("import_rom_zip_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Import", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Import ROM ZIP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sub Tabs (Virtual Devices / ROMs / Backups)
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = BentoSurface,
            contentColor = BentoPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = BentoPrimary,
                    height = 3.dp
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, BentoBorder, RoundedCornerShape(16.dp))
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = {
                    Text(
                        "Devices (${devices.size})",
                        fontSize = 12.sp,
                        fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedSubTab == 0) BentoPrimary else BentoTextSecondary
                    )
                }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = {
                    Text(
                        "ROM Packages (${roms.size})",
                        fontSize = 12.sp,
                        fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedSubTab == 1) BentoPrimary else BentoTextSecondary
                    )
                }
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = {
                    Text(
                        "Snapshots (${backups.size})",
                        fontSize = 12.sp,
                        fontWeight = if (selectedSubTab == 2) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedSubTab == 2) BentoPrimary else BentoTextSecondary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedSubTab) {
            0 -> {
                // Virtual Devices List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (devices.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp, horizontal = 20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.FolderZip, contentDescription = null, tint = BentoPrimary, modifier = Modifier.size(36.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("No virtual devices yet.", color = BentoTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Text("Import a ROM ZIP or spawn one from the ROMs tab.", color = BentoTextSecondary, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        items(devices) { dev ->
                            val isRunning = vmState is VmRuntimeState.Running && activeDevice?.id == dev.id
                            val matchingRom = roms.find { it.id == dev.romId }
                            val isRomValid = matchingRom?.isValid ?: true

                            Card(
                                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        if (isRunning) BentoGreen else if (!isRomValid) BentoEmergencyText.copy(alpha = 0.5f) else if (dev.isDefaultDevice) BentoPrimary else BentoBorder,
                                        RoundedCornerShape(24.dp)
                                    )
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    // Header: Status indicator, Name, Badges
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isRunning) BentoGreen else BentoTextMuted)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = dev.name,
                                                color = BentoTextPrimary,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            if (isRunning) {
                                                Surface(
                                                    color = BentoGreen.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(100)
                                                ) {
                                                    Text(
                                                        "RUNNING",
                                                        color = BentoGreen,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            if (dev.isDefaultDevice) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(100))
                                                        .background(BentoPrimaryContainer)
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text("Default VM", color = BentoPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Base ROM & Specs
                                    Text(
                                        text = "Base ROM: ${matchingRom?.name ?: dev.romId}",
                                        color = BentoPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${dev.cpuCores} Virtual Cores • ${dev.ramMb} MB RAM • ${dev.storageMb} MB Isolated Storage",
                                        color = BentoTextSecondary,
                                        fontSize = 11.sp
                                    )

                                    if (!isRomValid) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(BentoEmergencyBg.copy(alpha = 0.4f))
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Warning, contentDescription = null, tint = BentoEmergencyText, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = matchingRom?.validationSummary ?: "ROM validation failed. Boot restricted.",
                                                color = BentoEmergencyText,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    // Hardware Feature Access Badges Row
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            HwFeaturePill(icon = Icons.Default.CameraAlt, label = "CAM", enabled = dev.cameraAllowed)
                                            HwFeaturePill(icon = Icons.Default.LocationOn, label = "GPS", enabled = dev.locationAllowed)
                                            HwFeaturePill(icon = Icons.Default.Public, label = "NET", enabled = dev.networkAllowed)
                                            HwFeaturePill(icon = Icons.Default.BatteryFull, label = "BAT", enabled = dev.batteryAllowed)
                                            HwFeaturePill(icon = Icons.Default.Sensors, label = "IMU", enabled = dev.sensorsAllowed)
                                        }

                                        TextButton(
                                            onClick = { deviceToConfigureHw = dev },
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(Icons.Default.Tune, contentDescription = "Config Hardware", tint = BentoPrimary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Hardware Access", fontSize = 10.sp, color = BentoPrimary, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Individual Operations: Start, Stop, Restart, Delete
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // 1. START Button
                                        Button(
                                            onClick = {
                                                if (!isRomValid) {
                                                    validationAlertMessage = "Cannot boot '${dev.name}': ${matchingRom?.validationSummary ?: "ROM validation check failed. Essential boot components are missing or architecture is incompatible."}"
                                                } else {
                                                    viewModel.startDevice(dev)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isRunning) BentoGreen.copy(alpha = 0.2f) else BentoPrimary,
                                                contentColor = if (isRunning) BentoGreen else BentoOnPrimary
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1.1f)
                                                .testTag("device_start_button_${dev.id}")
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = "Start", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isRunning) "Running" else "Start", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // 2. STOP Button
                                        Button(
                                            onClick = { viewModel.stopDevice(dev) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = BentoSurfaceElevated,
                                                contentColor = BentoEmergencyText
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(0.9f)
                                                .testTag("device_stop_button_${dev.id}")
                                        ) {
                                            Icon(Icons.Default.PowerSettingsNew, contentDescription = "Stop", modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Stop", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }

                                        // 3. RESTART Button
                                        Button(
                                            onClick = {
                                                if (!isRomValid) {
                                                    validationAlertMessage = "Cannot reboot '${dev.name}': ${matchingRom?.validationSummary ?: "ROM validation check failed."}"
                                                } else {
                                                    viewModel.restartDevice(dev)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = BentoSurfaceElevated,
                                                contentColor = BentoAmber
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(0.9f)
                                                .testTag("device_restart_button_${dev.id}")
                                        ) {
                                            Icon(Icons.Default.Refresh, contentDescription = "Restart", modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Restart", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }

                                        // 4. SNAPSHOT Button
                                        IconButton(
                                            onClick = {
                                                deviceToBackup = dev
                                                backupNameInput = "${dev.name} Snapshot"
                                            },
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(BentoSurfaceElevated)
                                        ) {
                                            Icon(Icons.Default.Backup, contentDescription = "Backup", tint = BentoGreen, modifier = Modifier.size(16.dp))
                                        }

                                        // 5. DELETE Button (Opens safety confirmation)
                                        IconButton(
                                            onClick = { deviceToDelete = dev },
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(BentoSurfaceElevated)
                                                .testTag("device_delete_button_${dev.id}")
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BentoEmergencyText, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // ROM Packages List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (roms.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No ROM packages installed. Tap Import ZIP above.", color = BentoTextSecondary, fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        items(roms) { rom ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        if (rom.isValid && rom.isArchCompatible) BentoBorder else BentoEmergencyBg.copy(alpha = 0.5f),
                                        RoundedCornerShape(24.dp)
                                    )
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (rom.isValid) BentoPrimaryContainer else BentoEmergencyBg.copy(alpha = 0.3f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.FolderZip,
                                                    contentDescription = "ROM",
                                                    tint = if (rom.isValid) BentoPrimary else BentoEmergencyText,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(rom.name, color = BentoTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                                Text("${rom.osType} • Rel: ${rom.version}", color = BentoTextSecondary, fontSize = 11.sp)
                                            }
                                        }

                                        IconButton(
                                            onClick = { romToDelete = rom },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(BentoSurfaceElevated)
                                                .testTag("delete_rom_button_${rom.id}")
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BentoEmergencyText, modifier = Modifier.size(18.dp))
                                        }
                                    }

                                    // ROM Validation Status Badge
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (rom.isValid && rom.isArchCompatible) {
                                            Surface(
                                                color = BentoGreen.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(100)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BentoGreen, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("VALID & BOOTABLE", color = BentoGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        } else if (!rom.isArchCompatible) {
                                            Surface(
                                                color = BentoEmergencyBg.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(100)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Icon(Icons.Default.Warning, contentDescription = null, tint = BentoEmergencyText, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("INCOMPATIBLE ARCH (${rom.architecture})", color = BentoEmergencyText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        } else {
                                            Surface(
                                                color = BentoAmber.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(100)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Icon(Icons.Default.Info, contentDescription = null, tint = BentoAmber, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("INVALID COMPONENTS", color = BentoAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        Text(
                                            text = "${rom.fileSize / (1024 * 1024)} MB",
                                            color = BentoTextMuted,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(rom.description, color = BentoTextSecondary, fontSize = 12.sp)

                                    if (rom.validationSummary.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = rom.validationSummary,
                                            color = if (rom.isValid) BentoTextMuted else BentoEmergencyText,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { romToInspect = rom },
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Assessment, contentDescription = "Report", tint = BentoPrimary, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Validation Report", fontSize = 11.sp, color = BentoPrimary)
                                        }

                                        Button(
                                            onClick = {
                                                if (!rom.isValid) {
                                                    validationAlertMessage = "Cannot spawn virtual device from invalid ROM: ${rom.validationSummary}"
                                                } else {
                                                    viewModel.createDeviceForRom(rom, "${rom.name} VM")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = BentoPrimary,
                                                contentColor = BentoOnPrimary
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1.2f)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Create", modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Spawn VM", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Snapshots & Backups List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (backups.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No snapshots created yet. Tap Backup on any device.", color = BentoTextSecondary, fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        items(backups) { bak ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                                .background(BentoSurfaceElevated),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Backup, contentDescription = "Backup", tint = BentoGreen, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(bak.backupName, color = BentoTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text("Device: ${bak.deviceId} • ${bak.sizeBytes / (1024 * 1024)} MB", color = BentoTextSecondary, fontSize = 11.sp)
                                        }
                                    }

                                    Button(
                                        onClick = { viewModel.restoreBackup(bak.id) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = BentoSurfaceElevated,
                                            contentColor = BentoGreen
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Restore", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Safety Confirmation Dialog for Device Deletion
    if (deviceToDelete != null) {
        AlertDialog(
            onDismissRequest = { deviceToDelete = null },
            title = { Text("Delete Virtual Device?", color = BentoTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete '${deviceToDelete!!.name}'? All sandboxed apps, settings, and virtual user data in this device will be permanently erased. This cannot be undone.",
                    color = BentoTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDevice(deviceToDelete!!.id)
                        deviceToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoEmergencyBg,
                        contentColor = BentoEmergencyText
                    ),
                    modifier = Modifier.testTag("confirm_delete_device_button")
                ) {
                    Text("Delete Permanently", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deviceToDelete = null }) {
                    Text("Cancel", color = BentoTextSecondary)
                }
            },
            containerColor = BentoSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Safety Confirmation Dialog for ROM Deletion
    if (romToDelete != null) {
        AlertDialog(
            onDismissRequest = { romToDelete = null },
            title = { Text("Delete ROM Package?", color = BentoTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Delete '${romToDelete!!.name}' package from local storage? Associated virtual devices will need to be deleted as well.",
                    color = BentoTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteRom(romToDelete!!.id)
                        romToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoEmergencyBg,
                        contentColor = BentoEmergencyText
                    )
                ) {
                    Text("Delete ROM", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { romToDelete = null }) {
                    Text("Cancel", color = BentoTextSecondary)
                }
            },
            containerColor = BentoSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Validation Report Dialog
    if (romToInspect != null) {
        val r = romToInspect!!
        AlertDialog(
            onDismissRequest = { romToInspect = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (r.isValid && r.isArchCompatible) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (r.isValid && r.isArchCompatible) BentoGreen else BentoEmergencyText,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ROM Validation Report", color = BentoTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Package: ${r.name} (${r.fileName})",
                        color = BentoPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Architecture Compatibility Tile
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (r.isArchCompatible) BentoSurfaceElevated else BentoEmergencyBg.copy(alpha = 0.3f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "ARCHITECTURE COMPATIBILITY",
                                color = if (r.isArchCompatible) BentoGreen else BentoEmergencyText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (r.isArchCompatible) "Target ISA '${r.architecture}' is supported by host hardware." else "Target ISA '${r.architecture}' cannot run natively on this device.",
                                color = BentoTextPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Essential Boot Components Checklist
                    Text(
                        text = "BOOT COMPONENTS AUDIT",
                        color = BentoTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    ValidationCheckRow("Linux Boot Image / Kernel (boot.img)", r.hasBootImage)
                    ValidationCheckRow("System RootFS / Payload (system.img/payload.bin)", r.hasSystemPartition)
                    ValidationCheckRow("Framework Properties (build.prop)", r.buildPropsJson.isNotEmpty())
                    ValidationCheckRow("Architecture ELF Compatibility", r.isArchCompatible)

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Diagnostics: ${r.validationSummary.ifEmpty { "All boot components verified successfully. Safe for virtualization." }}",
                        color = BentoTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { romToInspect = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = BentoOnPrimary)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = BentoSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Hardware Feature Access Configuration Dialog for a Device
    if (deviceToConfigureHw != null) {
        val dev = deviceToConfigureHw!!
        val liveDev = devices.find { it.id == dev.id } ?: dev

        AlertDialog(
            onDismissRequest = { deviceToConfigureHw = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = BentoPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hardware Permissions", color = BentoTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Grant or deny host hardware passthrough for '${liveDev.name}'. Changes apply persistently to this virtual device.",
                        color = BentoTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    DeviceHwToggleRow(
                        icon = Icons.Default.CameraAlt,
                        title = "Camera Passthrough",
                        subtitle = "CMOS sensor frame streaming",
                        allowed = liveDev.cameraAllowed
                    ) {
                        viewModel.toggleDeviceHardwarePermission(liveDev.id, "camera", it)
                    }

                    DeviceHwToggleRow(
                        icon = Icons.Default.LocationOn,
                        title = "Location & GPS",
                        subtitle = "GNSS coordinates & coarse triangulation",
                        allowed = liveDev.locationAllowed
                    ) {
                        viewModel.toggleDeviceHardwarePermission(liveDev.id, "location", it)
                    }

                    DeviceHwToggleRow(
                        icon = Icons.Default.Public,
                        title = "Network & Sockets",
                        subtitle = "TCP/UDP routing via host adapter",
                        allowed = liveDev.networkAllowed
                    ) {
                        viewModel.toggleDeviceHardwarePermission(liveDev.id, "network", it)
                    }

                    DeviceHwToggleRow(
                        icon = Icons.Default.BatteryFull,
                        title = "Battery Telemetry",
                        subtitle = "Real charge percentage & PMIC voltage",
                        allowed = liveDev.batteryAllowed
                    ) {
                        viewModel.toggleDeviceHardwarePermission(liveDev.id, "battery", it)
                    }

                    DeviceHwToggleRow(
                        icon = Icons.Default.Sensors,
                        title = "Sensors (IMU/Lux)",
                        subtitle = "3-Axis accelerometer & ambient light",
                        allowed = liveDev.sensorsAllowed
                    ) {
                        viewModel.toggleDeviceHardwarePermission(liveDev.id, "sensors", it)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { deviceToConfigureHw = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = BentoOnPrimary)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = BentoSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Incompatible / Invalid ROM Warning Modal
    if (validationAlertMessage != null) {
        AlertDialog(
            onDismissRequest = { validationAlertMessage = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = BentoEmergencyText, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ROM Validation Blocker", color = BentoTextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = validationAlertMessage!!,
                    color = BentoTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { validationAlertMessage = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary, contentColor = BentoOnPrimary)
                ) {
                    Text("Understood", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = BentoSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Snapshot Modal
    if (deviceToBackup != null) {
        AlertDialog(
            onDismissRequest = { deviceToBackup = null },
            title = { Text("Create Device Snapshot", color = BentoTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Creates an isolated persistent snapshot of current virtual disk and settings.", color = BentoTextSecondary, fontSize = 12.sp)
                    OutlinedTextField(
                        value = backupNameInput,
                        onValueChange = { backupNameInput = it },
                        label = { Text("Snapshot Label") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BentoTextPrimary,
                            unfocusedTextColor = BentoTextPrimary,
                            focusedBorderColor = BentoPrimary,
                            unfocusedBorderColor = BentoBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createBackup(deviceToBackup!!.id, backupNameInput, "")
                        deviceToBackup = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoPrimary,
                        contentColor = BentoOnPrimary
                    )
                ) {
                    Text("Create Snapshot", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deviceToBackup = null }) {
                    Text("Cancel", color = BentoTextSecondary)
                }
            },
            containerColor = BentoSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun HwFeaturePill(icon: ImageVector, label: String, enabled: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (enabled) BentoGreen.copy(alpha = 0.12f) else BentoSurfaceElevated)
            .border(0.5.dp, if (enabled) BentoGreen.copy(alpha = 0.3f) else BentoBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 5.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) BentoGreen else BentoTextMuted,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                color = if (enabled) BentoGreen else BentoTextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ValidationCheckRow(component: String, found: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (found) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
                tint = if (found) BentoGreen else BentoEmergencyText,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(component, color = BentoTextPrimary, fontSize = 11.sp)
        }
        Text(
            text = if (found) "FOUND / OK" else "MISSING",
            color = if (found) BentoGreen else BentoEmergencyText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DeviceHwToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    allowed: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BentoBackground)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (allowed) BentoPrimary else BentoTextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, color = BentoTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (allowed) "Accessible to VM" else "Access Denied / Isolated",
                    color = if (allowed) BentoGreen else BentoEmergencyText,
                    fontSize = 10.sp
                )
            }
        }

        Switch(
            checked = allowed,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BentoPrimary,
                checkedTrackColor = BentoPrimaryContainer,
                uncheckedThumbColor = BentoTextSecondary,
                uncheckedTrackColor = BentoSurfaceElevated
            )
        )
    }
}


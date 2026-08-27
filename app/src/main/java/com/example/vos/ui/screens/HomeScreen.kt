package com.example.vos.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import com.example.vos.data.model.RomEntity
import com.example.vos.data.model.VirtualDeviceEntity
import com.example.vos.engine.HardwareTelemetry
import com.example.vos.engine.VmRuntimeState
import com.example.vos.viewmodel.MainTab
import com.example.vos.viewmodel.VosViewModel

@Composable
fun HomeScreen(
    viewModel: VosViewModel,
    roms: List<RomEntity>,
    devices: List<VirtualDeviceEntity>,
    activeDevice: VirtualDeviceEntity?,
    vmState: VmRuntimeState,
    telemetry: HardwareTelemetry,
    importProgress: Pair<Float, String>?,
    onNavigateTab: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.importRomFromUri(it) }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Bento Header: Hypervisor Status
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HYPERVISOR L3",
                        color = BentoPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "VOS World",
                        color = BentoTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Bento Header Pulse Node
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BentoSurfaceElevated)
                        .border(1.dp, BentoBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (vmState is VmRuntimeState.Running) BentoGreen else BentoAmber
                            )
                            .alpha(if (vmState is VmRuntimeState.Running) pulseAlpha else 1f)
                    )
                }
            }
        }

        // Import Progress Bento Card (if active)
        if (importProgress != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BentoPrimary, RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                progress = { importProgress.first },
                                color = BentoPrimary,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Importing ROM Archive...", color = BentoTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(importProgress.second, color = BentoTextSecondary, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { importProgress.first },
                            color = BentoPrimary,
                            trackColor = BentoSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Primary Bento Hero Card: Active Instance (Lavender Highlight)
        item {
            val isRunning = vmState is VmRuntimeState.Running
            val isBooting = vmState is VmRuntimeState.Booting
            val targetDevice = activeDevice ?: devices.firstOrNull()

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isRunning || targetDevice != null) BentoPrimary else BentoSurface
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isRunning || targetDevice != null) BentoPrimary else BentoBorder,
                        RoundedCornerShape(28.dp)
                    )
            ) {
                val textColor = if (isRunning || targetDevice != null) BentoOnPrimary else BentoTextPrimary
                val subtextColor = if (isRunning || targetDevice != null) BentoOnPrimary.copy(alpha = 0.75f) else BentoTextSecondary

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top row: status pill + PID / Uptime
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (isRunning || targetDevice != null) BentoOnPrimary else BentoSurfaceElevated,
                            shape = RoundedCornerShape(100)
                        ) {
                            Text(
                                text = if (isRunning) "ACTIVE INSTANCE" else if (isBooting) "BOOTING GUEST" else "STANDBY INSTANCE",
                                color = if (isRunning || targetDevice != null) BentoPrimary else BentoTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = if (isRunning) "PID: 8842 • ${(vmState as VmRuntimeState.Running).uptimeSeconds}s" else "STATUS: READY",
                            color = subtextColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = targetDevice?.name ?: "No Virtual Device",
                        color = textColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )
                    Text(
                        text = if (targetDevice != null) "Android Guest OS • ${telemetry.hostAbi} • ${targetDevice.ramMb}MB RAM" else "Import a ROM ZIP to initialize hypervisor container",
                        color = subtextColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress bar & memory allocation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(100))
                                .background(
                                    if (isRunning || targetDevice != null) BentoOnPrimary.copy(alpha = 0.2f) else BentoBorder
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if (isRunning) 0.64f else 0.1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(100))
                                    .background(
                                        if (isRunning || targetDevice != null) BentoOnPrimary else BentoPrimary
                                    )
                            )
                        }

                        Text(
                            text = if (targetDevice != null) "${targetDevice.ramMb / 1024}GB / 8GB" else "0GB / 8GB",
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bento Hero Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (isRunning) {
                            Button(
                                onClick = { onNavigateTab(MainTab.VIRTUAL_PHONE) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BentoOnPrimary,
                                    contentColor = BentoPrimary
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("open_running_os_button")
                            ) {
                                Icon(Icons.Default.Smartphone, contentDescription = "View", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open Virtual OS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Button(
                                onClick = { viewModel.stopDevice() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BentoEmergencyBg,
                                    contentColor = BentoEmergencyText
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = "Stop", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Stop", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        } else {
                            Button(
                                onClick = { targetDevice?.let { viewModel.startDevice(it) } },
                                enabled = targetDevice != null && !isBooting,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (targetDevice != null) BentoOnPrimary else BentoSurfaceElevated,
                                    contentColor = if (targetDevice != null) BentoPrimary else BentoTextSecondary
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("boot_virtual_os_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Boot", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Boot Virtual OS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // 2-Column Bento Quick-Action Grid (Import ROM & Emergency)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Column 1: Import ROM Bento Tile
                Card(
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
                        .clickable { filePicker.launch("application/zip") }
                        .testTag("import_rom_zip_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(BentoPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Import",
                                tint = BentoPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Import ROM",
                            color = BentoTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "ZIP Archives",
                            color = BentoTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Column 2: Emergency & Sensors Bento Tile
                Card(
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
                        .clickable { onNavigateTab(MainTab.EMERGENCY_HARDWARE) }
                        .testTag("nav_tab_emergency_shortcut")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(BentoEmergencyBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Emergency",
                                tint = BentoEmergencyText,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Emergency",
                            color = BentoTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "HAL Passthrough",
                            color = BentoTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Section Bento: Local ROM Library
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Local ROM Library",
                            color = BentoTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${roms.size} VOLUMES STORED",
                            color = BentoPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    roms.forEach { rom ->
                        val matchingDevice = devices.find { it.romId == rom.id }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(BentoBackground)
                                .border(1.dp, BentoBorder.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BentoSurfaceElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val prefix = rom.name.take(2).uppercase()
                                    Text(
                                        text = prefix,
                                        color = BentoPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = rom.name,
                                        color = BentoTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${rom.version} • ${rom.architecture} • ${if (matchingDevice != null) "Ready" else "Unconfigured"}",
                                        color = BentoTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (matchingDevice != null) {
                                        viewModel.startDevice(matchingDevice)
                                    } else {
                                        viewModel.createDeviceForRom(rom, null)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BentoSurfaceElevated,
                                    contentColor = BentoPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (matchingDevice != null) "Boot" else "Init",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Bento: Architecture & Isolation Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BentoSurfaceElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Security",
                                tint = BentoGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Isolated Hypervisor Sandbox",
                            color = BentoTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• Runs completely on-device in user-space with zero cloud dependencies.\n" +
                                "• Host ABI: ${telemetry.hostAbi} • Native ISA acceleration active.\n" +
                                "• Host bootloader, stock partitions, and Knox/SafetyNet intact.\n" +
                                "• Sandboxed disk images stored in private application directory.",
                        color = BentoTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

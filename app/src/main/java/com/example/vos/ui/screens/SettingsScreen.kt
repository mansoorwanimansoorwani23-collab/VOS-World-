package com.example.vos.ui.screens

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoAmber
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoGreen
import com.example.ui.theme.BentoOnPrimary
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceElevated
import com.example.ui.theme.BentoTextMuted
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.vos.data.model.VirtualDeviceEntity
import com.example.vos.viewmodel.VosViewModel

@Composable
fun SettingsScreen(
    viewModel: VosViewModel,
    devices: List<VirtualDeviceEntity>,
    defaultDevice: VirtualDeviceEntity?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "CONFIGURATION & SYSTEM",
                    color = BentoPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Settings",
                    color = BentoTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Section: Default Home / Launcher Integration Bento Tile
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, BentoPrimary, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BentoPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Home, contentDescription = "Home", tint = BentoPrimary, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Default Home Launcher",
                                color = BentoTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Direct OS boot on phone unlock",
                                color = BentoPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Set VOS World as your default phone launcher. When you press the home button on your real phone, VOS World presents your isolated virtual OS environment without altering system firmware.",
                        color = BentoTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.openSystemHomeSettings(context) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BentoPrimary,
                            contentColor = BentoOnPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("open_home_settings_button")
                    ) {
                        Text("Open Default Home App Settings", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Section: Select Default Virtual Device Bento Container
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
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Auto-Boot Default Device",
                        color = BentoTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Choose which virtual device boots on launch:",
                        color = BentoTextSecondary,
                        fontSize = 12.sp
                    )

                    devices.forEach { dev ->
                        val isSelected = defaultDevice?.id == dev.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) BentoSurfaceElevated else BentoBackground)
                                .border(1.dp, if (isSelected) BentoPrimary else BentoBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                .clickable { viewModel.setDefaultDevice(dev.id) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(dev.name, color = BentoTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("${dev.cpuCores} Cores • ${dev.ramMb} MB RAM", color = BentoTextSecondary, fontSize = 11.sp)
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.setDefaultDevice(dev.id) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = BentoPrimary,
                                    unselectedColor = BentoTextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section: Sandbox Security Declaration Bento Container
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
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Security & Data Isolation Guarantees",
                        color = BentoTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    SecurityFactRow(Icons.Default.CloudOff, "Zero Cloud Transmission", "ROMs, guest databases, files, and user logs are kept 100% on device.")
                    SecurityFactRow(Icons.Default.Lock, "Unmodified Bootloader", "Host system partitions, bootloader, recovery, and vendor firmware are strictly untouched.")
                    SecurityFactRow(Icons.Default.Shield, "Userspace Container", "All guest processes execute securely within Android sandbox uid boundaries.")
                }
            }
        }

        // Section: About Bento Container
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
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Hypervisor Information",
                        color = BentoTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Application Version", color = BentoTextSecondary, fontSize = 13.sp)
                        Text("v2.4.0-Bento", color = BentoTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Virtual V-HAL Engine", color = BentoTextSecondary, fontSize = 13.sp)
                        Text("ARM64 Native User-Space", color = BentoPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Database Persistence", color = BentoTextSecondary, fontSize = 13.sp)
                        Text("Room SQLite Local", color = BentoTextPrimary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityFactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BentoBackground)
            .border(1.dp, BentoBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BentoSurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = BentoGreen, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, color = BentoTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(description, color = BentoTextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}

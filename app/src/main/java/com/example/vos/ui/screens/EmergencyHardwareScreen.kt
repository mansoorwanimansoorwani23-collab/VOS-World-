package com.example.vos.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoAmber
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoEmergencyBg
import com.example.ui.theme.BentoEmergencyCard
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
import com.example.vos.engine.HardwareBridgeStatus
import com.example.vos.engine.HardwareTelemetry
import com.example.vos.viewmodel.VosViewModel

@Composable
fun EmergencyHardwareScreen(
    viewModel: VosViewModel,
    telemetry: HardwareTelemetry,
    bridgeStatus: HardwareBridgeStatus,
    isSosActive: Boolean,
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
        // Bento Screen Header
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "VIRTUAL HARDWARE ABSTRACTION LAYER",
                    color = BentoPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Hardware Access & V-HAL",
                    color = BentoTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Section Title: Hardware Feature Permissions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GUEST HARDWARE PERMISSIONS",
                    color = BentoPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                val activeCount = listOf(
                    bridgeStatus.cameraPassthrough,
                    bridgeStatus.locationBridge,
                    bridgeStatus.networkBridge,
                    bridgeStatus.batteryTelemetry,
                    bridgeStatus.sensorsPassthrough
                ).count { it }
                Text(
                    text = "$activeCount / 5 Accessible",
                    color = if (activeCount == 5) BentoGreen else BentoAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 1. Camera Access Card
        item {
            HardwarePermissionCard(
                icon = Icons.Default.CameraAlt,
                title = "Camera Hardware Access",
                description = "Grants virtual camera HAL pipeline to stream real device CMOS image sensor buffers into the guest OS.",
                isAccessible = bridgeStatus.cameraPassthrough,
                accessibleLabel = "ACCESSIBLE TO VIRTUAL OS",
                deniedLabel = "ACCESS DENIED / BLOCKED",
                onToggle = { viewModel.toggleHardwareBridge("camera", it) },
                testTag = "toggle_camera_bridge"
            )
        }

        // 2. Location Access Card
        item {
            HardwarePermissionCard(
                icon = Icons.Default.LocationOn,
                title = "Location & GPS Provider",
                description = "Provides real GNSS satellite coordinates and cell-tower triangulation to guest location services.",
                isAccessible = bridgeStatus.locationBridge,
                accessibleLabel = "ACCESSIBLE TO VIRTUAL OS",
                deniedLabel = "ACCESS DENIED / BLOCKED",
                onToggle = { viewModel.toggleHardwareBridge("location", it) },
                testTag = "toggle_location_bridge"
            )
        }

        // 3. Network Access Card
        item {
            HardwarePermissionCard(
                icon = Icons.Default.Public,
                title = "Network Status & Sockets",
                description = "Routes guest TCP/UDP packets and DNS queries through host Wi-Fi & cellular network stack.",
                isAccessible = bridgeStatus.networkBridge,
                accessibleLabel = "ACCESSIBLE TO VIRTUAL OS",
                deniedLabel = "SANDBOXED / OFFLINE",
                onToggle = { viewModel.toggleHardwareBridge("network", it) },
                testTag = "toggle_network_bridge"
            )
        }

        // 4. Battery Access Card
        item {
            HardwarePermissionCard(
                icon = if (telemetry.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                title = "Battery & Power Telemetry",
                description = "Passes real host battery charge percentage, voltage (${telemetry.batteryVoltage} mV), and charging state to the virtual PMIC.",
                isAccessible = bridgeStatus.batteryTelemetry,
                accessibleLabel = "ACCESSIBLE TO VIRTUAL OS",
                deniedLabel = "RESTRICTED (STATIC 100%)",
                onToggle = { viewModel.toggleHardwareBridge("battery", it) },
                testTag = "toggle_battery_bridge"
            )
        }

        // 5. Sensors & IMU Access Card
        item {
            HardwarePermissionCard(
                icon = Icons.Default.Sensors,
                title = "Sensors & IMU (Accelerometer/Light)",
                description = "Streams real 3-axis accelerometer motions and ambient lux readings into guest sensors HAL.",
                isAccessible = bridgeStatus.sensorsPassthrough,
                accessibleLabel = "ACCESSIBLE TO VIRTUAL OS",
                deniedLabel = "ACCESS DENIED / ZEROED",
                onToggle = { viewModel.toggleHardwareBridge("sensors", it) },
                testTag = "toggle_sensors_bridge"
            )
        }

        // Emergency SOS Hero Bento Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSosActive) BentoEmergencyBg else BentoEmergencyCard
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        if (isSosActive) BentoEmergencyText else BentoBorder,
                        RoundedCornerShape(28.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (isSosActive) Color.White else BentoEmergencyBg,
                            shape = RoundedCornerShape(100)
                        ) {
                            Text(
                                text = if (isSosActive) "SOS BEACON TRANSMITTING" else "EMERGENCY PROTOCOL READY",
                                color = if (isSosActive) BentoEmergencyBg else BentoEmergencyText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        if (isSosActive) {
                            Icon(Icons.Default.Warning, contentDescription = "Alert", tint = BentoEmergencyText)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Real-Phone Emergency Direct Dial",
                        color = BentoTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "VOS World provides a direct hardware emergency bridge to guarantee safety access even while running deep inside a virtual guest OS.",
                        color = BentoTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.toggleSos() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSosActive) Color.White else BentoEmergencyBg,
                                contentColor = if (isSosActive) BentoEmergencyBg else Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("toggle_sos_button")
                        ) {
                            Text(
                                text = if (isSosActive) "Deactivate SOS" else "Activate SOS Beacon",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:911"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    viewModel.toggleSos()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoSurfaceElevated,
                                contentColor = BentoTextPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Dial 911", tint = BentoGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call 911", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section Title: Live Telemetry
        item {
            Text(
                text = "HOST TELEMETRY TILES",
                color = BentoPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // 2-Column Bento Grid for Battery & Light Sensor
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Battery Bento Tile
                Card(
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BentoSurfaceElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (telemetry.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                                contentDescription = "Battery",
                                tint = BentoGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("${telemetry.batteryLevel}%", color = BentoTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(if (telemetry.isCharging) "Charging (${telemetry.batteryVoltage}mV)" else "On Battery", color = BentoTextSecondary, fontSize = 11.sp)
                    }
                }

                // Sensor Bento Tile
                Card(
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BentoSurfaceElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = "Light", tint = BentoAmber, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("${telemetry.lightLux.toInt()} lx", color = BentoTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Ambient Lux Sensor", color = BentoTextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        // Accelerometer 3-Axis Bento Tile
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BentoPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Sensors, contentDescription = "IMU", tint = BentoPrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Host Accelerometer (3-Axis IMU)", color = BentoTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(
                            color = BentoSurfaceElevated,
                            shape = RoundedCornerShape(100)
                        ) {
                            Text("V-HAL SYNC", color = BentoGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AxisDisplay("X-AXIS", telemetry.accelX)
                        AxisDisplay("Y-AXIS", telemetry.accelY)
                        AxisDisplay("Z-AXIS", telemetry.accelZ)
                    }
                }
            }
        }
    }
}

@Composable
fun HardwarePermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    isAccessible: Boolean,
    accessibleLabel: String,
    deniedLabel: String,
    onToggle: (Boolean) -> Unit,
    testTag: String = ""
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isAccessible) BentoBorder else BentoEmergencyBg.copy(alpha = 0.4f),
                RoundedCornerShape(22.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isAccessible) BentoPrimaryContainer else BentoSurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (isAccessible) BentoPrimary else BentoTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            color = BentoTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        // Status Indicator Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isAccessible) BentoGreen else BentoEmergencyText)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isAccessible) accessibleLabel else deniedLabel,
                                color = if (isAccessible) BentoGreen else BentoEmergencyText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Switch(
                    checked = isAccessible,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BentoPrimary,
                        checkedTrackColor = BentoPrimaryContainer,
                        uncheckedThumbColor = BentoTextSecondary,
                        uncheckedTrackColor = BentoSurfaceElevated
                    ),
                    modifier = Modifier.testTag(testTag)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description,
                color = BentoTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun AxisDisplay(label: String, value: Float) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BentoBackground)
            .border(1.dp, BentoBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = BentoTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = String.format("%.2f", value),
                color = BentoTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

package com.example.vos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NetworkCell
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vos.engine.HardwareTelemetry
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 1. GUEST ROM STATUS BAR
@Composable
fun GuestRomStatusBar(
    telemetry: HardwareTelemetry,
    carrierName: String,
    onPullDownQuickSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        while (true) {
            currentTime = sdf.format(Date())
            delay(10000)
        }
    }

    Surface(
        color = Color(0xFF090D16).copy(alpha = 0.92f),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPullDownQuickSettings() }
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > 10f) {
                        onPullDownQuickSettings()
                    }
                }
            }
            .testTag("guest_status_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Clock & Network Carrier
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currentTime.ifEmpty { "10:00" },
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = carrierName,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Center Camera Punch Hole
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF030712))
                    .border(1.dp, Color(0xFF1E293B), CircleShape)
            )

            // Right: Status Icons (WiFi, 5G/LTE, Battery %)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (telemetry.isWifiConnected) {
                    Icon(
                        imageVector = Icons.Default.SignalWifi4Bar,
                        contentDescription = "WiFi",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = "No WiFi",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(14.dp)
                    )
                }

                Icon(
                    imageVector = Icons.Default.NetworkCell,
                    contentDescription = "Cellular",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${telemetry.batteryLevel}%",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = if (telemetry.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                        contentDescription = "Battery",
                        tint = if (telemetry.batteryLevel <= 15) Color(0xFFEF4444) else Color(0xFF10B981),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// 2. GUEST ROM NOTIFICATION & QUICK SETTINGS PANEL
@Composable
fun GuestQuickSettingsPanel(
    isOpen: Boolean,
    onClose: () -> Unit,
    telemetry: HardwareTelemetry,
    onOpenSettingsApp: () -> Unit,
    onRestartDevice: () -> Unit,
    onStopDevice: () -> Unit,
    modifier: Modifier = Modifier
) {
    var wifiOn by remember { mutableStateOf(true) }
    var bluetoothOn by remember { mutableStateOf(true) }
    var mobileDataOn by remember { mutableStateOf(true) }
    var flashlightOn by remember { mutableStateOf(false) }
    var airplaneOn by remember { mutableStateOf(false) }
    var autoRotateOn by remember { mutableStateOf(true) }
    var nightLightOn by remember { mutableStateOf(true) }
    var darkThemeOn by remember { mutableStateOf(true) }
    var hotspotOn by remember { mutableStateOf(false) }
    var batterySaverOn by remember { mutableStateOf(false) }
    var brightness by remember { mutableFloatStateOf(0.85f) }

    var isMediaPlaying by remember { mutableStateOf(true) }

    val notifications = remember {
        mutableStateListOf(
            Pair("Android System", "Guest hypervisor running with optimal vCPU performance"),
            Pair("Hardware Bridge", "Camera, Location, and Network HAL active"),
            Pair("System Update", "LineageOS 21.0 / Pixel OS framework up to date")
        )
    }

    var currentTimeText by remember { mutableStateOf("") }
    var currentDateText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val tf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val df = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTimeText = tf.format(now)
            currentDateText = df.format(now)
            delay(10000)
        }
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            color = Color(0xFF090D16).copy(alpha = 0.98f),
            modifier = Modifier
                .fillMaxSize()
                .testTag("control_center_shade")
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Quick Settings Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentTimeText.ifEmpty { "10:00" },
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Light
                            )
                            Text(
                                text = currentDateText.ifEmpty { "Thursday, Aug 27" },
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {
                                    onClose()
                                    onOpenSettingsApp()
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B))
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(18.dp))
                            }

                            IconButton(
                                onClick = onClose,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B))
                                    .testTag("close_control_center_button")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Brightness Slider Bar
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.BrightnessMedium, contentDescription = "Brightness", tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Slider(
                                value = brightness,
                                onValueChange = { brightness = it },
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF38BDF8),
                                    activeTrackColor = Color(0xFF38BDF8),
                                    inactiveTrackColor = Color(0xFF334155)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${(brightness * 100).toInt()}%", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Quick Settings Grid Tiles (Row 1)
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuickSettingTile(
                            icon = if (wifiOn) Icons.Default.Wifi else Icons.Default.WifiOff,
                            title = "Internet",
                            subtitle = if (wifiOn) "Virtual-Bridge" else "Off",
                            isActive = wifiOn,
                            onClick = { wifiOn = !wifiOn },
                            modifier = Modifier.weight(1f)
                        )
                        QuickSettingTile(
                            icon = Icons.Default.Bluetooth,
                            title = "Bluetooth",
                            subtitle = if (bluetoothOn) "Connected" else "Off",
                            isActive = bluetoothOn,
                            onClick = { bluetoothOn = !bluetoothOn },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Quick Settings Grid Tiles (Row 2)
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuickSettingTile(
                            icon = Icons.Default.FlashlightOn,
                            title = "Flashlight",
                            subtitle = if (flashlightOn) "On" else "Off",
                            isActive = flashlightOn,
                            onClick = { flashlightOn = !flashlightOn },
                            modifier = Modifier.weight(1f)
                        )
                        QuickSettingTile(
                            icon = Icons.Default.AirplanemodeActive,
                            title = "Airplane Mode",
                            subtitle = if (airplaneOn) "On" else "Off",
                            isActive = airplaneOn,
                            onClick = { airplaneOn = !airplaneOn },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Quick Settings Grid Tiles (Row 3)
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuickSettingTile(
                            icon = Icons.Default.ScreenRotation,
                            title = "Auto-Rotate",
                            subtitle = if (autoRotateOn) "Auto" else "Locked",
                            isActive = autoRotateOn,
                            onClick = { autoRotateOn = !autoRotateOn },
                            modifier = Modifier.weight(1f)
                        )
                        QuickSettingTile(
                            icon = Icons.Default.DarkMode,
                            title = "Dark Theme",
                            subtitle = if (darkThemeOn) "Active" else "Light",
                            isActive = darkThemeOn,
                            onClick = { darkThemeOn = !darkThemeOn },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Media Player Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Media", tint = Color(0xFF38BDF8), modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Virtual Audio Synthesizer", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Guest OS Sound Engine • 48kHz", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { isMediaPlaying = !isMediaPlaying }) {
                                    Icon(
                                        imageVector = if (isMediaPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = Color(0xFF38BDF8)
                                    )
                                }
                                IconButton(onClick = { /* next track */ }) {
                                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color(0xFF94A3B8))
                                }
                            }
                        }
                    }
                }

                // System Notifications Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notifications", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        if (notifications.isNotEmpty()) {
                            Text(
                                text = "Clear All",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { notifications.clear() }
                            )
                        }
                    }
                }

                if (notifications.isEmpty()) {
                    item {
                        Text("No new notifications", color = Color(0xFF64748B), fontSize = 12.sp)
                    }
                } else {
                    items(notifications) { (title, desc) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Notification", tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(desc, color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                }
                                IconButton(onClick = { notifications.remove(Pair(title, desc)) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Power Controls
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onRestartDevice()
                                    onClose()
                                }
                                .testTag("control_center_restart")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Restart", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onStopDevice()
                                    onClose()
                                }
                                .testTag("control_center_shutdown")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PowerSettingsNew, contentDescription = "Power Off", tint = Color(0xFFFCA5A5), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Power Off", color = Color(0xFFFCA5A5), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickSettingTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFF0284C7) else Color(0xFF1E293B)
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) Color.White else Color(0xFF94A3B8),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, color = if (isActive) Color.White else Color(0xFFF1F5F9), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = if (isActive) Color(0xFFE0F2FE) else Color(0xFF94A3B8), fontSize = 10.sp)
            }
        }
    }
}

// 3. GUEST ROM NAVIGATION SYSTEM (Gesture Pill or 3-Button Bar)
@Composable
fun GuestRomNavigationBar(
    navMode: String, // "gesture" or "three_button"
    onBack: () -> Unit,
    onHome: () -> Unit,
    onRecents: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF090D16).copy(alpha = 0.95f),
        modifier = modifier
            .fillMaxWidth()
            .testTag("guest_nav_bar")
    ) {
        if (navMode == "gesture") {
            // Modern Gesture Pill Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clickable { onHome() },
                contentAlignment = Alignment.Center
            ) {
                // Gesture Bar Pill
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFCBD5E1))
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { _, dragAmount ->
                                if (dragAmount < -15f) {
                                    onRecents()
                                }
                            }
                        }
                        .testTag("guest_gesture_pill")
                )
            }
        } else {
            // Classic 3-Button Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button (Triangle / Chevron)
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp).testTag("guest_nav_back")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Home Button (Circle)
                IconButton(
                    onClick = onHome,
                    modifier = Modifier.size(48.dp).testTag("guest_nav_home")
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(2.dp, Color(0xFFCBD5E1), CircleShape)
                    )
                }

                // Recents Button (Square)
                IconButton(
                    onClick = onRecents,
                    modifier = Modifier.size(48.dp).testTag("guest_nav_recents")
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .border(2.dp, Color(0xFFCBD5E1), RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}

// 4. GUEST LOCK SCREEN
@Composable
fun GuestLockScreen(
    romName: String,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val tf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val df = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTime = tf.format(now)
            currentDate = df.format(now)
            delay(10000)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF020617)
                    )
                )
            )
            .clickable { onUnlock() }
            .testTag("guest_lock_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Lock icon & Lock state
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(romName, color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            // Big Clock & Date
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentTime.ifEmpty { "10:00" },
                    color = Color.White,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraLight,
                    letterSpacing = (-2).sp
                )
                Text(
                    text = currentDate.ifEmpty { "Thursday, August 27" },
                    color = Color(0xFFCBD5E1),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Swipe to unlock hint
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B).copy(alpha = 0.8f))
                        .clickable { onUnlock() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = "Unlock", tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("Tap or swipe up to unlock", color = Color(0xFF94A3B8), fontSize = 13.sp)
            }
        }
    }
}

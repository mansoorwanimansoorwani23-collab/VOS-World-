package com.example.vos.ui.components

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.vos.data.model.GuestAppEntity
import com.example.vos.data.model.RomEntity
import com.example.vos.data.model.VirtualDeviceEntity
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GuestLauncherHome(
    device: VirtualDeviceEntity,
    rom: RomEntity,
    apps: List<GuestAppEntity>,
    onLaunchApp: (packageName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentDateText by remember { mutableStateOf("") }
    var currentClockText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        while (true) {
            val now = Date()
            currentDateText = dateFormat.format(now)
            currentClockText = timeFormat.format(now)
            delay(15000)
        }
    }

    // Dynamic ROM Wallpaper Gradient
    val wallpaperGradient = when {
        rom.name.contains("Lineage", ignoreCase = true) -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F2027),
                Color(0xFF203A43),
                Color(0xFF2C5364)
            )
        )
        rom.name.contains("Pixel", ignoreCase = true) -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1E1B4B),
                Color(0xFF312E81),
                Color(0xFF0F172A)
            )
        )
        else -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F172A),
                Color(0xFF1E293B),
                Color(0xFF020617)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(wallpaperGradient)
            .testTag("guest_launcher_home")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top ROM Smart Widget (At-a-Glance / Trebuchet Widget)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 8.dp, end = 8.dp)
            ) {
                Text(
                    text = currentClockText.ifEmpty { "10:00" },
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (-1).sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentDateText.ifEmpty { "Thursday, August 27" },
                        color = Color(0xFFE2E8F0),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•  24°C Sunny",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )
                }
            }

            // Grid of Installed System and User Apps
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(apps) { app ->
                    GuestAppIconItem(app = app, onClick = { onLaunchApp(app.packageName) })
                }
            }

            // ROM Home Search Bar & Bottom App Dock
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search Bar Pill
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.75f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLaunchApp("org.chromium.browser") }
                        .border(1.dp, Color(0xFF334155).copy(alpha = 0.6f), RoundedCornerShape(28.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Search apps, web, files...", color = Color(0xFF94A3B8), fontSize = 13.sp)
                        }
                        Icon(Icons.Default.Mic, contentDescription = "Voice", tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                    }
                }

                // Bottom App Dock (Pinned Apps)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF334155).copy(alpha = 0.7f), RoundedCornerShape(26.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DockItem(icon = Icons.Default.Phone, color = NeonGreen, label = "Phone", onClick = { onLaunchApp("com.android.dialer") })
                        DockItem(icon = Icons.Default.Language, color = NeonCyan, label = "Browser", onClick = { onLaunchApp("org.chromium.browser") })
                        DockItem(icon = Icons.Default.Folder, color = NeonAmber, label = "Files", onClick = { onLaunchApp("com.android.documentsui") })
                        DockItem(icon = Icons.Default.CameraAlt, color = NeonPurple, label = "Camera", onClick = { onLaunchApp("com.android.camera2") })
                        DockItem(icon = Icons.Default.Settings, color = Color.White, label = "Settings", onClick = { onLaunchApp("com.android.settings") })
                    }
                }
            }
        }
    }
}

@Composable
fun GuestAppIconItem(app: GuestAppEntity, onClick: () -> Unit) {
    val (icon, iconBgColor, iconTintColor) = when (app.iconType) {
        "settings" -> Triple(Icons.Default.Settings, Color(0xFF334155), Color.White)
        "phone" -> Triple(Icons.Default.Phone, Color(0xFF10B981), Color.White)
        "terminal" -> Triple(Icons.Default.Terminal, Color(0xFF0284C7), Color.White)
        "files" -> Triple(Icons.Default.Folder, Color(0xFFF59E0B), Color.White)
        "camera" -> Triple(Icons.Default.CameraAlt, Color(0xFF8B5CF6), Color.White)
        "browser" -> Triple(Icons.Default.Language, Color(0xFF0EA5E9), Color.White)
        "notes" -> Triple(Icons.Default.Description, Color(0xFFEAB308), Color.White)
        "store" -> Triple(Icons.Default.Shop, Color(0xFF22C55E), Color.White)
        "gallery" -> Triple(Icons.Default.Image, Color(0xFFEC4899), Color.White)
        "calculator" -> Triple(Icons.Default.Calculate, Color(0xFFF97316), Color.White)
        "clock" -> Triple(Icons.Default.Schedule, Color(0xFF6366F1), Color.White)
        else -> Triple(Icons.Default.Description, Color(0xFF475569), Color.White)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(2.dp)
            .testTag("guest_app_${app.packageName}")
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(iconBgColor)
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = app.appName, tint = iconTintColor, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = app.appName,
            color = Color(0xFFF1F5F9),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DockItem(icon: ImageVector, color: Color, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.22f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
    }
}

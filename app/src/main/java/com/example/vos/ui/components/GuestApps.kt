package com.example.vos.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.vos.data.model.GuestAppEntity
import com.example.vos.data.model.GuestStorageItemEntity
import com.example.vos.data.model.RomEntity
import com.example.vos.data.model.VirtualDeviceEntity
import com.example.vos.engine.GuestShellEngine
import com.example.vos.engine.HardwareTelemetry
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 1. GUEST SETTINGS APP
@Composable
fun GuestSettingsApp(
    device: VirtualDeviceEntity,
    rom: RomEntity,
    telemetry: HardwareTelemetry,
    onBack: () -> Unit
) {
    var selectedSection by remember { mutableStateOf("about") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
    ) {
        // App Header
        Surface(color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "System Settings",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Smartphone, contentDescription = "Phone", tint = NeonCyan, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(device.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("ROM: ${rom.name}", color = NeonCyan, fontSize = 13.sp)
                            Text("OS Version: ${rom.version}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Hardware & Isolation Specs
            item {
                Text("VIRTUAL HARDWARE & ISOLATION", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131E33)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SettingRow("Virtual CPU Cores", "${device.cpuCores} Cores (${telemetry.hostAbi})")
                        SettingRow("Allocated RAM", "${device.ramMb} MB LPDDR5")
                        SettingRow("Virtual Storage", "${device.storageMb} MB Sandboxed ext4")
                        SettingRow("Virtual Resolution", "${device.resolutionWidth} x ${device.resolutionHeight} (${device.densityDpi} DPI)")
                        SettingRow("Virtual IMEI", device.virtualImei)
                        SettingRow("Virtual MAC", device.virtualMac)
                        SettingRow("Kernel Version", device.guestKernelVersion)
                        SettingRow("Security Patch", rom.securityPatch)
                    }
                }
            }

            item {
                Text("SANDBOX SECURITY & SAFETY", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F291E)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = "Security", tint = NeonGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Host Device Untouched", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Guest OS operates in local userspace. Bootloader, system partition and real firmware are 100% untouched.", color = Color(0xFF86EFAC), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFF94A3B8), fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

// 2. GUEST TERMINAL / ROOT SHELL APP
@Composable
fun GuestTerminalApp(
    shellEngine: GuestShellEngine?,
    onBack: () -> Unit
) {
    val history = remember {
        mutableStateListOf<Pair<String, String>>(
            Pair("sys", "VOS World Sandboxed Linux Container Engine v2.4\nType 'help' for available guest commands.")
        )
    }
    var currentInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050914))
    ) {
        // Terminal Header
        Surface(color = Color(0xFF0F172A), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text("Guest Shell (root)", color = NeonCyan, fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                IconButton(onClick = { history.clear() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                }
            }
        }

        // Quick Command Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0B132B))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("uname -a", "df -h", "free", "getprop", "ps", "logcat").forEach { cmd ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            scope.launch {
                                val output = shellEngine?.executeCommand(cmd) ?: "Shell not ready"
                                history.add(Pair(cmd, output))
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(cmd, color = NeonCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // Logs Output
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history) { (cmd, out) ->
                Column {
                    if (cmd != "sys") {
                        Text("root@vos-virtual:/sdcard # $cmd", color = NeonGreen, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                    Text(out, color = Color(0xFFE2E8F0), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // Input Field
        Surface(color = Color(0xFF0F172A), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("# ", color = NeonGreen, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                OutlinedTextField(
                    value = currentInput,
                    onValueChange = { currentInput = it },
                    placeholder = { Text("Enter command...", color = Color.Gray, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("terminal_input"),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        val input = currentInput.trim()
                        if (input.isNotEmpty()) {
                            scope.launch {
                                val out = shellEngine?.executeCommand(input) ?: "Shell not initialized"
                                history.add(Pair(input, out))
                                currentInput = ""
                            }
                        }
                    })
                )
                IconButton(
                    onClick = {
                        val input = currentInput.trim()
                        if (input.isNotEmpty()) {
                            scope.launch {
                                val out = shellEngine?.executeCommand(input) ?: "Shell not initialized"
                                history.add(Pair(input, out))
                                currentInput = ""
                            }
                        }
                    },
                    modifier = Modifier.testTag("terminal_send")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Run", tint = NeonCyan)
                }
            }
        }
    }
}

// 3. GUEST FILES APP
@Composable
fun GuestFilesApp(
    storageItems: List<GuestStorageItemEntity>,
    onAddFile: (path: String, name: String, content: String) -> Unit,
    onDeleteFile: (path: String) -> Unit,
    onBack: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newFileName by remember { mutableStateOf("") }
    var newFileContent by remember { mutableStateOf("") }
    var viewingItem by remember { mutableStateOf<GuestStorageItemEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
    ) {
        Surface(color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text("Virtual Storage Explorer", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add File", tint = NeonCyan)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(storageItems) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewingItem = item }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (item.isDirectory) Icons.Default.Folder else Icons.Default.Description,
                                contentDescription = "File",
                                tint = if (item.isDirectory) NeonAmber else NeonCyan,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(item.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(item.path, color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                        IconButton(onClick = { onDeleteFile(item.path) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }

    if (viewingItem != null) {
        AlertDialog(
            onDismissRequest = { viewingItem = null },
            title = { Text(viewingItem!!.name, color = Color.White) },
            text = {
                Column {
                    Text("Path: ${viewingItem!!.path}", color = NeonCyan, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(viewingItem!!.contentText.ifEmpty { "[Binary / Empty Content]" }, color = Color(0xFFE2E8F0), fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { viewingItem = null }) {
                    Text("Close", color = NeonCyan)
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Virtual File", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        label = { Text("File Name (e.g. log.txt)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = newFileContent,
                        onValueChange = { newFileContent = it },
                        label = { Text("Content") },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFileName.isNotBlank()) {
                            onAddFile("/sdcard/$newFileName", newFileName, newFileContent)
                            newFileName = ""
                            newFileContent = ""
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Save to Guest OS", color = Color(0xFF00363D))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }
}

// 4. GUEST CAMERA APP
@Composable
fun GuestCameraApp(
    onBack: () -> Unit
) {
    var isFrontCamera by remember { mutableStateOf(false) }
    var flashMode by remember { mutableStateOf(false) }
    var capturedPhotoCount by remember { mutableIntStateOf(1) }
    var lastCapturedNotice by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("VOS Camera HAL Bridge", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { flashMode = !flashMode }) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Flash", tint = if (flashMode) NeonAmber else Color.White)
            }
        }

        // Viewport Viewfinder
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Lens",
                    tint = NeonCyan,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    if (isFrontCamera) "Front Sensor (12MP 4K Virtual Sensor)" else "Rear Primary (50MP Dual Pixel OIS Bridge)",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text("Direct V-HAL Buffer Pipeline Active", color = NeonGreen, fontSize = 11.sp)
            }

            if (lastCapturedNotice != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF00E5FF).copy(alpha = 0.85f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(lastCapturedNotice!!, color = Color(0xFF00363D), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Shutter Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { isFrontCamera = !isFrontCamera }) {
                Icon(Icons.Default.Refresh, contentDescription = "Flip", tint = Color.White, modifier = Modifier.size(28.dp))
            }

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(4.dp, NeonCyan, CircleShape)
                    .background(Color.White)
                    .clickable {
                        capturedPhotoCount++
                        lastCapturedNotice = "Saved to /sdcard/DCIM/IMG_$capturedPhotoCount.jpg"
                    }
            )

            IconButton(onClick = { /* gallery */ }) {
                Icon(Icons.Default.Folder, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
    }
}

// 5. GUEST BROWSER APP
@Composable
fun GuestBrowserApp(
    onBack: () -> Unit
) {
    var urlText by remember { mutableStateOf("https://vos.android.internal") }
    var pageTitle by remember { mutableStateOf("VOS World Virtual Web Portal") }
    var pageContent by remember { mutableStateOf("Welcome to the isolated guest web container. Network requests are safely bridged through the host network stack.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Browser URL Bar
        Surface(color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                OutlinedTextField(
                    value = urlText,
                    onValueChange = { urlText = it },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Public, contentDescription = "Web", tint = NeonCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    pageTitle = "Navigating to $urlText"
                    pageContent = "HTTP 200 OK — Sandboxed response loaded via VOS Virtual Network Layer."
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Go", tint = NeonCyan)
                }
            }
        }

        // Bookmarks
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF131E33))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("LineageOS", "AOSP Wiki", "Android Open Source", "Kernel.org").forEach { site ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .clickable {
                            urlText = "https://www.$site.org"
                            pageTitle = "$site Portal"
                            pageContent = "Browsing official documentation for $site within the virtual guest sandbox."
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(site, color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Rendered Page Content
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(pageTitle, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(12.dp))
                Text(pageContent, color = Color(0xFFCBD5E1), fontSize = 13.sp, lineHeight = 20.sp)
            }
        }
    }
}

// 6. GUEST PHONE & EMERGENCY SOS APP
@Composable
fun GuestPhoneApp(
    onBack: () -> Unit
) {
    var dialNumber by remember { mutableStateOf("") }
    var inCallState by remember { mutableStateOf(false) }
    var callDurationSec by remember { mutableIntStateOf(0) }

    LaunchedEffect(inCallState) {
        if (inCallState) {
            callDurationSec = 0
            while (inCallState) {
                delay(1000)
                callDurationSec++
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Surface(color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Phone & Emergency SOS", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (inCallState) {
            // Active Call Screen
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(NeonGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = NeonGreen, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(dialNumber, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Connected (${callDurationSec / 60}:${(callDurationSec % 60).toString().padStart(2, '0')})",
                    color = NeonGreen,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                FloatingActionButton(
                    onClick = { inCallState = false },
                    containerColor = Color(0xFFEF4444)
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White)
                }
            }
        } else {
            // Dialpad Screen
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dialed text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dialNumber.ifEmpty { "Enter number..." },
                        color = if (dialNumber.isEmpty()) Color.Gray else Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Numeric Keypad
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("*", "0", "#")
                )

                keys.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { digit ->
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B))
                                    .clickable { dialNumber += digit },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(digit, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Call Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // SOS 911 button
                    Button(
                        onClick = {
                            dialNumber = "911 (SOS)"
                            inCallState = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("SOS 911", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    FloatingActionButton(
                        onClick = {
                            if (dialNumber.isNotEmpty()) inCallState = true
                        },
                        containerColor = NeonGreen
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color(0xFF003919))
                    }

                    IconButton(onClick = {
                        if (dialNumber.isNotEmpty()) dialNumber = dialNumber.dropLast(1)
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Backspace", tint = Color.Gray)
                    }
                }
            }
        }
    }
}

// 7. GUEST GALLERY APP
@Composable
fun GuestGalleryApp(onBack: () -> Unit) {
    val samplePhotos = listOf(
        Pair("DCIM_20260827_001.jpg", "4032x3024 • 3.2 MB"),
        Pair("DCIM_20260827_002.jpg", "3840x2160 • 4.1 MB"),
        Pair("ROM_Wallpaper_Trebuchet.png", "1080x2400 • 1.8 MB"),
        Pair("Screenshot_20260827.png", "1080x2400 • 840 KB"),
        Pair("Recovery_Backup_Splash.jpg", "1920x1080 • 950 KB")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Surface(color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Gallery / Photos", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(samplePhotos) { (name, meta) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF334155)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Photo", tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(meta, color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// 8. GUEST CALCULATOR APP
@Composable
fun GuestCalculatorApp(onBack: () -> Unit) {
    var display by remember { mutableStateOf("0") }
    var operand1 by remember { mutableStateOf<Double?>(null) }
    var operator by remember { mutableStateOf<String?>(null) }
    var resetOnNextDigit by remember { mutableStateOf(false) }

    fun onDigit(d: String) {
        if (display == "0" || resetOnNextDigit) {
            display = d
            resetOnNextDigit = false
        } else {
            display += d
        }
    }

    fun onOp(op: String) {
        operand1 = display.toDoubleOrNull()
        operator = op
        resetOnNextDigit = true
    }

    fun onEqual() {
        val op1 = operand1 ?: return
        val op2 = display.toDoubleOrNull() ?: return
        val res = when (operator) {
            "+" -> op1 + op2
            "-" -> op1 - op2
            "×" -> op1 * op2
            "÷" -> if (op2 != 0.0) op1 / op2 else Double.NaN
            else -> op2
        }
        display = if (res.isNaN()) "Error" else if (res % 1.0 == 0.0) res.toLong().toString() else "%.4f".format(res).trimEnd('0').trimEnd('.')
        operand1 = null
        operator = null
        resetOnNextDigit = true
    }

    fun onClear() {
        display = "0"
        operand1 = null
        operator = null
        resetOnNextDigit = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
    ) {
        Surface(color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Calculator", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = display,
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1
                )
            }

            val rows = listOf(
                listOf("C", "±", "%", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("0", ".", "=")
            )

            rows.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { key ->
                        val isOp = key in listOf("÷", "×", "-", "+", "=")
                        val isSpecial = key in listOf("C", "±", "%")
                        val weight = if (key == "0") 2f else 1f

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = when {
                                key == "=" -> Color(0xFF0284C7)
                                isOp -> Color(0xFFF59E0B)
                                isSpecial -> Color(0xFF334155)
                                else -> Color(0xFF1E293B)
                            },
                            modifier = Modifier
                                .weight(weight)
                                .height(56.dp)
                                .clickable {
                                    when (key) {
                                        "C" -> onClear()
                                        "±" -> display = (display.toDoubleOrNull()?.times(-1) ?: 0.0).toString()
                                        "%" -> display = ((display.toDoubleOrNull() ?: 0.0) / 100.0).toString()
                                        "=" -> onEqual()
                                        in listOf("÷", "×", "-", "+") -> onOp(key)
                                        else -> onDigit(key)
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = key,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 9. GUEST CLOCK APP
@Composable
fun GuestClockApp(onBack: () -> Unit) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    var isStopwatchRunning by remember { mutableStateOf(false) }
    var stopwatchMs by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        val tf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val df = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTime = tf.format(now)
            currentDate = df.format(now)
            delay(1000)
        }
    }

    LaunchedEffect(isStopwatchRunning) {
        while (isStopwatchRunning) {
            delay(100)
            stopwatchMs += 100
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Surface(color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Clock & Stopwatch", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("VIRTUAL SYSTEM CLOCK", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(currentTime.ifEmpty { "10:00:00" }, color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Light, fontFamily = FontFamily.Monospace)
                        Text(currentDate.ifEmpty { "Thursday, Aug 27" }, color = Color(0xFF94A3B8), fontSize = 13.sp)
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("STOPWATCH", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        val secs = stopwatchMs / 1000
                        val mins = secs / 60
                        val tenths = (stopwatchMs % 1000) / 100
                        Text(
                            "${mins.toString().padStart(2, '0')}:${(secs % 60).toString().padStart(2, '0')}.$tenths",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { isStopwatchRunning = !isStopwatchRunning },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isStopwatchRunning) Color(0xFFEF4444) else Color(0xFF10B981)
                                )
                            ) {
                                Text(if (isStopwatchRunning) "Stop" else "Start", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    isStopwatchRunning = false
                                    stopwatchMs = 0L
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                            ) {
                                Text("Reset")
                            }
                        }
                    }
                }
            }
        }
    }
}

// 10. GUEST NOTES APP
@Composable
fun GuestNotesApp(onBack: () -> Unit) {
    val notes = remember {
        mutableStateListOf(
            Pair("VOS World Sandbox Note", "Running LineageOS / Pixel OS with rootless hypervisor sandbox."),
            Pair("System Configuration", "vCPU allocation: 4 cores, RAM: 4096 MB, HAL bridge: Active.")
        )
    }
    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newBody by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Surface(color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text("Guest Notes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFF38BDF8))
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(notes) { (title, body) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { notes.remove(Pair(title, body)) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(body, color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF1E293B),
            title = { Text("New Note", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Title") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newBody,
                        onValueChange = { newBody = it },
                        label = { Text("Body") },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            notes.add(Pair(newTitle, newBody))
                            newTitle = ""
                            newBody = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Text("Save Note")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}


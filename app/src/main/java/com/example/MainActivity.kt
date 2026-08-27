package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ui.theme.BentoActiveIcon
import com.example.ui.theme.BentoActivePill
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.vos.engine.VmRuntimeState
import com.example.vos.ui.screens.EmergencyHardwareScreen
import com.example.vos.ui.screens.HomeScreen
import com.example.vos.ui.screens.RomManagerScreen
import com.example.vos.ui.screens.SettingsScreen
import com.example.vos.ui.screens.VirtualPhoneScreen
import com.example.vos.viewmodel.MainTab
import com.example.vos.viewmodel.VosViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: VosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                VosMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun VosMainApp(viewModel: VosViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val roms by viewModel.roms.collectAsState()
    val devices by viewModel.virtualDevices.collectAsState()
    val defaultDevice by viewModel.defaultDevice.collectAsState()
    val backups by viewModel.backups.collectAsState()
    val activeDevice by viewModel.activeDevice.collectAsState()
    val activeRom by viewModel.activeRom.collectAsState()
    val vmState by viewModel.vmState.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()
    val bridgeStatus by viewModel.bridgeStatus.collectAsState()
    val importProgress by viewModel.importProgress.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val isSosActive by viewModel.isSosActive.collectAsState()
    val activeApp by viewModel.activeApp.collectAsState()
    val runningApps by viewModel.runningApps.collectAsState()
    val isControlCenterOpen by viewModel.isControlCenterOpen.collectAsState()

    val guestApps by viewModel.activeDeviceApps.collectAsState()
    val guestStorage by viewModel.activeDeviceStorage.collectAsState()
    val guestLogs by viewModel.activeDeviceLogs.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissUserMessage()
        }
    }

    val context = LocalContext.current
    val isGuestFullscreen = selectedTab == MainTab.VIRTUAL_PHONE && (vmState is VmRuntimeState.Running || vmState is VmRuntimeState.Paused)

    // True Edge-to-Edge Immersive Display: completely hide host Android navigation and status bars
    DisposableEffect(isGuestFullscreen) {
        val window = (context as? Activity)?.window
        if (window != null) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            if (isGuestFullscreen) {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
        onDispose {
            val window = (context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = if (isGuestFullscreen) Color(0xFF090D16) else BentoBackground,
        contentWindowInsets = if (isGuestFullscreen) WindowInsets(0, 0, 0, 0) else androidx.compose.material3.ScaffoldDefaults.contentWindowInsets,
        snackbarHost = {
            if (!isGuestFullscreen) {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        containerColor = BentoSurface,
                        contentColor = BentoTextPrimary,
                        action = {
                            data.visuals.actionLabel?.let { actionLabel ->
                                TextButton(onClick = { data.performAction() }) {
                                    Text(actionLabel, color = BentoPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    ) {
                        Text(data.visuals.message)
                    }
                }
            }
        },
        bottomBar = {
            if (!isGuestFullscreen) {
                NavigationBar(
                    containerColor = BentoBackground,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = BentoBorder.copy(alpha = 0.6f)
                        )
                        .testTag("main_navigation_bar")
                ) {
                    MainTab.values().forEach { tab ->
                        val (icon, label) = when (tab) {
                            MainTab.HOME -> Pair(Icons.Default.Home, "Home")
                            MainTab.VIRTUAL_PHONE -> Pair(Icons.Default.Smartphone, "Virtual OS")
                            MainTab.DEVICES_ROMS -> Pair(Icons.Default.Storage, "ROMs")
                            MainTab.EMERGENCY_HARDWARE -> Pair(Icons.Default.Sensors, "Emergency")
                            MainTab.SETTINGS -> Pair(Icons.Default.Settings, "Settings")
                        }

                        val isSelected = selectedTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab) },
                            icon = { Icon(imageVector = icon, contentDescription = label) },
                            label = {
                                Text(
                                    label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BentoActiveIcon,
                                selectedTextColor = BentoPrimary,
                                indicatorColor = BentoActivePill,
                                unselectedIconColor = BentoTextSecondary,
                                unselectedTextColor = BentoTextSecondary
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isGuestFullscreen) androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
        ) {
            Crossfade(targetState = selectedTab, label = "TabTransition") { tab ->
                when (tab) {
                    MainTab.HOME -> HomeScreen(
                        viewModel = viewModel,
                        roms = roms,
                        devices = devices,
                        activeDevice = activeDevice,
                        vmState = vmState,
                        telemetry = telemetry,
                        importProgress = importProgress,
                        onNavigateTab = { viewModel.selectTab(it) }
                    )
                    MainTab.VIRTUAL_PHONE -> VirtualPhoneScreen(
                        viewModel = viewModel,
                        activeDevice = activeDevice,
                        activeRom = activeRom,
                        devices = devices,
                        vmState = vmState,
                        telemetry = telemetry,
                        activeApp = activeApp,
                        runningApps = runningApps,
                        guestApps = guestApps,
                        guestStorage = guestStorage,
                        guestLogs = guestLogs,
                        isControlCenterOpen = isControlCenterOpen,
                        onNavigateTab = { viewModel.selectTab(it) }
                    )
                    MainTab.DEVICES_ROMS -> RomManagerScreen(
                        viewModel = viewModel,
                        roms = roms,
                        devices = devices,
                        backups = backups,
                        activeDevice = activeDevice,
                        vmState = vmState
                    )
                    MainTab.EMERGENCY_HARDWARE -> EmergencyHardwareScreen(
                        viewModel = viewModel,
                        telemetry = telemetry,
                        bridgeStatus = bridgeStatus,
                        isSosActive = isSosActive
                    )
                    MainTab.SETTINGS -> SettingsScreen(
                        viewModel = viewModel,
                        devices = devices,
                        defaultDevice = defaultDevice
                    )
                }
            }
        }
    }
}

package com.example.vos.engine

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HardwareTelemetry(
    val batteryLevel: Int = 85,
    val isCharging: Boolean = false,
    val batteryTemperature: Float = 31.5f,
    val batteryVoltage: Int = 4150, // mV
    val isWifiConnected: Boolean = true,
    val isCellularConnected: Boolean = true,
    val networkSpeedMbps: Int = 120,
    val accelX: Float = 0.0f,
    val accelY: Float = 9.8f,
    val accelZ: Float = 0.0f,
    val lightLux: Float = 180f,
    val isGpsEnabled: Boolean = true,
    val isCameraAvailable: Boolean = true,
    val cpuUsagePercent: Int = 24,
    val hostAbi: String = Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a",
    val hostModel: String = "${Build.MANUFACTURER} ${Build.MODEL}",
    val hostAndroidVersion: String = Build.VERSION.RELEASE
)

data class HardwareBridgeStatus(
    val cpuVirtualization: Boolean = true,
    val ramMemoryBridge: Boolean = true,
    val storageSandboxing: Boolean = true,
    val displayFrameBuffer: Boolean = true,
    val batteryTelemetry: Boolean = true,
    val networkBridge: Boolean = true,
    val sensorsPassthrough: Boolean = true,
    val locationBridge: Boolean = true,
    val cameraPassthrough: Boolean = true,
    val audioHapticsBridge: Boolean = true
)

class VirtualHal(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

    private val _telemetry = MutableStateFlow(HardwareTelemetry())
    val telemetry: StateFlow<HardwareTelemetry> = _telemetry.asStateFlow()

    private val _bridgeStatus = MutableStateFlow(HardwareBridgeStatus())
    val bridgeStatus: StateFlow<HardwareBridgeStatus> = _bridgeStatus.asStateFlow()

    private var isListeningSensors = false

    fun startTelemetryMonitoring() {
        refreshBatteryAndNetwork()
        if (!isListeningSensors && sensorManager != null) {
            accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
            lightSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
            isListeningSensors = true
        }
    }

    fun stopTelemetryMonitoring() {
        if (isListeningSensors && sensorManager != null) {
            sensorManager.unregisterListener(this)
            isListeningSensors = false
        }
    }

    fun refreshBatteryAndNetwork() {
        var batteryPct = 85
        var isCharging = false
        var tempC = 31.0f
        var voltageMv = 4100

        try {
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, ifilter)
            batteryStatus?.let {
                val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (level >= 0 && scale > 0) {
                    batteryPct = (level * 100 / scale.toFloat()).toInt()
                }
                val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                tempC = it.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 300) / 10.0f
                voltageMv = it.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4100)
            }
        } catch (e: Exception) {
            // Safe fallback
        }

        var isWifi = true
        var isCell = true
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val activeNet = cm?.activeNetwork
            val caps = cm?.getNetworkCapabilities(activeNet)
            if (caps != null) {
                isWifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                isCell = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            }
        } catch (e: Exception) {
            // Safe fallback
        }

        var isGps = false
        try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            isGps = lm?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
        } catch (e: Exception) {
            // Safe fallback
        }

        _telemetry.value = _telemetry.value.copy(
            batteryLevel = batteryPct,
            isCharging = isCharging,
            batteryTemperature = tempC,
            batteryVoltage = voltageMv,
            isWifiConnected = isWifi,
            isCellularConnected = isCell,
            isGpsEnabled = isGps,
            cpuUsagePercent = (15..45).random()
        )
    }

    fun triggerVibration(durationMs: Long = 40L) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    fun toggleBridgeFeature(feature: String, enabled: Boolean) {
        val current = _bridgeStatus.value
        _bridgeStatus.value = when (feature) {
            "sensors" -> current.copy(sensorsPassthrough = enabled)
            "location" -> current.copy(locationBridge = enabled)
            "camera" -> current.copy(cameraPassthrough = enabled)
            "network" -> current.copy(networkBridge = enabled)
            "battery" -> current.copy(batteryTelemetry = enabled)
            else -> current
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                _telemetry.value = _telemetry.value.copy(
                    accelX = event.values[0],
                    accelY = event.values[1],
                    accelZ = event.values[2]
                )
            }
            Sensor.TYPE_LIGHT -> {
                _telemetry.value = _telemetry.value.copy(
                    lightLux = event.values[0]
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

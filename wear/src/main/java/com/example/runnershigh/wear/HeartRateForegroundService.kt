package com.example.runnershigh.wear

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class HeartRateForegroundService : Service(), SensorEventListener {

    companion object {
        const val ACTION_START = "action_start"
        const val ACTION_STOP = "action_stop"
        private const val CHANNEL_ID = "hr_stream_channel"
        private const val NOTIFICATION_ID = 3301
    }

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private val tilePrefs by lazy {
        getSharedPreferences(HealthTileService.PREFS_NAME, MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startStreaming()
            ACTION_STOP -> stopStreaming()
        }
        return START_STICKY
    }

    private fun startStreaming() {
        tilePrefs.edit().putLong(HealthTileService.KEY_EXERCISE_START_TIME, System.currentTimeMillis()).apply()
        createChannelIfNeeded()
        startForeground(NOTIFICATION_ID, buildNotification())
        heartRateSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun stopStreaming() {
        tilePrefs.edit().putInt(HealthTileService.KEY_CURRENT_BPM, 0).apply()
        sensorManager.unregisterListener(this)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val bpm = event?.values?.firstOrNull()?.toInt() ?: return
        if (bpm <= 0) return

        tilePrefs.edit().putInt(HealthTileService.KEY_CURRENT_BPM, bpm).apply()
        val now = System.currentTimeMillis()
        val request = PutDataMapRequest.create(WearHeartRateContract.HEART_RATE_PATH).apply {
            dataMap.putInt(WearHeartRateContract.KEY_BPM, bpm)
            dataMap.putInt(WearHeartRateContract.KEY_BPM_HEART_RATE, bpm)
            dataMap.putInt(WearHeartRateContract.KEY_BPM_HEART_RATE_CAMEL, bpm)
            dataMap.putLong(WearHeartRateContract.KEY_TIMESTAMP, now)
        }.asPutDataRequest().setUrgent()

        Wearable.getDataClient(this).putDataItem(request)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(com.example.runnershigh.wear.R.string.hr_notification_channel),
                NotificationManager.IMPORTANCE_LOW
            )
            nm.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(getString(com.example.runnershigh.wear.R.string.app_name))
            .setContentText(getString(com.example.runnershigh.wear.R.string.hr_notification_text))
            .setOngoing(true)
            .build()
}

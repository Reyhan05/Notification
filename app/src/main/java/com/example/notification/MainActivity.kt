package com.example.notification

import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.notification.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var notificationManager: NotificationManager? = null
    private var CHANNEL_ID = "channel_id"
    private val KEY_TEXT_REPLY = "key_text_reply"


    private lateinit var countDownTimer: CountDownTimer
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Register channel ke dalam sistem
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(CHANNEL_ID, "Countdown", "Ini adalah desk")

        binding.btnStart.setOnClickListener {
            countDownTimer.start()
        }

        countDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(p0: Long) {
                // Masukan text dari string
                binding.timer.text = getString(R.string.time_reamining, p0 / 1000)
            }

            override fun onFinish() {
                displayNotification()
            }

        }

    }

    private fun displayNotification() {
        val notifId = 40

        // tambahkan kode
        val intent = Intent(this, notifikasiActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val snoozeIntent = Intent(this, notifikasiActivity::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ID, 0)
        }
        val snoozePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, snoozeIntent, 0)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // untuk menampilkan notifikasi
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Timer")
            .setContentText("Waktunya Habis")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Waktu Habis")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_delete, "Snooze",
                snoozePendingIntent
            )
            .build()

        notificationManager?.notify(notifId, builder)
    }

    private fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        // validasi notif akan dibuat juga version SDK 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

}
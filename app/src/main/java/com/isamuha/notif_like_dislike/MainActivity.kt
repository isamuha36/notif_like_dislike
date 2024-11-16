package com.isamuha.notif_like_dislike

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.isamuha.notif_like_dislike.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val CHANNEL_ID = "like_dislike_channel"
    private val NOTIFICATION_ID = 1
    private val LIKE_ACTION = "com.isamuha.notif_like_dislike.LIKE_ACTION"
    private val DISLIKE_ACTION = "com.isamuha.notif_like_dislike.DISLIKE_ACTION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()

        val filter = IntentFilter().apply {
            addAction(LIKE_ACTION)
            addAction(DISLIKE_ACTION)
        }
        registerReceiver(notificationReceiver, filter)

        binding.btnNotif.setOnClickListener {
            showNotification()
        }
    }

    private val notificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                LIKE_ACTION -> {
                    // Update like total
                    val currentLike = binding.likeTotal.text.toString().toInt()
                    binding.likeTotal.text = (currentLike + 1).toString()

                    // Cancel the notification after like action is pressed
                    NotificationManagerCompat.from(context!!).cancel(NOTIFICATION_ID)
                }
                DISLIKE_ACTION -> {
                    // Update dislike total
                    val currentDislike = binding.dislikeTotal.text.toString().toInt()
                    binding.dislikeTotal.text = (currentDislike + 1).toString()

                    // Cancel the notification after dislike action is pressed
                    NotificationManagerCompat.from(context!!).cancel(NOTIFICATION_ID)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Like/Dislike Channel"
            val descriptionText = "Channel for Like and Dislike notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification() {
        // Intent for Like action
        val likeIntent = Intent(LIKE_ACTION)
        val likePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, likeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Intent for Dislike action
        val dislikeIntent = Intent(DISLIKE_ACTION)
        val dislikePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, dislikeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Load image from drawable resources
        val bigPicture = BitmapFactory.decodeResource(resources, R.drawable.ijat)

        // Build the notification
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)  // Small icon for notification
            .setContentTitle("Apakah gwejh gamteng???")
            .setContentText("Apakah kamu suka?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigPictureStyle()  // Set the style to BigPictureStyle
                    .bigPicture(bigPicture)  // Use the loaded bitmap as the big picture
                    .setBigContentTitle("iki rai mu")
                    .setSummaryText("Apakah kamu suka dengan gambar ini?")
            )
            .addAction(0, "Like", likePendingIntent) // Add Like action
            .addAction(0, "Dislike", dislikePendingIntent) // Add Dislike action
            .setAutoCancel(true)  // Dismiss the notification when clicked

        with(NotificationManagerCompat.from(this)) {
            try {
                notify(NOTIFICATION_ID, builder.build())
            } catch (e: SecurityException) {
                e.printStackTrace()
                // Handle the case where permission hasn't been granted
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
    }
}

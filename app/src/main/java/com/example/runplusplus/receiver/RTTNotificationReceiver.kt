package com.example.runplusplus.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.runplusplus.R
import com.example.runplusplus.view.RTTGiornoActivity

//gestisce le notifice
class RTTNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val titolo = intent.getStringExtra("titolo") ?: "Allenamento RTT"
        val messaggio = intent.getStringExtra("messaggio") ?: "Hai un allenamento oggi!"

        val channelId = "rtt_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "RTT Notifiche", NotificationManager.IMPORTANCE_HIGH) //invia la notifica con suono e vibrazione
            notificationManager.createNotificationChannel(channel)
        }

        //apre la schermata dell'allenamento del giorno
        val openIntent = Intent(context, RTTGiornoActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            2001, //request code
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE //per migliore sicurezza
        )

        //costruzione della notifica
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(titolo)
            .setContentText(messaggio)
            .setSmallIcon(R.drawable.ic_notification_special)
            .setContentIntent(pendingIntent) //collega la schermata al click
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2001, notification)
    }
}
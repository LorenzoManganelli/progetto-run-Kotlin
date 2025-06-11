package com.example.runplusplus.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.runplusplus.R
import com.example.runplusplus.view.DettagliAllenamentiCalendarioActivity

class NotificaAllenamentoReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val titolo = intent?.getStringExtra("titolo") ?: "Allenamento in programma!"
        val messaggio = intent?.getStringExtra("messaggio") ?: "È ora di allenarti!"
        val dataAllenamento = intent?.getStringExtra("data") ?: return

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "allenamento_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notifiche Allenamenti",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        //apre l'app sul giorno della notifica NOTA BENE: PER DOPO QUESTA POTREBBE ESSERE DA CAMBIARE O ADATTARE PER ALTRE NOTIFICHE
        val openIntent = Intent(context, DettagliAllenamentiCalendarioActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("data", dataAllenamento)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titolo)
            .setContentText(messaggio)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify((0..10000).random(), notification) //distingue le notifiche tra loro dandogli un numero random
    }
}
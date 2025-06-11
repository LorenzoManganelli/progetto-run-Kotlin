package com.example.runplusplus.util
// è usato per tutte e 3 i tipi di notifiche del sistema (la guida dice che si può mettere nelle activity DA RIVEDERE)

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.example.runplusplus.model.CalendarioAllenamento
import com.example.runplusplus.model.RTTAttivo
import com.example.runplusplus.receiver.NotificaAllenamentoReceiver
import com.example.runplusplus.receiver.RTTNotificationReceiver
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import kotlin.jvm.java

fun scheduleAllenamentoNotification(context: Context, allenamento: CalendarioAllenamento) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    //questo è quello che BroadcastReceiver riceverà
    val intent = Intent(context, NotificaAllenamentoReceiver::class.java).apply {
        putExtra("titolo", "Allenamento: ${allenamento.tipo}")
        putExtra("messaggio", "Hai un allenamento oggi alle ${allenamento.ora}")
        putExtra("data", allenamento.data.toString())
    }

    val requestCode = allenamento.id
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val zonedDateTime = ZonedDateTime.of(allenamento.data, allenamento.ora, ZoneId.systemDefault())
    val triggerAtMillis = zonedDateTime.toInstant().toEpochMilli()
        //blocco all'apparenza MOLTO importante per sicurezza, tenendo conto di permessi e API
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                Toast.makeText(
                    context,
                    "Permesso per allarmi precisi non abilitato. Controlla le impostazioni.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    } catch (e: SecurityException) {
        e.printStackTrace()
        Toast.makeText(context, "Errore: permesso per allarmi precisi negato", Toast.LENGTH_SHORT).show()
    }
}

//ID fisso per RTT
private const val RTT_REQUEST_CODE = 2000

fun scheduleRTTNotification(context: Context, rtt: RTTAttivo) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, RTTNotificationReceiver::class.java).apply {
        putExtra("programmaId", rtt.id)
        putExtra("nome", rtt.nome)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        RTT_REQUEST_CODE,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    //Calendar per il trigger giornaliero delle notifiche
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, rtt.orarioNotifica.hour)
        set(Calendar.MINUTE, rtt.orarioNotifica.minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (before(Calendar.getInstance())) {
            add(Calendar.DAY_OF_MONTH, 1) //pianifica per il giorno successivo se orario passato
        }
    }

    //questo praticamente fa in modo che la notifica è giornaliera
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY, //questo
                    pendingIntent
                )
            } else {
                Toast.makeText(context, "Permesso per allarmi precisi non abilitato", Toast.LENGTH_LONG).show()
            }
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    } catch (e: SecurityException) {
        e.printStackTrace()
        Toast.makeText(context, "Errore: permesso negato per notifiche RTT", Toast.LENGTH_SHORT).show()
    }
}

fun cancellaRTTNotification(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, RTTNotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        RTT_REQUEST_CODE,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}
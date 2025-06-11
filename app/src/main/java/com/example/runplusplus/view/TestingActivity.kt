package com.example.runplusplus.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.runplusplus.R
import com.example.runplusplus.database.AppDatabase
import com.example.runplusplus.receiver.RTTNotificationReceiver
import com.example.runplusplus.view.MainActivity
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

//spostato qui il forzamento dell'RTT dalla schermata iniziale, semplicemente fa quello che il normale scorrere dei giorni farebbe solo istantaneo. Magari si potrebbe convertire in una feature vera
class TestingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing)

        val btnSimula = findViewById<Button>(R.id.btnSimulaNotificaRTT)
        val btnForzaGiorno = findViewById<Button>(R.id.btnForzaProssimoGiornoRTT)

        btnSimula.setOnClickListener {
            lifecycleScope.launch {
                val dao = AppDatabase.getDatabase(this@TestingActivity).rttAttivoDao()
                val attivo = dao.getRTTAttivoNow()
                if (attivo == null) {
                    Toast.makeText(this@TestingActivity, "Nessun RTT attivo", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val giornoCorrente = TimeUnit.MILLISECONDS
                    .toDays(System.currentTimeMillis() - attivo.giornoInizio)
                    .toInt() + 1

                val intent = Intent(this@TestingActivity, RTTNotificationReceiver::class.java).apply {
                    putExtra("titolo", "Allenamento RTT – Giorno $giornoCorrente")
                    putExtra("messaggio", "Clicca per vedere l’allenamento del giorno $giornoCorrente")
                }

                sendBroadcast(intent)
                Toast.makeText(this@TestingActivity, "Notifica simulata per giorno $giornoCorrente", Toast.LENGTH_SHORT).show()
            }
        }

        btnForzaGiorno.setOnClickListener {
            lifecycleScope.launch {
                val dao = AppDatabase.getDatabase(this@TestingActivity).rttAttivoDao()
                val attivo = dao.getRTTAttivoNow()

                if (attivo == null) {
                    Toast.makeText(this@TestingActivity, "Nessun RTT attivo", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val giornoCorrente = TimeUnit.MILLISECONDS
                    .toDays(System.currentTimeMillis() - attivo.giornoInizio)
                    .toInt() + 1

                val nuovoInizio = attivo.giornoInizio - TimeUnit.DAYS.toMillis(1)
                val nuovoRTT = attivo.copy(giornoInizio = nuovoInizio)
                dao.attivaRTT(nuovoRTT)

                val intent = Intent(this@TestingActivity, RTTNotificationReceiver::class.java).apply {
                    putExtra("titolo", "Allenamento RTT – Giorno ${giornoCorrente + 1}")
                    putExtra("messaggio", "Clicca per scoprire l’allenamento di oggi.")
                }

                sendBroadcast(intent)
                Toast.makeText(this@TestingActivity, "Giorno forzato a ${giornoCorrente + 1}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

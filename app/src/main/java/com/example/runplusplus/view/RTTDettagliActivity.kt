package com.example.runplusplus.view

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.runplusplus.R
import androidx.appcompat.widget.Toolbar
import com.example.runplusplus.model.RTTAttivo
import com.example.runplusplus.model.RTTProgramma
import com.example.runplusplus.viewmodel.RTTViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalTime
import java.util.*

class RTTDettagliActivity : AppCompatActivity() {

    private lateinit var viewModel: RTTViewModel
    private lateinit var btnAttiva: Button
    private lateinit var btnDisattiva: Button
    private lateinit var btnOra: Button
    private lateinit var oraSelezionata: LocalTime
    private lateinit var textOraSelezionata: TextView

    private lateinit var programma: RTTProgramma

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rtt_dettagli)

        //toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Run++"
        supportActionBar?.subtitle = "Dettagli RTT"

        programma = intent.getSerializableExtra("programma") as RTTProgramma

        //preparazione pulsanti
        val textNome = findViewById<TextView>(R.id.textNomeRTTDettaglio)
        val textInfo = findViewById<TextView>(R.id.textInfoRTTDettaglio)
        btnAttiva = findViewById(R.id.btnAttiva)
        btnDisattiva = findViewById(R.id.btnDisattiva)
        btnOra = findViewById(R.id.btnImpostaOra)
        textOraSelezionata = findViewById(R.id.textOraSelezionata)

        textNome.text = programma.nome
        textInfo.text = "${programma.tipologia} • ${programma.difficolta} • ${programma.durataGiorni} giorni"

        oraSelezionata = LocalTime.of(8, 0) // Default

        btnOra.setOnClickListener {
            val cal = Calendar.getInstance()
            //prende l'ora e i minuti (e con un aggiunta fatta dopo per evitare che ti invii la notifica più volte in un giorno solo)
            val timePickerDialog = TimePickerDialog(
                ContextThemeWrapper(this, R.style.CustomTimePickerDialog),
                { _, hour, minute -> //lambda che controlla l'ora e i minuti esatti per confermare la scelta
                oraSelezionata = LocalTime.of(hour, minute)
                textOraSelezionata.text = "Ora selezionata: ${oraSelezionata}"

                val adesso = LocalTime.now() //servono per permettere
                val oggi = java.time.LocalDate.now() //non usato

                //aggiorna l'orario della notifica, dato che quello sopra funzionava solo per il primo
                viewModel.setAttivo.value?.let { attivo ->
                    if (attivo.id == programma.id) {
                        //questo sistema permette, se inserita una data successiva nello stesso giorno, di far inviare la notifica il giorno dopo se l'hai già ricevuta
                        val nuovoGiornoInizio = if (oraSelezionata.isBefore(adesso)) { //se l'ora è già passata
                            System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(1) //lo mette un giorno dopo (mettendolo in millisecondi)
                        } else {
                            System.currentTimeMillis()
                        }

                        val nuovoAttivo = attivo.copy(orarioNotifica = oraSelezionata, giornoInizio = nuovoGiornoInizio) //crea una copia con il giorno nuovo

                        viewModel.attivaSet(this, nuovoAttivo) //sovrascrive il vecchio
                        Toast.makeText(this, "Orario aggiornato per il prossimo giorno!", Toast.LENGTH_SHORT).show()
                    }
                }

                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[RTTViewModel::class.java]

        viewModel.setAttivo.observe(this) { attivo ->
            if (attivo?.id == programma.id) {
                btnAttiva.isEnabled = false
                btnDisattiva.isEnabled = true
                btnAttiva.text = "Già attivo"
                //mostra l'ora salvata se è attivo
                textOraSelezionata.text = "Ora selezionata: ${attivo.orarioNotifica}"
            } else if (attivo != null) {
                btnAttiva.text = "Sostituisci set attivo"
                btnAttiva.isEnabled = true
                btnDisattiva.isEnabled = false
                //NON mostra l'ora salvata se è attivo un'altro
                textOraSelezionata.text = ""
            } else {
                btnAttiva.text = "Attiva"
                btnAttiva.isEnabled = true
                btnDisattiva.isEnabled = false
                //NON mostra l'ora salvata se è non c'è nulla attivo
                textOraSelezionata.text = ""
            }
        }

        //pulsanti della schermata
        btnAttiva.setOnClickListener {
            viewModel.resetProgresso(programma.id) //resetta il programma
            val rtt = RTTAttivo(
                id = programma.id,
                nome = programma.nome,
                orarioNotifica = oraSelezionata,
                giornoInizio = System.currentTimeMillis()
            )
            viewModel.attivaSet(this, rtt)
            Toast.makeText(this, "Set attivato!", Toast.LENGTH_SHORT).show()
        }

        btnDisattiva.setOnClickListener {
            viewModel.disattivaSet(this)
            Toast.makeText(this, "Set disattivato!", Toast.LENGTH_SHORT).show()
        }

        val btnApriGiorno = findViewById<Button>(R.id.btnApriGiornoCorrente)

        //mostra l'allenamento del giorno corrente DOPO che è arrivata la notifica o se si tratta del primo giorno
        btnApriGiorno.setOnClickListener {
            viewModel.setAttivo.observe(this) { attivo ->
                if (attivo == null || attivo.id != programma.id) {
                    Toast.makeText(this, "Questo programma non è attivo.", Toast.LENGTH_SHORT).show()
                    return@observe
                }

                val giornoCorrente = java.util.concurrent.TimeUnit.MILLISECONDS
                    .toDays(System.currentTimeMillis() - attivo.giornoInizio)
                    .toInt() + 1

                val intent = Intent(this, RTTGiornoActivity::class.java)
                startActivity(intent)
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.remind_to_train
        bottomNav.menu.findItem(R.id.remind_to_train).isEnabled = false

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.allenamenti -> {
                    startActivity(Intent(this, AllenamentiActivity::class.java))
                    true
                }
                R.id.calendario -> {
                    startActivity(Intent(this, CalendarioActivity::class.java))
                    true
                }
                R.id.allenamenti_precaricati -> {
                    startActivity(Intent(this, RemindToTrainActivity::class.java))
                    true
                }
                R.id.test -> {
                    startActivity(Intent(this, TestingActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

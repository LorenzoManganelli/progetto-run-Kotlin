package com.example.runplusplus.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.runplusplus.R
import androidx.appcompat.widget.Toolbar
import com.example.runplusplus.database.AppDatabase
import com.example.runplusplus.model.RTTGiorno
import com.example.runplusplus.viewmodel.RTTViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RTTGiornoActivity : AppCompatActivity() {

    private lateinit var viewModel: RTTViewModel

    private lateinit var textGiornoCorrente: TextView
    private lateinit var textTitolo: TextView
    private lateinit var textDescrizione: TextView
    private lateinit var btnCompletato: Button
    private lateinit var btnTorna: Button
    private lateinit var btnVediValutazione: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rtt_giorno)

        //toolbar come al solito
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Run++"
        supportActionBar?.subtitle = "Programmi RTT"

        textGiornoCorrente = findViewById(R.id.textGiornoCorrente)
        textTitolo = findViewById(R.id.textTitolo)
        textDescrizione = findViewById(R.id.textDescrizione)
        btnCompletato = findViewById(R.id.btnCompletato)
        btnTorna = findViewById(R.id.btnTorna)
        btnVediValutazione = findViewById(R.id.btnVediValutazione)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[RTTViewModel::class.java]

        viewModel.setAttivo.observe(this) { attivo ->
            if (attivo == null) {
                Toast.makeText(this, "Nessun set RTT attivo.", Toast.LENGTH_SHORT).show() //se nessuno è attivo fa apparire questo messaggio
                finish()
                return@observe
            }

            //tiene conto dei giorni, convertendoli da millisecondi
            val giorniPassati = TimeUnit.MILLISECONDS.toDays(
                System.currentTimeMillis() - attivo.giornoInizio
            ).toInt() + 1

            //operazioni Firestore
            val db = FirebaseFirestore.getInstance()

            db.collection("rtt_sets")
                .document(attivo.id.toString())
                .get()
                .addOnSuccessListener { doc ->
                    val durata = doc.getLong("durataGiorni")?.toInt() ?: 0 //il valore di durata parte sempre a zero (se manca o se non c'è)

                    //programma finito e l'else è programma che continua
                    if (giorniPassati > durata) {
                        textGiornoCorrente.text = "Programma concluso!"
                        textTitolo.text = ""
                        textDescrizione.text = "Complimenti per aver finito il programma!"
                        btnCompletato.isEnabled = false

                        btnVediValutazione.visibility = View.VISIBLE //pulsante per vedere la valutazione appare
                        btnVediValutazione.setOnClickListener {
                            val intent = Intent(this, ValutazioneFinaleRTTActivity::class.java).apply {
                                putExtra("programmaId", attivo.id)
                                putExtra("durataGiorni", durata)
                            }
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        textGiornoCorrente.text = "Giorno $giorniPassati"
                        //dettagli del giorno
                        db.collection("rtt_sets")
                            .document(attivo.id.toString())
                            .collection("giorni")
                            .document(giorniPassati.toString())
                            .get()
                            .addOnSuccessListener { docGiorno ->
                                val giorno = docGiorno.toObject(RTTGiorno::class.java)
                                if (giorno != null) {
                                    textTitolo.text = giorno.titolo
                                    textDescrizione.text = giorno.descrizione
                                } else {
                                    textTitolo.text = "Errore"
                                    textDescrizione.text = "Impossibile recuperare i dati."
                                }
                            }

                        //fa il check per vedere se il giorno è completato o meno, così che l'utente non lo può premere di nuovo
                        checkCompletamento(attivo.id, giorniPassati)

                        //il pulsante per "completare" il giorno
                        btnCompletato.setOnClickListener {
                            viewModel.aggiungiGiornoCompletato(attivo.id, giorniPassati)
                            btnCompletato.isEnabled = false
                            btnCompletato.text = "Completato"
                            Toast.makeText(this, "Giorno completato!", Toast.LENGTH_SHORT).show()

                            if (giorniPassati >= durata) {
                                viewModel.disattivaSet(this)
                                val intent = Intent(this, ValutazioneFinaleRTTActivity::class.java).apply {
                                    putExtra("programmaId", attivo.id)
                                    putExtra("durataGiorni", durata)
                                }
                                startActivity(intent)
                            }

                            finish()
                        }
                    }
                }

            btnTorna.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    //messo separato dato che creava parecchia confusione
    private fun checkCompletamento(programmaId: Int, giorno: Int) {
        val dao = AppDatabase.getDatabase(applicationContext).rttProgressoDao() //ottieni il DAO per accedere al db
        lifecycleScope.launch {
            val progresso = dao.getProgressoNow(programmaId) //recupera la giornata attuale
            val completati = progresso?.giorniCompletati ?: emptyList() //recupera i giorni completati

            //pulsante  che cambia quando viene premuto
            if (completati.contains(giorno)) {
                btnCompletato.isEnabled = false
                btnCompletato.text = "Completato"
            } else {
                btnCompletato.isEnabled = true
                btnCompletato.text = "Segna come completato"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

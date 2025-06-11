package com.example.runplusplus.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.runplusplus.R
import com.example.runplusplus.database.AppDatabase
import com.example.runplusplus.model.RTTGiorno
import com.example.runplusplus.model.RTTProgramma
import com.example.runplusplus.repository.AllenamentoRepository
import com.example.runplusplus.viewmodel.AllenamentoViewModel
import com.example.runplusplus.viewmodel.AllenamentoViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var allenamentoViewModel: AllenamentoViewModel
    //crea la schermata e attiva il layout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) //NB: questo layout è solo un "benvenuto" all'utente

        // è il setup della toolbar in alto
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply { //applica le caratteristiche sotto alla barra
            setDisplayHomeAsUpEnabled(false) // nessuna freccia indietro in home chiaramente
            title = "Run++"
            subtitle = "Benvenuto su Run++ :)"
        }

        //parte sul database, la metto quà così si attiva all'accensione del programma
        val database = AppDatabase.getDatabase(this) //prende e prepara i dati dal database
        val allenamentoDao =
            database.allenamentoDao() //NOTA ricorda che l'acronimo del DAO è Data Access Object (IE definisce le query)
        val repository =
            AllenamentoRepository(allenamentoDao) //la repository fornisce i dati al ViewModel

        // inizializza il ViewModel
        allenamentoViewModel = ViewModelProvider(
            this,
            AllenamentoViewModelFactory(repository)
        ).get(AllenamentoViewModel::class.java)

        // caricaRTTInFirestore() metodo barbarico da chiamare se serve popolare il db in Firestore

        // questa parte è a proposito della funzione "schermata iniziale" dov'è l'utente può scegliere quale è la schermata alla apertura del programma
        val prefs = getSharedPreferences("impostazioni_app", MODE_PRIVATE)
        val classeHome = prefs.getString("schermata_iniziale", MainActivity::class.java.name)

        // se questa activity non è la home salvata, reindirizza a quella scelta
        if (classeHome != this::class.java.name) {
            try {
                val activityClass = Class.forName(classeHome)
                startActivity(Intent(this, activityClass))
                finish()
                return
            } catch (e: ClassNotFoundException) { //una eccezione dato che per mi ha fatto errore una volta
                e.printStackTrace()
                Toast.makeText(this, "Errore: schermata salvata non trovata.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // parte per la barra sotto con tutti i collegamenti alle varie schermate
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.allenamenti -> startActivity(Intent(this, AllenamentiActivity::class.java))
                R.id.calendario -> startActivity(Intent(this, CalendarioActivity::class.java))
                R.id.allenamenti_precaricati -> startActivity(
                    Intent(
                        this,
                        AllenamentiPrecaricatiActivity::class.java
                    )
                )

                R.id.remind_to_train -> startActivity(
                    Intent(
                        this,
                        RemindToTrainActivity::class.java
                    )
                )

                R.id.test -> startActivity(
                    Intent(
                        this,
                        TestingActivity::class.java
                    )
                ) //va chiaramente rimossa alla release dell'app, ma la tengo per ora
                else -> null
            } != null
        }

        //controlla tutti i cambiamenti
        allenamentoViewModel.allAllenamenti.observe(this) { allenamenti ->
            Log.d("MainActivity", "Allenamenti osservati: ${allenamenti.size}")
        }
    }
    // menù in alto a destra per il setup della schermata principlae
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // gestione delle azioni del menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.imposta_home -> {
                val prefs = getSharedPreferences("impostazioni_app", MODE_PRIVATE)
                prefs.edit().putString("schermata_iniziale", this::class.java.name).apply()
                Toast.makeText(this, "Schermata iniziale salvata!", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // caricamento manuale su Firestore tramite codice, semplicemente vanno riempite le varie parti
    private fun caricaRTTInFirestore() {
        val db = FirebaseFirestore.getInstance()

        // definizione base del programma RTT
        val programma = RTTProgramma(
            id = 2,
            nome = "TEST",
            tipologia = "Corsa",
            difficolta = "Intermedio",
            durataGiorni = 3
        )

        // giorni del programma
        val giorni = listOf(
            RTTGiorno(1, "Giorno 1 TEST1", "KILL"),
            RTTGiorno(2, "Giorno 2 TEST2", "MAIM"),
            RTTGiorno(3, "Giorno 3 TEST3", "BURN")
        )

        val programmaDoc = db.collection("rtt_sets").document(programma.id.toString()) //fa accedere alla raccolta del db

        // carica programma e relativi giorni in Firestore
        programmaDoc.set(programma) //Firestore trasformerà l'oggetto Kotlin in una mappa JSON salvata nel documento in questa linea per poi caricare i singoli giorni
            .addOnSuccessListener {
                val giorniRef = programmaDoc.collection("giorni")
                for (giorno in giorni) {
                    giorniRef.document(giorno.numero.toString()).set(giorno)
                }
            }
            .addOnFailureListener { e ->
                println("Errore nel salvataggio RTT: ${e.message}") //eccezione generale
            }
        }
}

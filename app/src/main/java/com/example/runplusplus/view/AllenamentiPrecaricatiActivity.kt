package com.example.runplusplus.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runplusplus.R
import com.example.runplusplus.model.AllenamentoPrecaricato
import com.example.runplusplus.adapter.AllenamentiPrecaricatiAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class AllenamentiPrecaricatiActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerCategoria: Spinner
    private lateinit var inputNomeFiltro: EditText
    private lateinit var btnCerca: Button
    private lateinit var spinnerDifficoltaFiltro: Spinner


    private lateinit var adapter: AllenamentiPrecaricatiAdapter
    private var listaAllenamenti: List<AllenamentoPrecaricato> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allenamenti_precaricati)

        // inizializza la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Run++"
            subtitle = "Allenamenti Precaricati"
        }

        //prepara lo spinner e il dropdown con i tre elementi
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        recyclerView = findViewById(R.id.recyclerAllenamenti)

        spinnerCategoria.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Corsa", "Pesi", "Ginnastica")
        )

        recyclerView.layoutManager = LinearLayoutManager(this)

        //carica tutti gli allenamenti precaricati
        listaAllenamenti = caricaAllenamentiPredefiniti()

        //mostra la schermata di default su Corsa
        mostraAllenamentiPerCategoria("Corsa")

        //mostra la categoria corretta per ogni parte del programm
        spinnerCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val categoria = spinnerCategoria.selectedItem.toString()
                mostraAllenamentiPerCategoria(categoria)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //preparazione filtro e ricerca
        inputNomeFiltro = findViewById(R.id.inputNomeFiltro)
        btnCerca = findViewById(R.id.btnCerca)

        //difficoltà allo stesso modo
        spinnerDifficoltaFiltro = findViewById(R.id.spinnerDifficoltaFiltro)
        spinnerDifficoltaFiltro.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Tutte", "Facile", "Intermedio", "Difficile")
        )

        //attivazione filtro tramite pulsante (ho preferito fare SOLO a premuta del pulsante per comodità)
        btnCerca.setOnClickListener {
            val nome = inputNomeFiltro.text.toString()
            val diff = spinnerDifficoltaFiltro.selectedItem.toString()
            filtraAllenamenti(spinnerCategoria.selectedItem.toString(), nome, diff)
        }

        //barra sotto come sempre
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.allenamenti_precaricati
        bottomNav.menu.findItem(R.id.allenamenti_precaricati).isEnabled = false

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
                R.id.remind_to_train -> {
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

    //il filtro fa il suo lavoro
    private fun mostraAllenamentiPerCategoria(tipo: String) {
        val filtrati = listaAllenamenti.filter { it.tipo == tipo }.sortedByDescending { it.preferito }
        adapter = AllenamentiPrecaricatiAdapter(filtrati, this)
        recyclerView.adapter = adapter
    }
    //i dati di per se degli allenamento, possono essere aumentati facilmente in teoria, ma per ora li tengo hardcoded
    private fun caricaAllenamentiPredefiniti(): List<AllenamentoPrecaricato> {
        val prefs = getSharedPreferences("preferiti_precaricati", MODE_PRIVATE)
        val preferitiSalvati = prefs.getStringSet("preferiti", emptySet()) ?: emptySet()

        val allenamenti = listOf(
            AllenamentoPrecaricato(1, "Corsa base", "Corsa", "20 minuti a ritmo moderato per migliorare la resistenza di base.", "Facile"),
            AllenamentoPrecaricato(2, "Sprint 30/30", "Corsa", "8 ripetute: 30 secondi corsa veloce, 30 secondi camminata.", "Intermedio"),
            AllenamentoPrecaricato(3, "Interval Run", "Corsa", "4 blocchi da 4 minuti: 2 minuti veloce, 2 minuti lento.", "Intermedio"),
            AllenamentoPrecaricato(4, "Long Run", "Corsa", "10km a ritmo costante. Lavora sulla resistenza prolungata.", "Difficile"),
            AllenamentoPrecaricato(5, "Salita Sprint", "Corsa", "5 sprint in salita di 100 metri con 2 minuti di recupero.", "Difficile"),

            AllenamentoPrecaricato(6, "Upper Body Base", "Pesi", "3 serie di push-up, curl bicipiti, alzate laterali. 45s di recupero.", "Facile"),
            AllenamentoPrecaricato(7, "Gambe e Glutei", "Pesi", "Affondi, squat con manubri, ponte per glutei. 3x12.", "Intermedio"),
            AllenamentoPrecaricato(8, "Full Body HIIT", "Pesi", "4 circuiti ad alta intensità: 5 esercizi x 40s.", "Difficile"),
            AllenamentoPrecaricato(9, "Chest Focus", "Pesi", "Bench press, chest fly, dips. 4 serie, carico medio.", "Intermedio"),
            AllenamentoPrecaricato(10, "Spalle e Trapezi", "Pesi", "Alzate frontali, shrugs, overhead press. 3x10.", "Difficile"),

            AllenamentoPrecaricato(11, "Stretching base", "Ginnastica", "Routine completa di stretching per collo, schiena, gambe. 20 minuti.", "Facile"),
            AllenamentoPrecaricato(12, "Addome 10'", "Ginnastica", "Crunch, plank, bicycle kicks. 30 sec esercizio, 10 sec pausa.", "Facile"),
            AllenamentoPrecaricato(13, "Mobility Flow", "Ginnastica", "Sequenza fluida per migliorare mobilità e coordinazione.", "Intermedio"),
            AllenamentoPrecaricato(14, "Core Killer", "Ginnastica", "Circuito addome e lombari. 4 round, 6 esercizi. No pause.", "Difficile"),
            AllenamentoPrecaricato(15, "Total Body Pilates", "Ginnastica", "Allenamento stile pilates di 30 minuti per corpo intero.", "Intermedio"),

            AllenamentoPrecaricato(16, "Corsa in progressione", "Corsa", "10 min lenti + 10 min medi + 5 min veloci.", "Intermedio"),
            AllenamentoPrecaricato(17, "Fartlek 4x2", "Corsa", "4 blocchi da 2 min forte + 2 min lento.", "Intermedio"),
            AllenamentoPrecaricato(18, "Run & Walk", "Corsa", "Alterna 3 min corsa, 2 min camminata per 30 minuti.", "Facile"),
            AllenamentoPrecaricato(19, "10km record", "Corsa", "Testa la tua soglia su 10 km. Cronometra tutto!", "Difficile"),
            AllenamentoPrecaricato(20, "Pista Intervalli", "Corsa", "6x400m a ritmo alto con 1:30 recupero.", "Difficile"),

            AllenamentoPrecaricato(21, "Cardio Circuit", "Ginnastica", "Jumping jack, burpees, mountain climber. 3 round intensi.", "Intermedio"),
            AllenamentoPrecaricato(22, "Yoga Relax", "Ginnastica", "Routine di yoga per rilassamento, 25 minuti di flusso.", "Facile"),
            AllenamentoPrecaricato(23, "Pesi gambe avanzato", "Pesi", "Squat con bilanciere, stacchi rumeni, leg curl. 4x8.", "Difficile"),
            AllenamentoPrecaricato(24, "Push Day", "Pesi", "Petto, spalle, tricipiti. 3 superserie con carico crescente.", "Difficile"),
            AllenamentoPrecaricato(25, "Abs & Stability", "Ginnastica", "Core e stabilizzazione con plank, side plank, bird-dog.", "Intermedio")
        )

        return allenamenti.map { allenamento ->
            allenamento.copy(preferito = preferitiSalvati.contains(allenamento.id.toString()))
        }
    }

    private fun filtraAllenamenti(tipo: String, nome: String, difficolta: String) {
        //è tutto un blocco unico MESSO COSì PER RENDERLO LEGGIBILE
        val filtrati = listaAllenamenti.filter {
            it.tipo == tipo &&
                    it.nome.contains(nome, ignoreCase = true) &&
                    (difficolta == "Tutte" || it.difficolta.equals(difficolta, ignoreCase = true)) //default chiaramente è tutte le difficoltà allo stesso tempo
        }.sortedByDescending { it.preferito }


        adapter = AllenamentiPrecaricatiAdapter(filtrati, this) //aggiunto il contesto
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.imposta_home -> {
                val prefs = getSharedPreferences("impostazioni_app", MODE_PRIVATE)
                prefs.edit().putString("schermata_iniziale", this::class.java.name).apply()
                Toast.makeText(this, "Schermata iniziale salvata!", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}

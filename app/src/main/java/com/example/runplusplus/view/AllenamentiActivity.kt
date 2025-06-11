package com.example.runplusplus.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runplusplus.R
import com.example.runplusplus.adapter.AllenamentoAdapter
import com.example.runplusplus.databinding.ActivityAllenamentiBinding
import com.example.runplusplus.database.AppDatabase
import com.example.runplusplus.model.Allenamento
import com.example.runplusplus.repository.AllenamentoRepository
import com.example.runplusplus.viewmodel.AllenamentoViewModel
import com.example.runplusplus.viewmodel.AllenamentoViewModelFactory
import kotlin.jvm.java


class AllenamentiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllenamentiBinding //lega la view al codice
    private lateinit var allenamentoViewModel: AllenamentoViewModel
    private lateinit var adapter: AllenamentoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllenamentiBinding.inflate(layoutInflater) //viene attivato il layout e viene poi usato per le varie operazioni del programma
        setContentView(binding.root)

        // inizializza la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Run++"
            subtitle = "Allenamenti"
        }

        // inizializza il ViewModel
        val database = AppDatabase.getDatabase(this) //recupera il database di Room e i DAO
        val allenamentoDao = database.allenamentoDao()
        val repository = AllenamentoRepository(allenamentoDao)
        allenamentoViewModel = ViewModelProvider(this, AllenamentoViewModelFactory(repository)).get(AllenamentoViewModel::class.java) //fornisce i dati al viewmodel

        setupRecyclerView() //setup della recyclerview, che "ricicla" le view non attualmente usate
        observeAllenamenti() //attiva l'osservazione degli allenamenti

        //setup pulsnti
        val btnOrdinaData = findViewById<Button>(R.id.btnOrdinaData)
        val btnOrdinaTipo = findViewById<Button>(R.id.btnOrdinaTipo)
        val btnOrdinaNome = findViewById<Button>(R.id.btnOrdinaNome)

        btnOrdinaData.setOnClickListener {
            allenamentoViewModel.ordinaPerData()
        }

        btnOrdinaTipo.setOnClickListener {
            allenamentoViewModel.ordinaPerTipo()
        }

        btnOrdinaNome.setOnClickListener {
            allenamentoViewModel.ordinaPerNome()
        }

        binding.fabAggiungiAllenamento.setOnClickListener {
            val intent = Intent(this, NuovoAllenamentoActivity::class.java)
            startActivity(intent)
        }

        //setup pulsanti barra sotto
        binding.bottomNav.menu.findItem(R.id.allenamenti).isEnabled = false //messo così il pulsante sotto degli allenamenti non è premibile

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.calendario -> {
                    startActivity(Intent(this, CalendarioActivity::class.java))
                    true
                }
                R.id.allenamenti_precaricati -> {
                    startActivity(Intent(this, AllenamentiPrecaricatiActivity::class.java))
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

    //setup della recycler view e l'adapter, preparando quindi li layout per essere riempito (la UI quindi)
    private fun setupRecyclerView() {
        adapter = AllenamentoAdapter(emptyList()) { allenamento ->
            mostraOpzioniAllenamento(allenamento) //questo prende il click su un allenamento per prenderne i dettagli
        }
        binding.recyclerViewAllenamenti.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAllenamenti.adapter = adapter
    }

    //l'osservatore prende il Livedata e lo inserisce nella UI appena creata
    private fun observeAllenamenti() {
        allenamentoViewModel.allAllenamenti.observe(this, Observer { allenamenti ->
            Log.d("AllenamentiActivity", "Allenamenti ricevuti: ${allenamenti.size}")
            adapter.setData(allenamenti) // Usa setData per aggiornare l'adapter co i nuovi dati
        })
    }

    //apre la schermata per la modifica allenamento al click
    private fun mostraOpzioniAllenamento(allenamento: Allenamento) {
        val intent = Intent(this, ModificaAllenamentoActivity::class.java)
        intent.putExtra("allenamento_id", allenamento.id) // prende l'id
        startActivity(intent)
    }

    //ogni volta che si ritorna indietro rifa tutto
    override fun onResume() {
        super.onResume()
        observeAllenamenti() //messo per sicurezza anche una seconda volta, così i dati che lo popolano sono sempre corretti
    }

    //questi due metodi gestiscono il menu a tre puntini e quindi del setup per la schermata principale
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

    //per la freccia indietro
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
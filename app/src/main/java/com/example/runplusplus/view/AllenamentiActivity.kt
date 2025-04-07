package com.example.runplusplus.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var binding: ActivityAllenamentiBinding
    private lateinit var allenamentoViewModel: AllenamentoViewModel
    private lateinit var adapter: AllenamentoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllenamentiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inizializza il ViewModel
        val database = AppDatabase.getDatabase(this)
        val allenamentoDao = database.allenamentoDao()
        val repository = AllenamentoRepository(allenamentoDao)
        allenamentoViewModel = ViewModelProvider(this, AllenamentoViewModelFactory(repository)).get(AllenamentoViewModel::class.java)

        setupRecyclerView()
        observeAllenamenti()

        binding.fabAggiungiAllenamento.setOnClickListener {
            val intent = Intent(this, NuovoAllenamentoActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setupRecyclerView() {
        adapter = AllenamentoAdapter(emptyList()) { allenamento ->
            // Gestisce il click sull'allenamento, basandosi sul listener
            mostraOpzioniAllenamento(allenamento)
        }
        binding.recyclerViewAllenamenti.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAllenamenti.adapter = adapter
    }

    private fun observeAllenamenti() {
        allenamentoViewModel.allAllenamenti.observe(this, Observer { allenamenti ->
            Log.d("AllenamentiActivity", "Allenamenti ricevuti: ${allenamenti.size}")
            adapter.setData(allenamenti) // Usa setData per aggiornare l'adapter co i nuovi dati
        })
    }

    private fun mostraOpzioniAllenamento(allenamento: Allenamento) {
        val intent = Intent(this, ModificaAllenamentoActivity::class.java)
        intent.putExtra("allenamento_id", allenamento.id) // prende l'id
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        observeAllenamenti()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.allenamenti_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.ordina_data -> {
                allenamentoViewModel.ordinaPerData()
                return true
            }
            R.id.ordina_tipo -> {
                allenamentoViewModel.ordinaPerTipo()
                return true
            }
            R.id.ordina_nome -> {
                allenamentoViewModel.ordinaPerNome()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}










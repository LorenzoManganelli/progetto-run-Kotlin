package com.example.runplusplus.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.runplusplus.R
import com.example.runplusplus.database.AppDatabase
import com.example.runplusplus.model.Allenamento
import com.example.runplusplus.repository.AllenamentoRepository
import com.example.runplusplus.viewmodel.AllenamentoViewModel
import com.example.runplusplus.viewmodel.AllenamentoViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModificaAllenamentoActivity : AppCompatActivity() {

    private lateinit var editTextData: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var editTextDurata: EditText
    private lateinit var editTextCalorie: EditText
    private lateinit var editTextNote: EditText
    private lateinit var buttonSalva: Button
    private lateinit var buttonElimina: Button
    private lateinit var allenamentoViewModel: AllenamentoViewModel
    private var allenamentoId: Int = -1 // Inizializza con un valore di default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifica_allenamento)

        // Inizializza il ViewModel
        val database = AppDatabase.getDatabase(this)
        val allenamentoDao = database.allenamentoDao()
        val repository = AllenamentoRepository(allenamentoDao)
        allenamentoViewModel = ViewModelProvider(this, AllenamentoViewModelFactory(repository)).get(AllenamentoViewModel::class.java)

        editTextData = findViewById(R.id.editTextData)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        editTextDurata = findViewById(R.id.editTextDurata)
        editTextCalorie = findViewById(R.id.editTextCalorie)
        editTextNote = findViewById(R.id.editTextNote)
        buttonSalva = findViewById(R.id.buttonSalva)
        buttonElimina = findViewById(R.id.buttonElimina)

        // Ottieni l'ID dell'allenamento dall'Intent
        allenamentoId = intent.getIntExtra("allenamento_id", -1)

        // Carica i dati dell'allenamento se l'ID è valido
        if (allenamentoId != -1) {
            caricaDatiAllenamento(allenamentoId)
        } else {
            Toast.makeText(this, "ID allenamento non valido", Toast.LENGTH_SHORT).show()
            finish()
        }

        buttonSalva.setOnClickListener {
            salvaModificheAllenamento()
        }

        buttonElimina.setOnClickListener {
            eliminaAllenamento()
        }
    }

    private fun caricaDatiAllenamento(id: Int) {
        allenamentoViewModel.getAllenamentoById(id).observe(this) { allenamento ->
            if (allenamento != null) {
                editTextData.setText(allenamento.data)
                val tipiAllenamento = resources.getStringArray(R.array.tipi_allenamento)
                val posizione = tipiAllenamento.indexOf(allenamento.tipo)
                spinnerTipo.setSelection(posizione)
                editTextDurata.setText(allenamento.durata.toString())
                editTextCalorie.setText(allenamento.calorieBruciate.toString())
                editTextNote.setText(allenamento.note)
            } else {
                Toast.makeText(this, "Allenamento non trovato", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun salvaModificheAllenamento() {
        val data = editTextData.text.toString()
        val tipo = spinnerTipo.selectedItem.toString()
        val durataStr = editTextDurata.text.toString()
        val calorieStr = editTextCalorie.text.toString()
        val note = editTextNote.text.toString()

        if (data.isEmpty() || tipo.isEmpty() || durataStr.isEmpty() || calorieStr.isEmpty()) {
            Toast.makeText(this, "Compila tutti i campi obbligatori", Toast.LENGTH_SHORT).show()
            return
        }

        val durata = durataStr.toIntOrNull()
        val calorieBruciate = calorieStr.toIntOrNull()

        if (durata == null || calorieBruciate == null) {
            Toast.makeText(this, "Durata e Calorie devono essere numeri validi", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val allenamento = Allenamento(
                id = allenamentoId, // Mantieni l'ID originale
                data = data,
                tipo = tipo,
                durata = durata,
                calorieBruciate = calorieBruciate,
                note = note
            )
            allenamentoViewModel.update(allenamento)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ModificaAllenamentoActivity, "Allenamento modificato", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun eliminaAllenamento() {
        allenamentoViewModel.getAllenamentoById(allenamentoId).observe(this) { allenamento ->
            if (allenamento != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    allenamentoViewModel.delete(allenamento)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ModificaAllenamentoActivity, "Allenamento eliminato", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}
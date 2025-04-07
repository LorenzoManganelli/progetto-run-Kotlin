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

class NuovoAllenamentoActivity : AppCompatActivity() {

    private lateinit var editTextData: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var editTextDurata: EditText
    private lateinit var editTextCalorie: EditText
    private lateinit var editTextNote: EditText
    private lateinit var buttonSalva: Button
    private lateinit var allenamentoViewModel: AllenamentoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuovo_allenamento)

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

        buttonSalva.setOnClickListener {
            salvaAllenamento()
        }
    }

    private fun salvaAllenamento() {
        val data = editTextData.text.toString()
        val tipo = spinnerTipo.selectedItem.toString() // Ottieni il valore dallo Spinner
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

        val allenamento = Allenamento(
            data = data,
            tipo = tipo,
            durata = durata,
            calorieBruciate = calorieBruciate,
            note = note
        )

        CoroutineScope(Dispatchers.IO).launch {
            allenamentoViewModel.insert(allenamento)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@NuovoAllenamentoActivity, "Allenamento salvato", Toast.LENGTH_SHORT).show()
                finish() // Chiude l'activity e torna alla lista
            }
        }
    }
}
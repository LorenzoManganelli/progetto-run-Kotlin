package com.example.runplusplus.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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

        //inizializza la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Run++"
            subtitle = "Modifica Allenamento"
        }

        //inizializza il ViewModel
        val database = AppDatabase.getDatabase(this)
        val allenamentoDao = database.allenamentoDao()
        val repository = AllenamentoRepository(allenamentoDao)
        allenamentoViewModel = ViewModelProvider(this, AllenamentoViewModelFactory(repository)).get(AllenamentoViewModel::class.java)

        editTextData = findViewById(R.id.editTextData) //qui va messo la data (come prima)
        //spero ci sia un metodo più banale di questo, però pare che funziona
        editTextData.addTextChangedListener(object : TextWatcher {
            private var isEditing = false //questo andava aggiunto per evitare modifiche ricorsive mentre il programma è attivo

            //prima e durante il cambio del testo, lasciati vuoti
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            //stessa cosa del nuovo allenamento
            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return //fa il check per evitare loop
                isEditing = true

                val clean = s.toString().replace("[^\\d]".toRegex(), "")
                val builder = StringBuilder()

                if (clean.length >= 2) {
                    builder.append(clean.substring(0, 2)).append("-")
                } else if (clean.isNotEmpty()) {
                    builder.append(clean)
                }

                if (clean.length >= 4) {
                    val month = clean.substring(2, 4).toInt().coerceIn(1, 12).toString().padStart(2, '0')
                    builder.append(month).append("-")
                } else if (clean.length > 2) {
                    builder.append(clean.substring(2))
                }

                if (clean.length > 4) {
                    builder.append(clean.substring(4).take(4))
                }

                val formatted = builder.toString()
                editTextData.setText(formatted)
                editTextData.setSelection(formatted.length.coerceAtMost(editTextData.text.length))

                isEditing = false
            }
        })

        spinnerTipo = findViewById(R.id.spinnerTipo)
        editTextDurata = findViewById(R.id.editTextDurata)
        editTextCalorie = findViewById(R.id.editTextCalorie)
        editTextNote = findViewById(R.id.editTextNote)
        buttonSalva = findViewById(R.id.buttonSalva)
        buttonElimina = findViewById(R.id.buttonElimina)

        // Ottieni l'ID dell'allenamento dall'Intent (se =-1 fa errore)
        allenamentoId = intent.getIntExtra("allenamento_id", -1)

        // Carica i dati dell'allenamento se l'ID è valido
        if (allenamentoId != -1) {
            caricaDatiAllenamento(allenamentoId)
        } else {
            Toast.makeText(this, "ID allenamento non valido", Toast.LENGTH_SHORT).show() //non è mai capitato in compenso
            finish()
        }

        buttonSalva.setOnClickListener {
            salvaModificheAllenamento()
        }

        buttonElimina.setOnClickListener {
            eliminaAllenamento()
        }
    }

    //recupera i dati da modificare
    private fun caricaDatiAllenamento(id: Int) {
        allenamentoViewModel.getAllenamentoById(id).observe(this) { allenamento -> //recupera l'allenamento in base all'id
            if (allenamento != null) { //tutto l'if contiene tutti i dati caricati
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

    //salve le modifiche
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

        // Soluzione un po' barbarica, ma usare il calendario per scegliere la data è l'odio incarnato (stessa di NuovoAllenamento)
        val regexData = Regex("^\\d{2}-\\d{2}-\\d{4}$")
        if (!regexData.matches(data)) {
            Toast.makeText(this, "Data non valida. Il formato deve essere DD-MM-YYYY", Toast.LENGTH_SHORT).show()
            return
        }

        val durata = durataStr.toIntOrNull()
        val calorieBruciate = calorieStr.toIntOrNull()

        if (durata == null || calorieBruciate == null) {
            Toast.makeText(this, "Durata e Calorie devono essere numeri validi", Toast.LENGTH_SHORT).show()
            return
        }


        val inputDate = editTextData.text.toString()
        val parsedDate = try {
            val inputFormatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val outputFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            java.time.LocalDate.parse(inputDate, inputFormatter).format(outputFormatter)
        } catch (e: Exception) {
            Toast.makeText(this, "Data non valida", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val allenamento = Allenamento(
                id = allenamentoId, //mantiene l'ID originale
                data = parsedDate, //in questo modo mantengo il formato standard per far funzionare l'ordinamento ma lo fa vedere come dd-MM-yyyy
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

    //elimina l'allenamento
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

    //freccia indietro
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
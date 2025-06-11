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

class NuovoAllenamentoActivity : AppCompatActivity() {

    private lateinit var editTextData: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var editTextDurata: EditText
    private lateinit var editTextCalorie: EditText
    private lateinit var editTextNote: EditText
    private lateinit var buttonSalva: Button
    private lateinit var allenamentoViewModel: AllenamentoViewModel

    //apre la schermata per i nuovi allenamenti
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuovo_allenamento)

        // inizializza la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Run++"
            subtitle = "Allenamenti"
        }

        val database = AppDatabase.getDatabase(this)
        val allenamentoDao = database.allenamentoDao()
        val repository = AllenamentoRepository(allenamentoDao)
        allenamentoViewModel = ViewModelProvider(this, AllenamentoViewModelFactory(repository)).get(AllenamentoViewModel::class.java)


        editTextData = findViewById(R.id.editTextData)

        //trovato metodo meno banale per formattare le date come voglio io, però è lunghissimo...
        editTextData.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true

                val clean = s.toString().replace("[^\\d]".toRegex(), "") //puoi inserire SOLO numeri. Forse quì c'è un problema
                val builder = StringBuilder()

                var day = ""
                var month = ""
                var year = ""

                //giorno
                if (clean.length >= 2) {
                    day = clean.substring(0, 2)
                    builder.append(day).append("-")
                } else if (clean.isNotEmpty()) {
                    builder.append(clean)
                }

                //mese
                if (clean.length >= 4) {
                    month = clean.substring(2, 4).toInt().coerceIn(1, 12).toString().padStart(2, '0')
                    builder.append(month).append("-")
                } else if (clean.length > 2) {
                    builder.append(clean.substring(2))
                }

                //anno
                if (clean.length > 4) {
                    year = clean.substring(4).take(4)
                    builder.append(year)
                }

                val formatted = builder.toString()
                editTextData.setText(formatted)
                editTextData.setSelection(formatted.length.coerceAtMost(editTextData.text.length))

                isEditing = false
            }
        })

        //preparazione layout
        spinnerTipo = findViewById(R.id.spinnerTipo)
        editTextDurata = findViewById(R.id.editTextDurata)
        editTextCalorie = findViewById(R.id.editTextCalorie)
        editTextNote = findViewById(R.id.editTextNote)
        buttonSalva = findViewById(R.id.buttonSalva)

        buttonSalva.setOnClickListener {
            salvaAllenamento()
        }
    }

    //salva tutti i dati e controlla se i campi sono validi
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

        // Soluzione un po' barbarica, ma usare il calendario per scegliere la data è l'odio incarnato
        val regexData = Regex("^\\d{2}-\\d{2}-\\d{4}$") //controllo del formato
        if (!regexData.matches(data)) {
            Toast.makeText(this, "Data non valida. Il formato deve essere DD-MM-YYYY", Toast.LENGTH_SHORT).show()
            return
        }

        //stringhe a numeri per evitare errori
        val durata = durataStr.toIntOrNull()
        val calorieBruciate = calorieStr.toIntOrNull()

        if (durata == null || calorieBruciate == null) {
            Toast.makeText(this, "Durata e Calorie devono essere numeri validi", Toast.LENGTH_SHORT).show()
            return
        }

        //cambia il format delle date MA il sistema le può comunque usare nella forma standard
        val inputDate = editTextData.text.toString()
        val parsedDate = try {
            val inputFormatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val outputFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            java.time.LocalDate.parse(inputDate, inputFormatter).format(outputFormatter)
        } catch (e: Exception) {
            Toast.makeText(this, "Data non valida", Toast.LENGTH_SHORT).show()
            return
        }

        //usa il model e lo popola
        val allenamento = Allenamento(
            data = parsedDate, //in questo modo mantengo il formato standard per far funzionare l'ordinamento ma lo fa vedere come dd-MM-yyyy
            tipo = tipo,
            durata = durata,
            calorieBruciate = calorieBruciate,
            note = note
        )

        //inserisce nel database tutti i dati
        CoroutineScope(Dispatchers.IO).launch {
            allenamentoViewModel.insert(allenamento) //ricorda: questi dati faranno un giro tipo Repository -> DAO -> Room
            withContext(Dispatchers.Main) {
                Toast.makeText(this@NuovoAllenamentoActivity, "Allenamento salvato", Toast.LENGTH_SHORT).show()
                finish() //chiude l'activity e torna alla lista
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
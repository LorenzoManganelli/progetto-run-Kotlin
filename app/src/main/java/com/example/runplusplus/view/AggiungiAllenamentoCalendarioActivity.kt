package com.example.runplusplus.view

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.runplusplus.R
import com.example.runplusplus.util.scheduleAllenamentoNotification
import com.example.runplusplus.model.CalendarioAllenamento
import com.example.runplusplus.viewmodel.CalendarioViewModel
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime

class AggiungiAllenamentoCalendarioActivity : AppCompatActivity() {

    private lateinit var viewModel: CalendarioViewModel
    private lateinit var dataSelezionata: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aggiungi_allenamento_calendario)

        // inizializza la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Run++"
            subtitle = "Nuovo Allenamento"
        }

        // Recupera la data selezionata
        dataSelezionata = LocalDate.parse(intent.getStringExtra("data"))

        val spinnerTipo = findViewById<Spinner>(R.id.spinnerTipo)
        val containerDettagli = findViewById<LinearLayout>(R.id.containerDettagli)
        val inputOra = findViewById<EditText>(R.id.inputOra)
        val btnSalva = findViewById<Button>(R.id.btnSalva)
        val isModifica = intent.getBooleanExtra("modifica", false)
        val allenamentoId = intent.getIntExtra("allenamentoId", -1)

        //colora la parte dello spinner visualizzata di nero NON FUNZIONA
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.tipi_allenamento,
            R.layout.spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapter

        // Popola lo spinner con le opzioni degli allenamenti
        spinnerTipo.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Corsa", "Pesi", "Ginnastica")
        )

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CalendarioViewModel::class.java]

        // Se modifica == true, recupera i dati
        if (isModifica && allenamentoId != -1) {
            viewModel.getAllenamentoById(allenamentoId).observe(this) { allenamento ->
                if (allenamento != null) {
                    // Precompila i campi con i dati già esistenti
                    spinnerTipo.setSelection(
                        (spinnerTipo.adapter as ArrayAdapter<String>).getPosition(allenamento.tipo)
                    )

                    inputOra.setText(allenamento.ora.toString())

                    // Precompila i campi dinamici con i dettagli
                    val json = JSONObject(allenamento.dettagli)
                    containerDettagli.removeAllViews()
                    for (key in json.keys()) {
                        val value = json.getString(key)
                        val campo = EditText(this).apply {
                            hint = key
                            setText(value)
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                        }
                        containerDettagli.addView(campo)
                    }
                }
            }
        }

        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val tipo = spinnerTipo.selectedItem.toString()
                containerDettagli.removeAllViews()

                when (tipo) {
                    "Corsa" -> {
                        aggiungiCampoEditText(containerDettagli, "Chilometri")
                        aggiungiCampoEditText(containerDettagli, "Velocità media (km/h)")
                    }
                    "Pesi" -> {
                        aggiungiCampoEditText(containerDettagli, "Tipo esercizio")
                        aggiungiCampoEditText(containerDettagli, "Ripetizioni e set")
                    }
                    "Ginnastica" -> {
                        aggiungiCampoEditText(containerDettagli, "Tipo esercizio")
                        aggiungiCampoEditText(containerDettagli, "Durata (minuti)")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSalva.setOnClickListener { //salva il valore come una stringa e controlla se è vuoto o pieno
            val tipo = spinnerTipo.selectedItem.toString()
            // Permette di mettere dinamicamente dettagli diversi
            val dettagliJson = JSONObject()
            for (i in 0 until containerDettagli.childCount) {
                val child = containerDettagli.getChildAt(i)
                if (child is EditText) {
                    val key = child.hint.toString()
                    val value = child.text.toString()
                    if (value.isBlank()) {
                        Toast.makeText(this, "Compila tutti i dettagli!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    dettagliJson.put(key, value)
                }
            }
            val dettagli = dettagliJson.toString()
            val oraString = inputOra.text.toString()
            val ora = try {
                LocalTime.parse(oraString)
            } catch (e: Exception) {
                null
            }
            if (dettagli.isBlank() || ora == null) {
                Toast.makeText(this, "Compila tutti i campi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val allenamento = CalendarioAllenamento(
                id = if (isModifica) allenamentoId else 0,
                data = dataSelezionata,
                ora = ora,
                tipo = tipo,
                dettagli = dettagli
            )

            viewModel.aggiungiAllenamento(allenamento)

            scheduleAllenamentoNotification(applicationContext, allenamento) //serve per preparare le notifiche per quell'allenamento AGGIUNGILO PURE ALLA MODIFICA

            Toast.makeText(this, "Allenamento salvato!", Toast.LENGTH_SHORT).show()
            finish()
        }

        inputOra.setOnClickListener {
            val oraCorrente = java.util.Calendar.getInstance()
            val hour = oraCorrente.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = oraCorrente.get(java.util.Calendar.MINUTE)

            val timePickerDialog = android.app.TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                inputOra.setText(formattedTime)
            }, hour, minute, true)

            timePickerDialog.show()
        }
    }

    private fun aggiungiCampoEditText(container: LinearLayout, hint: String) {
        val editText = EditText(this)
        editText.hint = hint
        editText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        container.addView(editText)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
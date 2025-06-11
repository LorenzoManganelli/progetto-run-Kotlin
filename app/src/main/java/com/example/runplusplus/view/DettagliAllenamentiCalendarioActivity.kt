package com.example.runplusplus.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.runplusplus.R
import com.example.runplusplus.viewmodel.CalendarioViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.time.LocalDate

class DettagliAllenamentiCalendarioActivity : AppCompatActivity() {

    private lateinit var viewModel: CalendarioViewModel
    private lateinit var dataSelezionata: LocalDate
    private lateinit var container: LinearLayout

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dettagli_allenamenti_calendario)

        // inizializza la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Run++"
            subtitle = "Modifica Allenamento"
        }

        dataSelezionata = LocalDate.parse(intent.getStringExtra("data"))
        container = findViewById(R.id.containerAllenamenti)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CalendarioViewModel::class.java]

        findViewById<FloatingActionButton>(R.id.btnAggiungiAllenamento).setOnClickListener {
            val intent = Intent(this, AggiungiAllenamentoCalendarioActivity::class.java)
            intent.putExtra("data", dataSelezionata.toString())
            startActivity(intent)
        }

        viewModel.getAllenamentiInData(dataSelezionata).observe(this) { allenamenti ->
            container.removeAllViews()
            if (allenamenti.isEmpty()) {
                startActivity(Intent(this, AggiungiAllenamentoCalendarioActivity::class.java).apply {
                    putExtra("data", dataSelezionata.toString())
                })
                finish()
            } else {
                allenamenti.forEach { allenamento ->
                    val view = layoutInflater.inflate(R.layout.allenamento_calendario_item, container, false)

                    view.findViewById<TextView>(R.id.textTipo).text = allenamento.tipo
                    view.findViewById<TextView>(R.id.textOra).text = "Ore: ${allenamento.ora}"
                    //invece di sputarlo fuori come un JSON lo tira fuori come valore migliore
                    val dettagliJson = try {
                        JSONObject(allenamento.dettagli)
                    } catch (e: Exception) {
                        null
                    }

                    val dettagliFormattati = if (dettagliJson != null) {
                        val builder = StringBuilder()
                        val keys = dettagliJson.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val value = dettagliJson.optString(key)
                            builder.append("$key: $value\n")
                        }
                        builder.toString().trim() // rimuove newline finale
                    } else {
                        allenamento.dettagli // fallback
                    }

                    view.findViewById<TextView>(R.id.textDettagli).text = dettagliFormattati

                    view.findViewById<Button>(R.id.btnModifica).setOnClickListener {
                        val intent = Intent(this, AggiungiAllenamentoCalendarioActivity::class.java)
                        intent.putExtra("data", dataSelezionata.toString())
                        intent.putExtra("modifica", true)
                        intent.putExtra("allenamentoId", allenamento.id)
                        startActivity(intent)
                        finish()
                    }

                    view.findViewById<Button>(R.id.btnElimina).setOnClickListener {
                        viewModel.eliminaAllenamento(allenamento)
                        Toast.makeText(this, "Allenamento eliminato", Toast.LENGTH_SHORT).show()
                    }

                    container.addView(view)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

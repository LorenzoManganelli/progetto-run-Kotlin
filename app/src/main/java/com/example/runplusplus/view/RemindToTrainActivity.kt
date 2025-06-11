package com.example.runplusplus.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runplusplus.R
import com.example.runplusplus.adapter.RTTAdapter
import com.example.runplusplus.model.RTTProgramma
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class RemindToTrainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RTTAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remind_to_train)

        //inizializza la toolbar come sempre
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Run++"
            subtitle = "RTT \uD83D\uDC30"
        }

        recyclerView = findViewById(R.id.recyclerViewRTT)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //
        caricaRTTDaFirestore()

        //barra sotto same as ever
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.remind_to_train
        bottomNav.menu.findItem(R.id.remind_to_train).isEnabled = false

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
                R.id.allenamenti_precaricati -> {
                    startActivity(Intent(this, AllenamentiPrecaricatiActivity::class.java))
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

    private fun caricaRTTDaFirestore() {
        val db = FirebaseFirestore.getInstance() //si crea il legame con il db
        db.collection("rtt_sets").get() //la raccolta è rtt_sets su Firestore
            .addOnSuccessListener { result ->
                val lista = result.mapNotNull { it.toObject(RTTProgramma::class.java) }

                //l'adapter è preparato
                adapter = RTTAdapter(lista) { programma ->
                    val intent = Intent(this, RTTDettagliActivity::class.java)
                    intent.putExtra("programma", programma)
                    startActivity(intent)
                }

                recyclerView.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Errore nel recupero dei programmi RTT", Toast.LENGTH_SHORT).show() //come sempre una eccezione, anche se non è mai apparsa a codice finito
            }
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

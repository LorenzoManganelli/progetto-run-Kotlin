package com.example.runplusplus.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runplusplus.R
import com.example.runplusplus.adapter.CalendarioAdapter
import com.example.runplusplus.adapter.CalendarioAdapter.OnItemListener
import com.example.runplusplus.viewmodel.CalendarioViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarioActivity : AppCompatActivity(), OnItemListener {

    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null
    var selectedDate: LocalDate? = null
    private lateinit var viewModel: CalendarioViewModel
    private var giorniConAllenamenti: Set<Int> = emptySet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        // inizializza la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Run++"
            subtitle = "Calendario"
        }

        initWidgets()
        selectedDate = LocalDate.now() //prepara la data attuale

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CalendarioViewModel::class.java]

        //usserva gli allenamenti e aggiorna la grafica del calendario
        viewModel.allenamentiFuturi.observe(this) { allenamenti ->
            val allenamentiInMese = allenamenti.filter {
                it.data.month == selectedDate!!.month && it.data.year == selectedDate!!.year
            }
            giorniConAllenamenti = allenamentiInMese.map { it.data.dayOfMonth }.toSet()
            setMonthView()
        }

        //barra sotto (come sempre non si può selezionare calendario)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.calendario
        bottomNav.menu.findItem(R.id.calendario).isEnabled = false

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.allenamenti -> {
                    startActivity(Intent(this, AllenamentiActivity::class.java))
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

    private fun initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        monthYearText = findViewById(R.id.monthYearTV)
    }

    //aggiorna la visualizzazione del mese corrente e mostra i giorni corretti e gli allenamenti
    fun setMonthView() {
        monthYearText!!.text = monthYearFromDate(selectedDate!!)
        val daysInMonth = daysInMonthArray(selectedDate) //mostra in alto la data attuale

        val allenamenti = viewModel.allenamentiFuturi.value ?: emptyList()
        //filtra gli allenamenti in base al mese e all'anno del selectedDate corrente (soluzione migliore non trovata... funziona comunque)
        val allenamentiInMese = allenamenti.filter {
            it.data.month == selectedDate!!.month && it.data.year == selectedDate!!.year
        }
        //attaccato un po' a caso ma funziona
        val giorniConAllenamenti = allenamentiInMese.map { it.data.dayOfMonth }.toSet()

        val calendarioAdapter = CalendarioAdapter(daysInMonth, this, giorniConAllenamenti) //crea l'adapter per la recyclerview del calendario, preparando la populazione di questo
        val layoutManager = GridLayoutManager(applicationContext, 7) //prepara le colonne delle settimane e le celle dei giorni
        calendarRecyclerView!!.layoutManager = layoutManager
        calendarRecyclerView!!.adapter = calendarioAdapter
    }

    //prepara la griglia (NOTA: è 42 perché pare il modo "standard" di fare i calendari come questo)
    private fun daysInMonthArray(date: LocalDate?): ArrayList<String> {
        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)

        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = selectedDate!!.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value

        //riempe di spazi vuoti le date finché non si arriva al primo giorno del mese (idem per il dopo)
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString()) //parte da qui
            }
        }
        return daysInMonthArray
    }

    //serve per ottnere il mese (a 4 lettere per brevità) e l'anno
    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    //avanti e indietro nel mese
    fun previousMonthAction(view: View?) {
        selectedDate = selectedDate!!.minusMonths(1)
        setMonthView()
    }

    fun nextMonthAction(view: View?) {
        selectedDate = selectedDate!!.plusMonths(1)
        setMonthView()
    }

    //Gestione del click sul giorno (NOTA: ignora i giorni precedenti al corrente)
    override fun onItemClick(position: Int, dayText: String) {
        if (dayText.isNotEmpty()) {
            val selectedDay = dayText.toInt()
            val selectedFullDate = selectedDate!!.withDayOfMonth(selectedDay)
            val today = LocalDate.now()

            //controlla prima di "oggi" per impedire i click
            if (selectedFullDate.isBefore(today)) {
                Toast.makeText(this, "Non puoi selezionare date passate", Toast.LENGTH_SHORT).show()
                return
            }

            val intent = Intent(this, DettagliAllenamentiCalendarioActivity::class.java)
            intent.putExtra("data", selectedFullDate.toString())
            startActivity(intent)
        }
    }

    //menu in alto a destra come al solito
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
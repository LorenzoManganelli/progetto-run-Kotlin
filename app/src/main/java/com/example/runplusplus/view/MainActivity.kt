package com.example.runplusplus.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.runplusplus.ui.theme.LorenzoManganelliRunTheme
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.runplusplus.R
import com.example.runplusplus.database.AppDatabase
import com.example.runplusplus.repository.AllenamentoRepository
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.runplusplus.viewmodel.AllenamentoViewModel
import com.example.runplusplus.viewmodel.AllenamentoViewModelFactory
import com.example.runplusplus.model.Allenamento
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var allenamentoViewModel: AllenamentoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inizializza il database e il repository
        val database = AppDatabase.getDatabase(this)
        val allenamentoDao = database.allenamentoDao()
        val repository = AllenamentoRepository(allenamentoDao)

        // Inizializza il ViewModel
        allenamentoViewModel = ViewModelProvider(this, AllenamentoViewModelFactory(repository)).get(AllenamentoViewModel::class.java)

//        insertTestData()

        // Esempio di utilizzo del ViewModel (DA USARE DOPO)
        allenamentoViewModel.allAllenamenti.observe(this) { allenamenti ->
        // Fai qualcosa con la lista di allenamenti
            Log.d("MainActivity", "Allenamenti osservati: ${allenamenti.size}")
        }
    }

//    private fun insertTestData() {
//        lifecycleScope.launch {
//            try {
//                // Inserisce i dati SONO SOLO UN TEST, DA SOSTITUIRE CON IL PULSANTE CHE PERMETTE DI AGGIUNGERLI
//                allenamentoViewModel.insert(Allenamento(data = "2024-01-01", tipo = "Corsa", durata = 30, calorieBruciate = 300, note = "Corsa leggera"))
//                allenamentoViewModel.insert(Allenamento(data = "2024-01-02", tipo = "Pesi", durata = 60, calorieBruciate = 400, note = "Allenamento gambe"))
//                allenamentoViewModel.insert(Allenamento(data = "2024-01-03", tipo = "Yoga", durata = 45, calorieBruciate = 200, note = "Stretching"))
//
//                Log.d("MainActivity", "Dati di test inseriti correttamente")
//            } catch (e: Exception) {
//                Log.e("MainActivity", "Errore durante l'inserimento dei dati di test: ${e.message}")
//            }
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.allenamenti -> {
                startActivity(Intent(this, AllenamentiActivity::class.java))
                true
            }
            R.id.calendario -> {
                // TODO: Apri la schermata del calendario
                true
            }
            R.id.allenamenti_precaricati -> {
                // TODO: Apri la schermata degli allenamenti precaricati
                true
            }
            R.id.remind_to_train -> {
                // TODO: Apri la schermata di RemindToTrain
                true
            }
            R.id.esci -> {
                finish() // Chiude l'app
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LorenzoManganelliRunTheme {
        Greeting("Android")
    }
}
package com.example.runplusplus.view
//implementazione schermata di fine RTT
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.runplusplus.R
import com.example.runplusplus.database.AppDatabase
import kotlinx.coroutines.launch

class ValutazioneFinaleRTTActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_valutazione_finale)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Run++"
        supportActionBar?.subtitle = "Risultato Finale"

        val container = findViewById<LinearLayout>(R.id.containerValutazione)
        val textRisultato = findViewById<TextView>(R.id.textRisultato)
        val textPercentuale = findViewById<TextView>(R.id.textPercentuale)

        val programmaId = intent.getIntExtra("programmaId", -1)
        val durataTotale = intent.getIntExtra("durataGiorni", 30) // fallback di 30, metterlo è necessario

        if (programmaId == -1) {
            textRisultato.text = "Errore"
            textPercentuale.text = "ID programma non valido"
            return
        }

        //controlla il numero di giorni completati e lo confronta con i totali
        lifecycleScope.launch {
            val dao = AppDatabase.getDatabase(applicationContext).rttProgressoDao()
            val progresso = dao.getProgressoNow(programmaId)
            val giorniCompletati = progresso?.giorniCompletati?.size ?: 0
            Log.d("ValutazioneRTT", "Giorni completati: $giorniCompletati su $durataTotale")

            val percentuale = (giorniCompletati.toFloat() / durataTotale) * 100
            textPercentuale.text = "Completato: ${percentuale.toInt()}%"

            //si potrebbe aggiungere un effetto coriandoli o simile
            when {
                percentuale >= 70 -> {
                    textRisultato.text = "COMPLIMENTI!!!"
                    container.setBackgroundColor(Color.parseColor("#A5D6A7")) // Verde
                }
                percentuale >= 40 -> {
                    textRisultato.text = "Buon lavoro, prossima volta puntiamo più in alto :)"
                    container.setBackgroundColor(Color.parseColor("#FFF59D")) // Giallo
                }
                else -> {
                    textRisultato.text = "Ti sei lasciato un po' andare con questo set. Ritenta"
                    container.setBackgroundColor(Color.parseColor("#EF9A9A")) // Rosso
                }
            }
        }
    }
}

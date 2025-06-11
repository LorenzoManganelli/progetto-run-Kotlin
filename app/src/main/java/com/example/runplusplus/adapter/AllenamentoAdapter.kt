package com.example.runplusplus.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView //tutta la parte del cliccare sugli allenemanti RICORDA DI VEDERE SE LO PUOI FARE CON IL CALENDARIO
import androidx.recyclerview.widget.RecyclerView
import com.example.runplusplus.R
import com.example.runplusplus.model.Allenamento

//l'adapter prende una lista di oggetti e li trasforma in una serie di view che vanno poi a popolare la RecyclerView
class AllenamentoAdapter(
    private var allenamenti: List<Allenamento>,
    private val onItemClick: (Allenamento) -> Unit
) :
    RecyclerView.Adapter<AllenamentoAdapter.AllenamentoViewHolder>() {

    //tiene conto degli elementi grafici
    class AllenamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dataTextView: TextView = itemView.findViewById(R.id.dataTextView)
        val tipoTextView: TextView = itemView.findViewById(R.id.tipoTextView)
        val durataTextView: TextView = itemView.findViewById(R.id.durataTextView)
        val calorieTextView: TextView = itemView.findViewById(R.id.calorieTextView)
        val noteTextView: TextView = itemView.findViewById(R.id.noteTextView)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    //la recyclerview richiama questo per rifare la schermata
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllenamentoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.allenamento_item, parent, false)
        return AllenamentoViewHolder(itemView)
    }

    //aggiunte un paio di parti per fare in modo che il formato di data che l'utente deve scrivere è quello di normali mortali
    override fun onBindViewHolder(holder: AllenamentoViewHolder, position: Int) {
        val allenamento = allenamenti[position]
        val formatterInput = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatterOutput = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")

        try {
            val parsedDate = java.time.LocalDate.parse(allenamento.data, formatterInput) //e questo li converte
            holder.dataTextView.text = parsedDate.format(formatterOutput)
        } catch (e: Exception) {
            holder.dataTextView.text = allenamento.data // fallback in caso di errore
        }

        //assegnazione tipi degli oggetti e dati
        holder.tipoTextView.text = allenamento.tipo
        holder.durataTextView.text = allenamento.durata.toString()
        holder.calorieTextView.text = allenamento.calorieBruciate.toString()
        holder.noteTextView.text = allenamento.note
        holder.cardView.setOnClickListener {
            onItemClick(allenamento)
        }
    }

    override fun getItemCount(): Int {
        Log.d("AllenamentoAdapter", "getItemCount: ${allenamenti.size}")
        return allenamenti.size //numero totale di oggetti
    }

    //aggiorna e ricrea la lista con i nuovi dati
    fun setData(nuoviAllenamenti: List<Allenamento>) {
        allenamenti = nuoviAllenamenti
        notifyDataSetChanged()
    }
}
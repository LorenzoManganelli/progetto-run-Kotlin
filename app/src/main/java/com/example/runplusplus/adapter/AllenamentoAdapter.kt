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

class AllenamentoAdapter(
    private var allenamenti: List<Allenamento>,
    private val onItemClick: (Allenamento) -> Unit
) :
    RecyclerView.Adapter<AllenamentoAdapter.AllenamentoViewHolder>() {

    class AllenamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dataTextView: TextView = itemView.findViewById(R.id.dataTextView)
        val tipoTextView: TextView = itemView.findViewById(R.id.tipoTextView)
        val durataTextView: TextView = itemView.findViewById(R.id.durataTextView)
        val calorieTextView: TextView = itemView.findViewById(R.id.calorieTextView)
        val noteTextView: TextView = itemView.findViewById(R.id.noteTextView)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllenamentoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.allenamento_item, parent, false)
        return AllenamentoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AllenamentoViewHolder, position: Int) {
        val allenamento = allenamenti[position]
        holder.dataTextView.text = allenamento.data
        holder.tipoTextView.text = allenamento.tipo
        holder.durataTextView.text = allenamento.durata.toString()
        holder.calorieTextView.text = allenamento.calorieBruciate.toString()
        holder.noteTextView.text = allenamento.note
        holder.cardView.setOnClickListener { //DEVO CAPIRE PERCHé SE LO MANDO SOTT QUESTO DA ERRORE
            onItemClick(allenamento)
        }
    }

    override fun getItemCount(): Int {
        Log.d("AllenamentoAdapter", "getItemCount: ${allenamenti.size}")
        return allenamenti.size
    }

    fun setData(nuoviAllenamenti: List<Allenamento>) {
        allenamenti = nuoviAllenamenti
        notifyDataSetChanged()
    }
}
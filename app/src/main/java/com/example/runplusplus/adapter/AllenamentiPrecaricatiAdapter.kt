package com.example.runplusplus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.runplusplus.R
import com.example.runplusplus.model.AllenamentoPrecaricato

class AllenamentiPrecaricatiAdapter(
    private val lista: List<AllenamentoPrecaricato>,
    private val context: android.content.Context
) : RecyclerView.Adapter<AllenamentiPrecaricatiAdapter.ViewHolder>() {
    private var selectedItemPosition: Int? = null

    //preparazione di tutti i dati
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.textNome)
        val descrizione: TextView = view.findViewById(R.id.textDescrizione)
        val difficolta: TextView = view.findViewById(R.id.textDifficolta)
        val btnPreferito: ImageButton = view.findViewById(R.id.btnPreferito)
        val textDescrizione: TextView = view.findViewById(R.id.textDescrizione)
        val boxDescrizione: LinearLayout = view.findViewById(R.id.boxDescrizione)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_allenamento_precaricato, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val allenamento = lista[position]
        holder.nome.text = allenamento.nome
        holder.descrizione.text = allenamento.descrizione
        holder.difficolta.text = "Difficoltà: ${allenamento.difficolta}"

        //parte del preferito
        holder.btnPreferito.setImageResource(
            if (allenamento.preferito) R.drawable.ic_star else R.drawable.ic_star_border
        )

        holder.btnPreferito.setOnClickListener {
            allenamento.preferito = !allenamento.preferito

            //aggiorna SharedPreferences
            val prefs = context.getSharedPreferences("preferiti_precaricati", android.content.Context.MODE_PRIVATE)
            val preferiti = prefs.getStringSet("preferiti", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

            if (allenamento.preferito) {
                preferiti.add(allenamento.id.toString())
            } else {
                preferiti.remove(allenamento.id.toString())
            }

            prefs.edit().putStringSet("preferiti", preferiti).apply()

            //aggiorna la UI
            notifyItemChanged(position)
        }

        holder.textDescrizione.text = allenamento.descrizione
        //mostra/nasconde descrizione quando clicchi l'allenamento. In un mondo perfetto sarebbe stato più bellino, ma così va bene
        holder.boxDescrizione.visibility = if (position == selectedItemPosition) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            selectedItemPosition = if (selectedItemPosition == position) null else position
            notifyDataSetChanged()
        }
    }
}
package com.example.runplusplus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.runplusplus.R
import com.example.runplusplus.model.RTTProgramma

//prende una lista di oggetti che usano il model dell'RRTProgramma e li “trasforma” in righe che si possono usare e vedere all’interno di un RecyclerView
class RTTAdapter(
    private val lista: List<RTTProgramma>, //prepara i dati
    private val onClick: (RTTProgramma) -> Unit //riceve un input in base alla riga premuta nella schermata
) : RecyclerView.Adapter<RTTAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.textNomeRTT)
        val info: TextView = view.findViewById(R.id.textInfoRTT)
    }

    //si occupa di attivare il layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rtt, parent, false)
        return ViewHolder(view)
    }

    //popola il layout
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val programma = lista[position]
        //imposta nome e descrizione
        holder.nome.text = programma.nome
        holder.info.text = "${programma.tipologia} • ${programma.difficolta} • ${programma.durataGiorni} giorni"
        //gestione click facendo aprire RTTDettagliActivity
        holder.itemView.setOnClickListener {
            onClick(programma)
        }
    }

    override fun getItemCount(): Int = lista.size //per testing
}

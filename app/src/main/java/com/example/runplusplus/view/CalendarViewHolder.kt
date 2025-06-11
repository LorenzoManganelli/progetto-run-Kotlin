package com.example.runplusplus.view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.runplusplus.adapter.CalendarioAdapter.OnItemListener
import com.example.runplusplus.R

//aggiunto come caso speciale, dovevo trovare un modo per gestire le singole celle e ho usato un codice a parte per lavorarci sopra con più calma
class CalendarViewHolder(itemView: View, private val onItemListener: OnItemListener) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {
    //prende il giorno corretto per la cella
    val dayOfMonth: TextView =
        itemView.findViewById(R.id.cellDayText)

    //indica con un pallino i giorni con eventi
    val eventIndicator: View =
        itemView.findViewById(R.id.eventIndicator)

    init {
        itemView.setOnClickListener(this) //this è per quello dopo
    }

    //gestisce il click sulle celle
    override fun onClick(view: View) {
        onItemListener.onItemClick(adapterPosition, dayOfMonth.text as String)
    }
}
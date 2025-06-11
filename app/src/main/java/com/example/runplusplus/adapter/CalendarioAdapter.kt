package com.example.runplusplus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.runplusplus.view.CalendarViewHolder
import com.example.runplusplus.R

class CalendarioAdapter(
    private val daysOfMonth: ArrayList<String>,
    private val onItemListener: OnItemListener,
    private val giorniConAllenamenti: Set<Int>
) :
    RecyclerView.Adapter<CalendarViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendar_cell, parent, false)
        //il layout delle singole celle lo faccio qui, pare sia un metodo flessibile che un layout non può creare
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt() //l'altezza della cella è un sesto del del tutto, mettere tutti quei 6 non è bello da vedere ma funziona
        return CalendarViewHolder(view, onItemListener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val dayText = daysOfMonth[position]
        holder.dayOfMonth.text = dayText

        //gestione giornate senza numeri (IE se è vuota non gli assegna un numero)
        if (dayText != "") {
            val day = dayText.toInt()
            holder.eventIndicator.visibility = if (giorniConAllenamenti.contains(day)) {
                View.VISIBLE
            } else {
                View.GONE
            }
        } else {
            holder.eventIndicator.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return daysOfMonth.size
    }

    interface OnItemListener {
        fun onItemClick(position: Int, dayText: String)
    }
}

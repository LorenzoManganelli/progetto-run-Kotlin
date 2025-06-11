package com.example.runplusplus.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime
//rappresenta il set attivo in questo momento, tenuto separato perché (in teoria) è un caso particolare

@Entity(tableName = "rtt_attivo")
data class RTTAttivo(
    @PrimaryKey val id: Int,
    val nome: String,
    val orarioNotifica: LocalTime,
    val giornoInizio: Long
)
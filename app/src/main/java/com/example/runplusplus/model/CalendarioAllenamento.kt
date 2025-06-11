package com.example.runplusplus.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "calendario_allenamento")
data class CalendarioAllenamento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val data: LocalDate,
    val ora: LocalTime, //un orario qualsiasi, reso LocalTime per inviare notifiche
    val tipo: String, //"Corsa", "Pesi", "Ginnastica" come al soliuto
    val dettagli: String //JSON o stringa formattata con i dati dell'allenamento
)

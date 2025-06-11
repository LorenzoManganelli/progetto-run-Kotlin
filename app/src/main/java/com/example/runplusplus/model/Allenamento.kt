package com.example.runplusplus.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allenamenti_table")
data class Allenamento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val data: String,
    val tipo: String, // RICORDA: sono 3 Corsa, Pesi e Ginnastica
    val durata: Int, //in minuti, non è elegante ma funziona
    val calorieBruciate: Int, //questo sarà un semplice valore numerico
    val note: String? = null
)
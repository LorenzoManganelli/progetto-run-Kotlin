package com.example.runplusplus.model
//il metodo migliore per indicare a che giorno si arriva è separarlo dal resto con un suo DAO che tiene conto di tutto
import androidx.room.Entity
import androidx.room.PrimaryKey
//il problema ad avere tutto in un punto era che non ci capivo più niente
@Entity(tableName = "rtt_progresso")
data class RTTProgresso(
    @PrimaryKey val programmaId: Int,
    val giorniCompletati: List<Int> //elenco giorni svolti
)
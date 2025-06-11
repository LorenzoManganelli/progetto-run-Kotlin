package com.example.runplusplus.model

import java.io.Serializable

data class RTTProgramma(
    val id: Int = 0,
    val nome: String = "",
    val tipologia: String = "",
    val difficolta: String = "",
    val durataGiorni: Int = 0,
    val giorni: List<RTTGiorno> = emptyList()
) : Serializable


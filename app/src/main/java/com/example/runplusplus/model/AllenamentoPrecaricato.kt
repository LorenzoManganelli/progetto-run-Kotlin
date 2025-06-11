package com.example.runplusplus.model

data class AllenamentoPrecaricato(
    val id: Int,
    val nome: String,
    val tipo: String, // Corsa, Pesi, Ginnastica
    val descrizione: String,
    val difficolta: String, // Es: Facile, Intermedio, Difficile
    var preferito: Boolean = false,
)
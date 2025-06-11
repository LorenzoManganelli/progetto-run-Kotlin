package com.example.runplusplus.repository

import androidx.lifecycle.LiveData
import com.example.runplusplus.dao.CalendarioDao
import com.example.runplusplus.model.CalendarioAllenamento
import java.time.LocalDate

//repository standard con i dati del DAO
class CalendarioRepository(private val dao: CalendarioDao) {

    fun getAllenamentiFuturi(): LiveData<List<CalendarioAllenamento>> {
        return dao.getAllenamentiFuturi(LocalDate.now())
    }

    fun getAllenamentiInData(data: LocalDate): LiveData<List<CalendarioAllenamento>> {
        return dao.getAllenamentiInData(data)
    }

    suspend fun aggiungiAllenamento(allenamento: CalendarioAllenamento) {
        dao.inserisciAllenamento(allenamento)
    }

    suspend fun eliminaAllenamento(allenamento: CalendarioAllenamento) {
        dao.eliminaAllenamento(allenamento)
    }

    fun getAllenamentoById(id: Int): LiveData<CalendarioAllenamento?> {
        return dao.getAllenamentoById(id)
    }
}
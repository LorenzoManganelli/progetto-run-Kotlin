package com.example.runplusplus.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import com.example.runplusplus.database.AppDatabase
import com.example.runplusplus.model.CalendarioAllenamento
import com.example.runplusplus.repository.CalendarioRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

//le operazioni sono poche, cioè solo leggere, aggiungere o eliminare un allenamento, quello in fondo lo prende da id in particolare
class CalendarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CalendarioRepository
    val allenamentiFuturi: LiveData<List<CalendarioAllenamento>>

    init {
        val dao = AppDatabase.getDatabase(application).calendarioDao()
        repository = CalendarioRepository(dao)
        allenamentiFuturi = repository.getAllenamentiFuturi()
    }

    fun getAllenamentiInData(data: LocalDate): LiveData<List<CalendarioAllenamento>> {
        return repository.getAllenamentiInData(data)
    }

    fun aggiungiAllenamento(allenamento: CalendarioAllenamento) {
        viewModelScope.launch {
            repository.aggiungiAllenamento(allenamento)
        }
    }

    fun eliminaAllenamento(allenamento: CalendarioAllenamento) {
        viewModelScope.launch {
            repository.eliminaAllenamento(allenamento)
        }
    }

    fun getAllenamentoById(id: Int): LiveData<CalendarioAllenamento?> {
        return repository.getAllenamentoById(id)
    }
}
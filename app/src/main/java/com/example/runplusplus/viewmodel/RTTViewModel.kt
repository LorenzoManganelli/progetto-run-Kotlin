package com.example.runplusplus.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.runplusplus.database.AppDatabase
import com.example.runplusplus.model.RTTAttivo
import com.example.runplusplus.model.RTTProgresso
import com.example.runplusplus.repository.RTTRepository
import kotlinx.coroutines.launch

class RTTViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RTTRepository

    val setAttivo: LiveData<RTTAttivo?>

    init {
        val db = AppDatabase.getDatabase(application)
        val attivoDao = db.rttAttivoDao()
        val progressoDao = db.rttProgressoDao()

        repository = RTTRepository(attivoDao, progressoDao)
        setAttivo = repository.getRTTAttivo()
    }

    fun attivaSet(context: Context, rtt: RTTAttivo) {
        viewModelScope.launch {
            repository.attivaRTT(context, rtt)
        }
    }

    fun disattivaSet(context: Context) {
        viewModelScope.launch {
            repository.disattivaRTT(context)
        }
    }

    fun aggiungiGiornoCompletato(programmaId: Int, giorno: Int) {
        viewModelScope.launch {
            repository.aggiungiGiornoCompletato(programmaId, giorno)
        }
    }

    //metodo sospeso da usare con coroutine per ottenere il progresso attuale (tradotto in italiano potrebbe servire)
    suspend fun getProgressoNow(programmaId: Int): RTTProgresso? {
        return repository.getProgressoNow(programmaId)
    }

    //serve per resettare il progresso del programma RTT
    fun resetProgresso(programmaId: Int) {
        viewModelScope.launch {
            repository.resetProgresso(programmaId)
        }
    }
}
package com.example.runplusplus.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.runplusplus.database.AppDatabase
import com.example.runplusplus.model.RTTProgresso
import kotlinx.coroutines.launch
//alla fine non l'ho più usato, ma lo tengo comunque
class RTTProgressoViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).rttProgressoDao()

    fun aggiungiGiornoCompletato(programmaId: Int, giorno: Int) {
        viewModelScope.launch {
            val attuale = dao.getProgressoNow(programmaId)
            val listaAggiornata = (attuale?.giorniCompletati ?: emptyList()).toMutableSet()
            listaAggiornata.add(giorno)
            dao.salvaProgresso(RTTProgresso(programmaId, listaAggiornata.toList()))
        }
    }

    fun getGiorniCompletati(programmaId: Int): LiveData<RTTProgresso?> {
        return dao.getProgresso(programmaId)
    }
}
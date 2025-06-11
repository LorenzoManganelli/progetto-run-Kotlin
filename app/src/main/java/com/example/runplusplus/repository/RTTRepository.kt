package com.example.runplusplus.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.runplusplus.dao.RTTAttivoDao
import com.example.runplusplus.dao.RTTProgressoDao
import com.example.runplusplus.model.RTTAttivo
import com.example.runplusplus.model.RTTProgresso
import com.example.runplusplus.util.cancellaRTTNotification
import com.example.runplusplus.util.scheduleRTTNotification

class RTTRepository(
    private val dao: RTTAttivoDao,
    private val progressoDao: RTTProgressoDao
) {

    //attiva il set selezionato e pianifica la notifica
    suspend fun attivaRTT(context: Context, rtt: RTTAttivo) {
        dao.attivaRTT(rtt)
        scheduleRTTNotification(context, rtt)
    }

    //disattiva il set corrente e cancella le notifiche
    suspend fun disattivaRTT(context: Context) {
        dao.disattivaRTT()
        cancellaRTTNotification(context)
    }

    //restituisce il set attivo
    fun getRTTAttivo(): LiveData<RTTAttivo?> = dao.getRTTAttivo()

    //aggiunge un giorno come completato per il set selezionato
    suspend fun aggiungiGiornoCompletato(programmaId: Int, giorno: Int) {
        progressoDao.aggiornaProgresso(programmaId, giorno)
    }

    //ottiene lo stato di avanzamento attuale del programma
    suspend fun getProgressoNow(programmaId: Int): RTTProgresso? {
        return progressoDao.getProgressoNow(programmaId)
    }

    //resetta il progresso così che si può, eventualmente, riattivare lo stesso programma RTT
    suspend fun resetProgresso(programmaId: Int) {
        progressoDao.resetProgresso(programmaId)
    }
}
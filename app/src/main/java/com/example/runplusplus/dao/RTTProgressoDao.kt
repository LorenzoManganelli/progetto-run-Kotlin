package com.example.runplusplus.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.runplusplus.model.RTTProgresso

@Dao
interface RTTProgressoDao {

    // recupera il progresso attuale come oggetto LiveData (usato per observer)
    @Query("SELECT * FROM rtt_progresso WHERE programmaId = :programmaId LIMIT 1")
    fun getProgresso(programmaId: Int): LiveData<RTTProgresso?>

    // recupera il progresso attuale in modo diretto (suspend per uso interno)
    @Query("SELECT * FROM rtt_progresso WHERE programmaId = :programmaId LIMIT 1")
    suspend fun getProgressoNow(programmaId: Int): RTTProgresso?

    // salva e aggiorna il progresso
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvaProgresso(progresso: RTTProgresso)

    // aggiunge il giorno completato alla lista esistente per la valutazione finale
    @Transaction
    suspend fun aggiornaProgresso(programmaId: Int, nuovoGiorno: Int) {
        val attuale = getProgressoNow(programmaId)
        val nuovaLista = if (attuale != null) {
            if (nuovoGiorno in attuale.giorniCompletati) {
                attuale.giorniCompletati // già presente
            } else {
                attuale.giorniCompletati + nuovoGiorno
            }
        } else {
            listOf(nuovoGiorno)
        }

        salvaProgresso(RTTProgresso(programmaId = programmaId, giorniCompletati = nuovaLista))
    }

    //serve per resettare il progresso del programma RTT
    @Query("DELETE FROM rtt_progresso WHERE programmaId = :programmaId")
    suspend fun resetProgresso(programmaId: Int)
}


package com.example.runplusplus.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Insert
import androidx.room.Query
import com.example.runplusplus.model.CalendarioAllenamento
import java.time.LocalDate

//operazioni DAO
@Dao
interface CalendarioDao {
    @Query("SELECT * FROM calendario_allenamento WHERE data >= :oggi ORDER BY data ASC")
    fun getAllenamentiFuturi(oggi: LocalDate): LiveData<List<CalendarioAllenamento>>

    @Query("SELECT * FROM calendario_allenamento WHERE data = :data")
    fun getAllenamentiInData(data: LocalDate): LiveData<List<CalendarioAllenamento>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserisciAllenamento(allenamento: CalendarioAllenamento)

    @Delete
    suspend fun eliminaAllenamento(allenamento: CalendarioAllenamento)

    @Query("SELECT * FROM calendario_allenamento WHERE id = :id")
    fun getAllenamentoById(id: Int): LiveData<CalendarioAllenamento?>
}
package com.example.runplusplus.dao

import androidx.room.*
import com.example.runplusplus.model.Allenamento
import kotlinx.coroutines.flow.Flow

//le varie operazioi del sistema
@Dao
interface AllenamentoDao {
    @Query("SELECT * FROM allenamenti_table ORDER BY data ASC") //ordina per data extra, serve se la persona clicca di nuovo (?)
    fun getAllAllenamentiOrderByData(): Flow<List<Allenamento>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(allenamento: Allenamento)

    @Update
    suspend fun update(allenamento: Allenamento)

    @Delete
    suspend fun delete(allenamento: Allenamento)

    //Le query per ora sono in ordine di data, un filtro per il tipo e la ricerca per id
    @Query("SELECT * FROM allenamenti_table ORDER BY data DESC")
    fun getAllAllenamenti(): Flow<List<Allenamento>>

    @Query("SELECT * FROM allenamenti_table ORDER BY note ASC")
    fun getAllAllenamentiOrderByNome(): Flow<List<Allenamento>>

    @Query("SELECT * FROM allenamenti_table ORDER BY tipo ASC")
    fun getAllAllenamentiOrderByTipo(): Flow<List<Allenamento>>

    @Query("SELECT * FROM allenamenti_table WHERE id = :id")
    fun getAllenamentoById(id: Int): Flow<Allenamento>

    @Query("SELECT * FROM allenamenti_table WHERE tipo = :tipo") //prende in base al tipo
    fun getAllenamentiByTipo(tipo: String): Flow<List<Allenamento>>

    //funzione sincrona per il log, messa per sicurezza
    @Query("SELECT * FROM allenamenti_table")
    suspend fun getAllAllenamentiSync(): List<Allenamento>
}
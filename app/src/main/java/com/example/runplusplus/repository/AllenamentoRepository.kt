package com.example.runplusplus.repository

import com.example.runplusplus.dao.AllenamentoDao
import com.example.runplusplus.model.Allenamento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
//tutte le operazioni degli allenamenti
class AllenamentoRepository(private val allenamentoDao: AllenamentoDao) {

    val allAllenamenti: Flow<List<Allenamento>> = allenamentoDao.getAllAllenamenti()

    suspend fun insert(allenamento: Allenamento) {
        allenamentoDao.insert(allenamento)
    }

    suspend fun delete(allenamento: Allenamento) {
        allenamentoDao.delete(allenamento)
    }

    suspend fun update(allenamento: Allenamento) {
        allenamentoDao.update(allenamento)
    }

    fun getAllAllenamentiOrderByData(): Flow<List<Allenamento>> {
        return allenamentoDao.getAllAllenamentiOrderByData()
    }

    fun getAllAllenamentiOrderByTipo(): Flow<List<Allenamento>> {
        return allenamentoDao.getAllAllenamentiOrderByTipo()
    }

    fun getAllAllenamentiOrderByNome(): Flow<List<Allenamento>> {
        return allenamentoDao.getAllAllenamentiOrderByNome()
    }

    fun getAllenamentiByTipo(tipo: String): Flow<List<Allenamento>> {
        return allenamentoDao.getAllenamentiByTipo(tipo)
    }

    suspend fun getAllenamentoById(id: Int): Allenamento? {
        return allenamentoDao.getAllenamentoById(id).firstOrNull()
    }

    suspend fun getAllAllenamentiSync(): List<Allenamento> {
        return allenamentoDao.getAllAllenamentiSync()
    }
}
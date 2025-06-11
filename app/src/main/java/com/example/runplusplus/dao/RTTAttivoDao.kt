package com.example.runplusplus.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.runplusplus.model.RTTAttivo

@Dao
interface RTTAttivoDao {

    @Query("SELECT * FROM rtt_attivo LIMIT 1")
    fun getRTTAttivo(): LiveData<RTTAttivo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun attivaRTT(rtt: RTTAttivo)

    @Query("DELETE FROM rtt_attivo")
    suspend fun disattivaRTT()

    //query di testing per RTT
    @Query("SELECT * FROM rtt_attivo LIMIT 1")
    suspend fun getRTTAttivoNow(): RTTAttivo?

}

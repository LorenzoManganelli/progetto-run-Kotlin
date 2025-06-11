package com.example.runplusplus.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.runplusplus.dao.AllenamentoDao
import com.example.runplusplus.dao.CalendarioDao
import com.example.runplusplus.dao.RTTAttivoDao
import com.example.runplusplus.dao.RTTProgressoDao
import com.example.runplusplus.model.Allenamento
import com.example.runplusplus.model.CalendarioAllenamento
import com.example.runplusplus.model.RTTAttivo
import com.example.runplusplus.model.RTTProgresso

@Database(entities = [
    Allenamento::class,
    CalendarioAllenamento::class,
    RTTAttivo::class,
    RTTProgresso::class
                     ],
    version = 4, exportSchema = false)

@TypeConverters(Converters::class) // Necessario per Date e per l'RTT
abstract class AppDatabase : RoomDatabase() {

    //tutti i db interni
    abstract fun allenamentoDao(): AllenamentoDao
    abstract fun calendarioDao(): CalendarioDao
    abstract fun rttAttivoDao(): RTTAttivoDao
    abstract fun rttProgressoDao(): RTTProgressoDao //il resto dell'RTT sta su Firestore, questo può essere interno

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Per semplificare gli aggiornamenti del database in fase di sviluppo (CANCELLA I DATI SE CAMBIA DI VERSIONE DA TOGLIERE)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.example.runplusplus.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.runplusplus.dao.AllenamentoDao
import com.example.runplusplus.model.Allenamento

@Database(entities = [Allenamento::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // Necessario per Date
abstract class AppDatabase : RoomDatabase() {
    abstract fun allenamentoDao(): AllenamentoDao

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
                    .fallbackToDestructiveMigration() // Per semplificare gli aggiornamenti del database in fase di sviluppo
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
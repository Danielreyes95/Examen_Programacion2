package com.danielreyes.examen_programacion.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Gasto::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateConverter::class)
abstract class BaseDatos : RoomDatabase() {
    abstract fun gastoDao(): GastoDao

    companion object{
        @Volatile
        private var instance: BaseDatos? = null

        fun getInstance(contexto: Context): BaseDatos {
            return instance ?: synchronized(this){
                Room.databaseBuilder(
                    contexto.applicationContext,
                    BaseDatos::class.java,
                    "gastos.bd"
                ).build()
            }.also {
                instance = it
            }
        }
    }
}


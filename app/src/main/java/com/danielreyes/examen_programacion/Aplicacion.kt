package com.danielreyes.examen_programacion

import com.danielreyes.examen_programacion.db.BaseDatos
import android.app.Application
import androidx.room.Room


class Aplicacion : Application() {

    private val bd by lazy { Room.databaseBuilder(this, BaseDatos::class.java, "gastos.db").build() }
    val gastoDao by lazy { bd.gastoDao() }
}



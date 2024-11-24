package com.danielreyes.examen_programacion.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var medidor: Int,
    var fecha: LocalDate,
    var categoria: String,

    )



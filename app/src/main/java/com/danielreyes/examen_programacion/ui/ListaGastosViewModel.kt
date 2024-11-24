package com.danielreyes.examen_programacion.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.danielreyes.examen_programacion.Aplicacion
import com.danielreyes.examen_programacion.db.Gasto
import com.danielreyes.examen_programacion.db.GastoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListaGastosViewModel(private val gastoDao: GastoDao) : ViewModel() {

    private val _gastos = MutableLiveData<List<Gasto>>()
    val gastos: LiveData<List<Gasto>> get() = _gastos

    init {
        obtenerGastos()
    }

    fun insertarGasto(gasto: Gasto){
        viewModelScope.launch (Dispatchers.IO){
            gastoDao.insertar(gasto)
            obtenerGastos()
        }
    }

    private fun obtenerGastos(){
        viewModelScope.launch(Dispatchers.IO) {
            val listaGastos = gastoDao.obtenerTodos()
            _gastos.postValue(listaGastos)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacion = (this[APPLICATION_KEY] as Aplicacion)
                ListaGastosViewModel(aplicacion.gastoDao)
            }
        }
    }
}

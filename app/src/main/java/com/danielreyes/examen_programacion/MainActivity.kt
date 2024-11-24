package com.danielreyes.examen_programacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danielreyes.examen_programacion.db.BaseDatos
import com.danielreyes.examen_programacion.db.Gasto
import com.danielreyes.examen_programacion.ui.ListaGastosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppGastosUI()
        }
    }
}

@Composable
fun AppGastosUI(
    navController: NavHostController = rememberNavController(),
    vmListaGastos: ListaGastosViewModel = viewModel(factory = ListaGastosViewModel.Factory)
){
    NavHost(
        navController = navController,
        startDestination = "inicio")
    {
        composable("inicio") {
            PantallaListaGastos(
               vmListaGastos = vmListaGastos,
                onAdd = { navController.navigate("form") }
            )
        }
        composable("form") {
            PantallaFormMedidor(vmListaGastos)
        }
    }
}



@Composable
fun PantallaFormMedidor(vmListaGastos: ListaGastosViewModel) {
    var medidor by rememberSaveable { mutableIntStateOf(0) }
    var fecha by rememberSaveable { mutableStateOf("") }
    var categoriaSeleccionada by rememberSaveable { mutableStateOf("Agua") }

    val contexto = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),

        ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Registro Medidor",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)

            )
            TextField(
                value = medidor.toString(),
                onValueChange = { medidor = it.toIntOrNull() ?: 0 },
                label = { Text("Medidor") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
            TextField(
                value = fecha,
                onValueChange = { fecha = it },
                placeholder = { Text("2024-01-18") },
                label = { Text("Fecha") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
            Text(
                text = "Medidor de:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OpcionesCategoriasUI(onCategoriaSeleccionada = { categoriaSeleccionada = it })

            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val bd = BaseDatos.getInstance(contexto)
                        val dao = bd.gastoDao()
                        val gasto = Gasto(
                            medidor = medidor,
                            fecha = LocalDate.parse(fecha),
                            categoria = categoriaSeleccionada
                        )
                        dao.insertar(gasto)
                        vmListaGastos.insertarGasto(gasto)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Registrar medición")
            }
        }
    }
}

@Composable
fun OpcionesCategoriasUI(onCategoriaSeleccionada: (String) -> Unit) {
    val categorias = listOf("Agua", "Luz", "Gas")
    var categoriaSeleccionada by rememberSaveable { mutableStateOf(categorias[0]) }
    Column(Modifier.selectableGroup()) {
        categorias.forEach { categoria ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (categoria == categoriaSeleccionada),
                        onClick = {
                            categoriaSeleccionada = categoria
                            onCategoriaSeleccionada(categoria)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (categoria == categoriaSeleccionada),
                    onClick = null
                )
                Text(text = categoria, modifier = Modifier.padding(start = 16.dp))
            }
        }
    }
}


@Composable
fun CategoriasItem(gasto: Gasto) {
    val icon = when (gasto.categoria) {
        "Agua" -> R.drawable.agua
        "Luz" -> R.drawable.luz
        "Gas" -> R.drawable.gas
        else -> null
    }

Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(2.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = gasto.categoria,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${gasto.medidor} ",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = gasto.fecha.format(DateTimeFormatter.ISO_LOCAL_DATE),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun PantallaListaGastos(
    vmListaGastos: ListaGastosViewModel = viewModel(factory = ListaGastosViewModel.Factory),
    onAdd: () -> Unit
) {
    val gastos by vmListaGastos.gastos.observeAsState(emptyList())
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onAdd()
            }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Agregar Gasto")
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it.calculateTopPadding())
        ) {
            items(gastos) { gasto ->
                CategoriasItem(gasto)
                HorizontalDivider()
            }
        }
    }
}





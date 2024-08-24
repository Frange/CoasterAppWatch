package com.jmr.coasterappwatch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import com.jmr.coasterappwatch.domain.base.AppResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: QueueViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.requestCompanyList()

        setContent {
            MainScreen(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: QueueViewModel) {
    val companyListResult by viewModel.companyList.observeAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            viewModel.requestCompanyList()
        }
    )

    var selectedOption by remember { mutableStateOf("Company") }
    var selectedOptionId by remember { mutableStateOf(0) } // Para almacenar el ID seleccionado
    val options = mutableListOf<String>()
    val ids = mutableListOf<Int>()
    var expanded by remember { mutableStateOf(false) }

    companyListResult?.let { result ->
        if (result is AppResult.Success) {
            options.addAll(result.data.map {
                it.name ?: "-"
            })  // Obtén los nombres de las compañías
            ids.addAll(result.data.map { it.id ?: 0 })  // Obtén los IDs de las compañías
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // El Spinner que permanece fijo en la parte superior
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    readOnly = true,
                    value = selectedOption,
                    onValueChange = {},
                    label = { Text("Select an option") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .height(56.dp) // Tamaño del Spinner
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEachIndexed { index, option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedOption = option
                                selectedOptionId = ids[index] // Guardamos el ID seleccionado
                                expanded = false
                                viewModel.requestRides(selectedOptionId) // Realizamos la llamada con el ID seleccionado
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // La lista que ocupa el resto de la pantalla con scroll independiente
            DisplayList(viewModel, selectedOption)
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

fun onOptionSelected(viewModel: QueueViewModel, option: String) {
    when (option) {
        "Company" -> viewModel.requestCompanyList()
        "Park" -> {
            // Lógica para cargar la lista de parques
        }

        "Coaster" -> {
            // Lógica para cargar la lista de coasters
        }
    }
}

@Composable
fun DisplayList(viewModel: QueueViewModel, selectedOption: String) {
    when (selectedOption) {
        "Company" -> {
            val companyListResult by viewModel.companyList.observeAsState()
            companyListResult?.let { result ->
                when (result) {
                    is AppResult.Success -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(result.data) { company ->
                                Text(text = company.name ?: "")
                            }
                        }
                    }

                    is AppResult.Error -> {
                        Text(text = "Error: $result")
                    }

                    is AppResult.Loading -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }

                    is AppResult.Exception -> {
                        // Manejar la excepción aquí
                    }
                }
            }
        }

        "Park" -> {
            // Similar lógica para la lista de parques
        }

        "Coaster" -> {
            // Similar lógica para la lista de coasters
        }
    }
}
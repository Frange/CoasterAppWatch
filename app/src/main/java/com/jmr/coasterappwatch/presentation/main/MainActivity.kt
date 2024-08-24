package com.jmr.coasterappwatch.presentation.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.presentation.park.ParkActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: QueueViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedParkInfo = getSelectedPark(this)

        setContent {
            if (selectedParkInfo != null) {
                // Si hay un parque guardado, navega a ParkActivity.
                startActivity(Intent(this, ParkActivity::class.java).apply {
                    putExtra("park_info_id", selectedParkInfo)
                })
            } else {
                // Si no hay parque guardado, muestra la lista de parques.
                ParkInfoScreen(viewModel) { parkInfoId ->
                    saveSelectedParkInfoId(this, parkInfoId)
                    startActivity(Intent(this, ParkActivity::class.java).apply {
                        putExtra("park_info_id", parkInfoId)
                    })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.requestAllParkList() // Cargar lista de parques.
        setContent {
            ParkInfoScreen(viewModel) { parkInfoId ->
                saveSelectedParkInfoId(this, parkInfoId)
                startActivity(Intent(this, ParkActivity::class.java).apply {
                    putExtra("park_info_id", parkInfoId)
                })
            }
        }
    }

    private fun saveSelectedParkInfoId(context: Context, parkInfoId: Int) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("selected_park_info_id", parkInfoId)
        editor.apply()
    }

    private fun getSelectedPark(context: Context): Int? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val parkInfoId = sharedPreferences.getInt("selected_park_info_id", -1)
        return if (parkInfoId != -1) parkInfoId else null
    }
}

@Composable
fun ParkInfoScreen(viewModel: QueueViewModel, onParkInfoSelected: (Int) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberScalingLazyListState()

    val parkInfoListResult by viewModel.parkInfoList.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.requestAllParkList()
    }

    LaunchedEffect(parkInfoListResult) {
        if (parkInfoListResult is AppResult.Success) {
            val indexToScrollTo = 0 // Ajusta el índice según el ítem que quieras centrar
            listState.animateScrollToItem(indexToScrollTo)
        }
    }

    
}
//@Composable
//fun ParkInfoScreen(viewModel: QueueViewModel, onParkInfoSelected: (Int) -> Unit) {
//    val coroutineScope = rememberCoroutineScope()
//    val listState = rememberScalingLazyListState()
//
//    val parkInfoListResult by viewModel.parkInfoList.observeAsState()
//
//    LaunchedEffect(Unit) {
//        viewModel.requestAllParkList()
//    }
//
//    LaunchedEffect(parkInfoListResult) {
//        if (parkInfoListResult is AppResult.Success) {
//            val indexToScrollTo = 0 // Ajusta el índice según el ítem que quieras centrar
//            listState.animateScrollToItem(indexToScrollTo)
//        }
//    }
//
//    // Renderiza la interfaz de usuario
//    Box(modifier = Modifier.fillMaxSize()) {
//        when (val result = parkInfoListResult) {
//            is AppResult.Success -> {
//                val parkInfoList = result.data
//
//                ScalingLazyColumn(
//                    state = listState,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    // Elemento de cabecera
//                    item {
//                        ListHeader {
//                            Text(
//                                text = "Parques de atracciones",
//                                style = TextStyle(
//                                    color = Color.White,
//                                    fontSize = 16.sp,
//                                    fontWeight = FontWeight.Bold
//                                ),
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(8.dp),
//                                textAlign = TextAlign.Center
//                            )
//                        }
//                    }
//
//                    // Elementos de la lista
//                    items(parkInfoList.size) { index ->
//                        val parkInfo = parkInfoList[index]
//                        val isSelected =
//                            listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index == index
//                        val scale = if (isSelected) 1.2f else 0.7f
//                        val alpha = if (isSelected) 1f else 0.6f
//
//                        Chip(
//                            onClick = {
//                                coroutineScope.launch {
////                                    listState.animateScrollToItem(index)
//                                    onParkInfoSelected(parkInfo.id!!)
//                                }
//                            },
//                            label = {
//                                Text(
//                                    parkInfo.name,
//                                    modifier = Modifier.graphicsLayer(
//                                        scaleX = scale,
//                                        scaleY = scale,
//                                        alpha = alpha
//                                    ),
//                                    textAlign = TextAlign.Center
//                                )
//                            },
//                            colors = if (isSelected) {
//                                ChipDefaults.primaryChipColors(
//                                    backgroundColor = Color.Blue,
//                                    contentColor = Color.White
//                                )
//                            } else {
//                                ChipDefaults.secondaryChipColors(
//                                    backgroundColor = Color.Gray,
//                                    contentColor = Color.Black
//                                )
//                            },
//                            modifier = Modifier
//                                .height(if (isSelected) 50.dp else 30.dp)
//                                .padding(4.dp)
//                                .fillMaxWidth()
//                        )
//                    }
//                }
//            }
//
//            is AppResult.Error -> {
//                // Puedes mostrar un mensaje de error aquí si lo deseas
//            }
//
//            is AppResult.Loading -> {
//                // Puedes mostrar un indicador de carga aquí si lo deseas
//            }
//
//            is AppResult.Exception -> {
//                // Puedes manejar excepciones aquí si lo deseas
//            }
//
//            null -> {}
//        }
//    }
//}

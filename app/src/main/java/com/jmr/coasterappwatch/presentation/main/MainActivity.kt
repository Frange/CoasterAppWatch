package com.jmr.coasterappwatch.presentation.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import com.jmr.coasterappwatch.R
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.ParkInfo
import com.jmr.coasterappwatch.presentation.park.ParkActivity
import dagger.hilt.android.AndroidEntryPoint

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
                RenderParkInfoScreen(viewModel) { parkInfoId ->
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
            RenderParkInfoScreen(viewModel) { parkInfoId ->
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
fun RenderParkInfoScreen(viewModel: QueueViewModel, onParkInfoSelected: (Int) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
//    val listState = rememberScalingLazyListState()
    val listState = rememberLazyListState()


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

    RenderParkInfoList(parkInfoListResult, listState)
}

@Composable
fun RenderParkInfoList(
    parkInfoListResult: AppResult<List<ParkInfo>>?,
    listState: LazyListState
) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        when (parkInfoListResult) {
            is AppResult.Success -> {
                val parkInfoList = parkInfoListResult.data

                item {
                    ListHeader {
                        Text(text = "List Header")
                    }
                }
                items(parkInfoList.size) { index ->
                    RenderChip(parkInfoList[index], listState, index)
                }
            }

            is AppResult.Error -> {}
            is AppResult.Exception -> {}
            is AppResult.Loading -> {}
            null -> {

            }
        }
    }
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun RenderChip(parkInfo: ParkInfo, listState: LazyListState, index: Int) {
    val isSelected = listState.firstVisibleItemIndex == index
//    val isSelected = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index == index
    val scale = if (isSelected) 1.0f else 0.7f
    val alpha = if (isSelected) 1f else 0.6f

    Chip(
        onClick = { },
        label = {
            Text(
                text = parkInfo.name,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = if (isSelected) Color.White else Color.Gray,
                    fontSize = if (isSelected) 12.sp else 8.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .scale(scale, 1f)
            .alpha(alpha)
            .height(if (isSelected) 36.dp else 20.dp),
        colors = if (isSelected) ChipDefaults.chipColors(
            backgroundColor = Color(
                ContextCompat.getColor(
                    LocalContext.current,
                    R.color.primary
                )
            )
        ) else ChipDefaults.secondaryChipColors(),
    )
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

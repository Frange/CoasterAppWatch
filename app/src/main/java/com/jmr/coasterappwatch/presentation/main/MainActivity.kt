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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
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

    private fun goToPark(selectedParkInfo: Int) {
        startActivity(Intent(this, ParkActivity::class.java).apply {
            putExtra("park_info_id", selectedParkInfo)
        })
    }

    @Composable
    private fun RenderScreen() {
        RenderParkInfoScreen(viewModel) { parkInfoId ->
            saveSelectedParkInfoId(this, parkInfoId)
            startActivity(Intent(this, ParkActivity::class.java).apply {
                putExtra("park_info_id", parkInfoId)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.requestAllParkList()

        val selectedParkInfo = getSelectedPark(this)

        setContent {
            if (selectedParkInfo != null) {
                goToPark(selectedParkInfo)
            } else {
                RenderScreen()
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

    RenderParkInfoList(parkInfoListResult, listState, onParkInfoSelected)
}

@Composable
fun RenderParkInfoList(
    parkInfoListResult: AppResult<List<ParkInfo>>?,
    listState: ScalingLazyListState,
    onParkInfoSelected: (Int) -> Unit
) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = listState // Aseguramos que el estado de la lista se pase correctamente
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
                    RenderChip(parkInfoList[index], listState, index, onParkInfoSelected)
                }
            }

            is AppResult.Error -> {}
            is AppResult.Exception -> {}
            is AppResult.Loading -> {}
            null -> {}
        }
    }
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun RenderChip(
    parkInfo: ParkInfo,
    listState: ScalingLazyListState,
    index: Int,
    onParkInfoSelected: (Int) -> Unit
) {
    val density = LocalDensity.current

    // Obtener la información de los elementos visibles
    val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo

    // Calcular el centro de la pantalla en píxeles
    val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val screenCenter = screenHeightPx / 2

    // Calcular el índice del elemento más cercano al centro
    val closestItemIndex = visibleItemsInfo
        .takeIf { it.isNotEmpty() } // Asegurar que no esté vacío
        ?.minByOrNull { itemInfo ->
            val itemCenter = (itemInfo.offset + 220 + itemInfo.size / 2).toFloat()
            kotlin.math.abs(itemCenter - screenCenter)
        }?.index

    // Verificar si este es el elemento seleccionado
    val isSelected = closestItemIndex == index

    val scale = if (isSelected) 1.0f else 0.7f
    val alpha = if (isSelected) 1f else 0.6f

    Chip(
        onClick = {
            onParkInfoSelected(parkInfo.id!!)
        },
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
            .scale(scale)
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

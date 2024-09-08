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
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RenderScreen()
        }
    }

    @Composable
    private fun RenderScreen() {
        val selectedParkInfo by remember { mutableStateOf(getSelectedPark(this)) }

        if (selectedParkInfo != null) {
            LaunchedEffect(selectedParkInfo) {
                startActivity(Intent(this@MainActivity, ParkActivity::class.java).apply {
                    putExtra("park_info_id", selectedParkInfo)
                })
                clearSelectedParkInfo(this@MainActivity)
            }
        }

        RenderParkInfoScreen(viewModel) { parkInfoId ->
            saveSelectedParkInfoId(this, parkInfoId)
            startActivity(Intent(this, ParkActivity::class.java).apply {
                putExtra("park_info_id", parkInfoId)
            })
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

    private fun clearSelectedParkInfo(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("selected_park_info_id")
        editor.apply()
    }
}

@Composable
fun RenderParkInfoScreen(viewModel: MainViewModel, onParkInfoSelected: (Int) -> Unit) {
    val listState = rememberScalingLazyListState()
    val parkInfoListResult by viewModel.parkInfoList.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.requestAllParkList()
    }

    LaunchedEffect(parkInfoListResult) {
        if (parkInfoListResult is AppResult.Success) {
            val indexToScrollTo = 0
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
        state = listState
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
    val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
    val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val screenCenter = screenHeightPx / 2

    val closestItemIndex = visibleItemsInfo
        .takeIf { it.isNotEmpty() }
        ?.minByOrNull { itemInfo ->
            val itemCenter = (itemInfo.offset + 260 + itemInfo.size / 2).toFloat()
            kotlin.math.abs(itemCenter - screenCenter)
        }?.index

    val isSelected = closestItemIndex == index
    val scale = if (isSelected) 1.0f else 0.8f
    val alpha = if (isSelected) 1f else 0.6f

    // ----------------< CONFIGURATION VALUES >-----------------
    val fontColor = if (isSelected) Color.White else Color.Gray
    val fontSize = if (isSelected) 15.sp else 12.sp
    val height = if (isSelected) 40.dp else 36.dp
    // ----------------------------------------------------------

    Chip(
        onClick = {
            onParkInfoSelected(parkInfo.id!!)
        },
        label = {
            Text(
                text = parkInfo.name,
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = TextStyle(
                    color = fontColor,
                    fontSize = fontSize,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .scale(scale)
            .alpha(alpha)
            .height(height),
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

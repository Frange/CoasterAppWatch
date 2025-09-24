package com.jmr.coasterappwatch.presentation.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import com.jmr.coasterappwatch.R
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.ParkInfo
import com.jmr.coasterappwatch.presentation.park.ParkActivity
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState

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

    LaunchedEffect(Unit) { viewModel.requestAllParkList() }

    // selectedIndex calculado de forma declarativa (sin efectos)
    val selectedIndex by remember {
        derivedStateOf {
            val visible = listState.layoutInfo.visibleItemsInfo
            if (visible.isEmpty()) null
            else {
                val screenCenter =
                    listState.layoutInfo.viewportEndOffset / 2f // usa viewport para ser independiente de density
                visible.minByOrNull { item ->
                    kotlin.math.abs(item.offset + item.size / 2f - screenCenter)
                }?.index
            }
        }
    }

    val parkInfos: List<ParkInfo> = when (val result = parkInfoListResult) {
        is AppResult.Success -> result.data
        else -> emptyList()
    }

    CenterSnapList(
        parkInfoList = parkInfos,
        onParkInfoSelected = onParkInfoSelected
    )
}

@Composable
fun CenterSnapList(
    parkInfoList: List<ParkInfo>,
    onParkInfoSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(listState)

    val configuration = LocalConfiguration.current
    val halfScreenDp = (configuration.screenHeightDp.dp) / 2

    val selectedIndex by remember(listState, parkInfoList) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visible = layoutInfo.visibleItemsInfo
            if (parkInfoList.isEmpty()) return@derivedStateOf 0
            if (visible.isEmpty()) return@derivedStateOf 0

            // centro real del viewport en px
            val centerPx = layoutInfo.viewportStartOffset + layoutInfo.viewportSize.height / 2f

            // rangos absolutos en LazyColumn por los spacers
            val headerCount = 1
            val dataStartIndex = headerCount
            val dataEndIndex = dataStartIndex + parkInfoList.size - 1

            // solo items visibles que corresponden a datos (filtramos spacers)
            val visibleData = visible.filter { it.index in dataStartIndex..dataEndIndex }

            // si no hay items de datos visibles, resolvemos por spacer visible
            if (visibleData.isEmpty()) {
                if ((visible.firstOrNull()?.index ?: 0) < dataStartIndex) return@derivedStateOf 0
                return@derivedStateOf parkInfoList.lastIndex
            }

            // altura representativa y umbral (ajustable)
            val avgItemHeightPx = visibleData.map { it.size }
                .let { if (it.isEmpty()) 0f else it.average().toFloat() }
                .coerceAtLeast(1f)
            val thresholdPx = avgItemHeightPx * 0.35f // 35% evita falsas detecciones de top/bottom

            val viewportBottom = layoutInfo.viewportStartOffset + layoutInfo.viewportSize.height

            // ---- atTop: SOLO cuando el spacer superior sigue siendo el primer item visible (índice absoluto 0)
            val atTop = listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset <= thresholdPx.toInt()
            if (atTop) return@derivedStateOf 0

            // ---- atBottom: último visible de datos y su borde inferior cercano al fondo
            val lastVisibleData = visibleData.last()
            val lastItemBottom = lastVisibleData.offset + lastVisibleData.size
            val atBottom = lastVisibleData.index == dataEndIndex &&
                    lastItemBottom >= (viewportBottom - thresholdPx)
            if (atBottom) return@derivedStateOf parkInfoList.lastIndex

            // ---- en resto de casos: calcular centros de los items visibles y elegir el más cercano al centroPx
            val closestAbsoluteIndex = visibleData
                .minByOrNull { kotlin.math.abs((it.offset + it.size / 2f) - centerPx) }
                ?.index ?: dataStartIndex

            // convertir índice absoluto (LazyColumn) -> relativo dentro de parkInfoList
            val closestRelative = (closestAbsoluteIndex - dataStartIndex).coerceIn(0, parkInfoList.lastIndex)
            closestRelative
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        state = listState,
        flingBehavior = flingBehavior,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 0.dp)
    ) {
        item { Spacer(modifier = Modifier.height(halfScreenDp)) }

        itemsIndexed(parkInfoList) { index, parkInfo ->
            RenderChip(
                parkInfo = parkInfo,
                index = index,
                selectedIndex = selectedIndex,
                onParkInfoSelected = onParkInfoSelected
            )
        }

        item { Spacer(modifier = Modifier.height(halfScreenDp)) }
    }
}

@Composable
fun RenderChip(
    parkInfo: ParkInfo,
    index: Int,
    selectedIndex: Int?,
    onParkInfoSelected: (Int) -> Unit
) {
    val isSelected = selectedIndex != null && selectedIndex == index

    val scale by animateFloatAsState(if (isSelected) 1.05f else 0.95f)
    val alpha by animateFloatAsState(if (isSelected) 1f else 0.85f)
    val height = if (isSelected) 44.dp else 36.dp
    val fontSize = if (isSelected) 15.sp else 13.sp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 6.dp)
            .height(height)
            .scale(scale)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Chip(
            onClick = { onParkInfoSelected(parkInfo.id!!) },
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            label = {
                Text(
                    text = parkInfo.name,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = fontSize,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color.LightGray
                    )
                )
            },
            colors = if (isSelected)
                ChipDefaults.chipColors(
                    backgroundColor = Color(
                        ContextCompat.getColor(
                            LocalContext.current,
                            R.color.primary
                        )
                    )
                )
            else ChipDefaults.secondaryChipColors()
        )
    }
}
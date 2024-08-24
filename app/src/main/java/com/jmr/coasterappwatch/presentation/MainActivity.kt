package com.jmr.coasterappwatch.presentation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import com.jmr.coasterappwatch.domain.base.AppResult
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
                RideListScreen(viewModel)
            } else {
                EnhancedScalingLazyColumnScreen(viewModel) { parkInfoId ->
                    saveSelectedParkInfoId(this, parkInfoId)
                    startActivity(Intent(this, RideListActivity::class.java).apply {
                        putExtra("park_info_id", parkInfoId)
                    })
                }
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
fun EnhancedScalingLazyColumnScreen(viewModel: QueueViewModel, onParkInfoSelected: (Int) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val itemSpacing = 8.dp
    val state = rememberScalingLazyListState()

    val parkInfoListResult by viewModel.parkInfoList.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.requestAllParkList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val result = parkInfoListResult) {
            is AppResult.Success -> {
                val parkInfoList = result.data // `result.data` es una lista de `ParkInfo`

                ScalingLazyColumn(
                    state = state,
                    modifier = Modifier.fillMaxSize(),
                    anchorType = ScalingLazyListAnchorType.ItemStart,
                    verticalArrangement = Arrangement.spacedBy(itemSpacing),
                    autoCentering = AutoCenteringParams(itemOffset = 0)
                ) {
                    items(parkInfoList.size) { index ->
                        val parkInfo = parkInfoList[index]
                        val isSelected = state.centerItemIndex == index
                        val scale = if (isSelected) 1.1f else 0.4f
                        val alpha = if (isSelected) 1f else 0.6f
                        val backgroundColor = if (isSelected) Color.Blue else Color.Gray

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp, vertical = 2.dp)
                                .background(
                                    backgroundColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    2.dp,
                                    if (isSelected) Color.White else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    coroutineScope.launch {
                                        state.animateScrollToItem(index)
                                        onParkInfoSelected(parkInfo.id!!)
                                    }
                                }
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    alpha = alpha
                                )
                                .padding(4.dp),
                            Alignment.Center
                        ) {
                            Text(
                                text = parkInfo.name,
                                color = Color.White,
                                style = TextStyle(fontSize = 12.sp),
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            is AppResult.Error -> {
                Text("Error: ${result.exception.message}")
            }

            is AppResult.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is AppResult.Exception -> {
                Text("Exception: ${result.exception.message}")
            }

            null -> {}
        }
    }
}

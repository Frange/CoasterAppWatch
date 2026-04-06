package com.jmr.coasterappwatch.presentation.main

import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState // <--- IMPORTANTE
import androidx.wear.compose.foundation.lazy.items
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.*
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Ride
import com.jmr.coasterappwatch.presentation.park.ParkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberSwipeDismissableNavController()

                SwipeDismissableNavHost(
                    navController = navController,
                    startDestination = "park_list"
                ) {
                    composable("park_list") {
                        val viewModel: MainViewModel = hiltViewModel()
                        val parkResult by viewModel.parkInfoList.collectAsState()

                        LaunchedEffect(Unit) { viewModel.requestAllParkList() }

                        RenderParkInfoScreen(parkResult) { parkId ->
                            navController.navigate("park_detail/$parkId")
                        }
                    }

                    composable("park_detail/{parkId}") { backStackEntry ->
                        val parkId = backStackEntry.arguments?.getString("parkId")?.toInt() ?: -1
                        val viewModel: ParkViewModel = hiltViewModel()
                        val parkState by viewModel.parkState.collectAsState()

                        LaunchedEffect(parkId) {
                            viewModel.loadParkDetails(parkId)
                        }

                        when (val result = parkState) {
                            is AppResult.Success -> {
                                ParkListScreen(park = result.data) { selectedRide ->
                                    println("Click en: ${selectedRide.name}")
                                }
                            }

                            is AppResult.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            is AppResult.Error -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Error: ${result.exception.message}", color = Color.Red)
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RenderParkInfoScreen(
    result: AppResult<List<com.jmr.coasterappwatch.domain.model.ParkInfo>>,
    onParkSelected: (Int) -> Unit
) {
    val listState = rememberScalingLazyListState()
    Scaffold(
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (result) {
                is AppResult.Success -> {
                    ScalingLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        autoCentering = AutoCenteringParams(itemIndex = 0)
                    ) {
                        items(result.data) { park ->
                            Chip(
                                onClick = { onParkSelected(park.id ?: 0) },
                                label = { Text(park.name) },
                                colors = ChipDefaults.primaryChipColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                is AppResult.Loading -> CircularProgressIndicator()
                is AppResult.Error -> Text("Error", color = Color.Red)
                else -> {}
            }
        }
    }
}

@Composable
fun ParkListScreen(
    park: com.jmr.coasterappwatch.domain.model.Park,
    onRideClick: (Ride) -> Unit
) {
    val listState = rememberScalingLazyListState()
    Scaffold(
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            autoCentering = AutoCenteringParams(itemIndex = 0)
        ) {
            park.landList.forEach { land ->
                item {
                    ListHeader {
                        Text(land.name, style = MaterialTheme.typography.caption1)
                    }
                }
                items(land.rideList) { ride ->
                    RideChip(ride = ride, onClick = onRideClick)
                }
            }

            if (park.landList.isEmpty()) {
                items(park.rideList) { ride ->
                    RideChip(ride = ride, onClick = onRideClick)
                }
            }
        }
    }
}

@Composable
fun RideChip(ride: Ride, onClick: (Ride) -> Unit) {
    val isClosed = !ride.isOpen || (ride.waitTime == null)
    Chip(
        onClick = { onClick(ride) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(ride.name, maxLines = 1) },
        secondaryLabel = {
            Text(
                text = if (isClosed) "CLOSED" else "${ride.waitTime} MIN",
                color = if (isClosed) Color.Red else Color.Green
            )
        },
        colors = if (ride.isFavourite) ChipDefaults.primaryChipColors() else ChipDefaults.secondaryChipColors()
    )
}
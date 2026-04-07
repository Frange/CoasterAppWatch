package com.jmr.coasterappwatch.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.*
import androidx.wear.compose.foundation.lazy.*
import androidx.wear.compose.navigation.*
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Ride
import com.jmr.coasterappwatch.domain.model.ParkInfo
import com.jmr.coasterappwatch.presentation.park.ParkViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState

val chipHeight = 48.dp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberSwipeDismissableNavController()
                val mainViewModel: MainViewModel = hiltViewModel()
                val lastParkId by mainViewModel.lastParkId.collectAsState()

                LaunchedEffect(lastParkId) {
                    lastParkId?.let { id ->
                        navController.navigate("park_detail/$id")
                    }
                }

                SwipeDismissableNavHost(
                    navController = navController,
                    startDestination = "park_list"
                ) {
                    composable("park_list") {
                        val parkResult by mainViewModel.parkInfoList.collectAsState()

                        LaunchedEffect(Unit) {
                            mainViewModel.clearLastPark()
                        }

                        RenderParkInfoScreen(
                            result = parkResult,
                            onParkSelected = { id ->
                                mainViewModel.saveLastPark(id)
                                navController.navigate("park_detail/$id")
                            },
                            onFavoriteToggle = { id -> mainViewModel.toggleParkFavorite(id) }
                        )
                    }

                    composable("park_detail/{parkId}") { backStackEntry ->
                        val parkId = backStackEntry.arguments?.getString("parkId")?.toInt() ?: -1
                        val parkViewModel: ParkViewModel = hiltViewModel()
                        val parkState by parkViewModel.parkState.collectAsState()

                        LaunchedEffect(parkId) { parkViewModel.loadParkDetails(parkId) }

                        Box(modifier = Modifier.fillMaxSize()) {
                            when (val result = parkState) {
                                is AppResult.Success -> {
                                    ParkDetailScreen(
                                        park = result.data,
                                        onRideFavorite = { rideId ->
                                            parkViewModel.toggleRideFavorite(rideId)
                                        }
                                    )
                                }

                                is AppResult.Loading -> CircularProgressIndicator(
                                    Modifier.align(
                                        Alignment.Center
                                    )
                                )

                                is AppResult.Error -> Text(
                                    "Error",
                                    color = Color.Red,
                                    modifier = Modifier.align(Alignment.Center)
                                )

                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RenderParkInfoScreen(
    result: AppResult<List<ParkInfo>>,
    onParkSelected: (Int) -> Unit,
    onFavoriteToggle: (Int) -> Unit
) {
    val favColor = ChipDefaults.chipColors(
        backgroundColor = Color(0xFF1A1C1E),
        contentColor = Color.White
    )
    val listState = rememberScalingLazyListState()

    Scaffold(
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        when (result) {
            is AppResult.Success -> {
                ScalingLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    autoCentering = AutoCenteringParams(itemIndex = 0)
                ) {
                    item { ListHeader { Text("Parques") } }

                    items(result.data, key = { it.id ?: 0 }) { park ->
                        ParkChip(park, onParkSelected, onFavoriteToggle, favColor)
                    }
                }
            }

            is AppResult.Loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            else -> {}
        }
    }
}

@Composable
fun ParkChip(
    park: ParkInfo,
    onParkSelected: (Int) -> Unit,
    onFavoriteToggle: (Int) -> Unit,
    favColor: ChipColors
) {
    Chip(
        onClick = { onParkSelected(park.id ?: 0) },
        label = {
            AutoSizeText(text = park.name)
        },
        secondaryLabel = {
            Text(
                text = park.country,
                color = Color.Green
            )
        },
        icon = {
            FavoriteIconArea(
                isFavorite = park.isFavorite,
                onToggle = { onFavoriteToggle(park.id ?: 0) }
            )
        },
        colors = if (park.isFavorite) favColor else ChipDefaults.secondaryChipColors(),
        modifier = Modifier
            .fillMaxWidth()
            .height(chipHeight)
    )
}

@Composable
fun ParkDetailScreen(
    park: com.jmr.coasterappwatch.domain.model.Park,
    onRideFavorite: (Int) -> Unit
) {
    val listState = rememberScalingLazyListState()
    val allRides = remember(park) {
        val ridesInLands = park.landList.flatMap { it.rideList }
        (ridesInLands + park.rideList).distinctBy { it.id }
    }

    Scaffold(
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            autoCentering = AutoCenteringParams(itemIndex = 0)
        ) {
            item { ListHeader { Text(park.name, textAlign = TextAlign.Center) } }

            items(allRides, key = { it.id }) { ride ->
                RideChip(
                    ride = ride,
                    onFavoriteToggle = { onRideFavorite(ride.id) }
                )
            }
        }
    }
}

@Composable
fun RideChip(ride: Ride, onFavoriteToggle: () -> Unit) {
    val isClosed = !ride.isOpen || (ride.waitTime == null)
    val favColor = ChipDefaults.chipColors(
        backgroundColor = Color(0xFF1A1C1E),
        contentColor = Color.White
    )

    Chip(
        onClick = { /* Navegar a info de la atracción si fuera necesario */ },
        label = {
            AutoSizeText(text = ride.name)
        },
        secondaryLabel = {
            Text(
                text = if (isClosed) "CLOSED" else "${ride.waitTime} MIN",
                color = if (isClosed) Color.Red else Color.Green
            )
        },
        icon = {
            FavoriteIconArea(
                isFavorite = ride.isFavourite,
                onToggle = onFavoriteToggle
            )
        },
        colors = if (ride.isFavourite) ChipDefaults.secondaryChipColors() else favColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(chipHeight)
    )
}

@Composable
fun FavoriteIconArea(isFavorite: Boolean, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(38.dp)
            .pointerInput(Unit) {
                detectTapGestures { onToggle() }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = if (isFavorite) Color(0xFFE91E63) else Color.Gray,
            modifier = Modifier.size(ButtonDefaults.SmallIconSize)
        )
    }
}

@Composable
fun AutoSizeText(
    text: String,
    maxLines: Int = 1,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = Color.Unspecified
) {
    var fontSizeValue by remember { mutableStateOf(12.sp) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier.drawWithContent { if (readyToDraw) drawContent() },
        textAlign = textAlign,
        color = color,
        softWrap = false,
        maxLines = maxLines,
        overflow = TextOverflow.Clip,
        style = MaterialTheme.typography.body1.copy(fontSize = fontSizeValue),
        onTextLayout = { layoutResult ->
            if (layoutResult.hasVisualOverflow && fontSizeValue > 10.sp) {
                fontSizeValue *= 0.9f
            } else {
                readyToDraw = true
            }
        }
    )
}
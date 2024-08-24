package com.jmr.coasterappwatch.presentation.park

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Park
import com.jmr.coasterappwatch.domain.model.Ride
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.jmr.coasterappwatch.domain.model.Land
import com.jmr.coasterappwatch.presentation.main.MainActivity

@AndroidEntryPoint
class ParkActivity : ComponentActivity() {
    private val parkViewModel: ParkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parkId = intent.getIntExtra("park_info_id", -1)

        setContent {
            ParkScreen(parkViewModel, parkId) { selectedRide ->
                // Lógica cuando se selecciona una atracción
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

@Composable
fun ParkScreen(viewModel: ParkViewModel, parkId: Int, onRideClick: (Ride) -> Unit) {
    val parkResult by viewModel.park.observeAsState()

    LaunchedEffect(parkId) {
        viewModel.requestRideList(parkId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val result = parkResult) {
            is AppResult.Success -> {
                ParkListScreen(park = result.data, onRideClick = onRideClick)
            }

            is AppResult.Error -> {
                Text(text = "Error: ${result.exception.message}")
            }

            is AppResult.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            else -> {
                // Mostrar estado vacío o manejar otros casos
            }
        }
    }
}

@Composable
fun ParkListScreen(park: Park, onRideClick: (Ride) -> Unit) {
    val expandedLands = remember { mutableStateMapOf<Int, Boolean>() }
    val isCategoryExpanded = remember { mutableStateOf(true) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        park.landList?.forEach { land ->
            item {
                LandItem(
                    land = land,
                    isExpanded = expandedLands[land.id] ?: true,
                    onLandClick = {
                        expandedLands[land.id] = !(expandedLands[land.id] ?: true)
                    }
                )
            }

            if (expandedLands[land.id] == true) {
                items(land.rideList.orEmpty()) { ride ->
                    RideListItem(ride = ride, onClick = onRideClick)
                }
            }
        }

        // Si no hay tierras, mostrar solo las atracciones (Rides)
        val rideList = park.rideList.orEmpty()
        if (rideList.isNotEmpty() && park.landList.isNullOrEmpty()) {
            item {
                CategoryHeader(
                    title = "Atracciones",
                    isExpanded = isCategoryExpanded.value,
                    onHeaderClick = {
                        isCategoryExpanded.value = !isCategoryExpanded.value
                    }
                )
            }

            if (isCategoryExpanded.value) {
                items(rideList) { ride ->
                    RideListItem(ride = ride, onClick = onRideClick)
                }
            }
        }
    }
}

@Composable
fun LandItem(land: Land, isExpanded: Boolean, onLandClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLandClick() }
            .padding(8.dp)
    ) {
        Text(
            text = land.name,
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Red
            )
        )
        if (isExpanded) {
            // Flecha hacia arriba/abajo para indicar estado de expansión
        }
    }
}

@Composable
fun RideListItem(ride: Ride, onClick: (Ride) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(ride) }
            .padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = ride.name ?: "-",
            color = if (ride.waitTime == null || ride.waitTime < 0) Color.Red else Color.White,
            modifier = Modifier.weight(1f),
            style = TextStyle(
                fontSize = 10.sp,
                color = Color.Gray
            ),
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 2,
        )
        Text(
            text = if (ride.waitTime == null || ride.waitTime < 0) "CLOSED" else "${ride.waitTime} min",
            color = if (ride.waitTime == null || ride.waitTime < 0) Color.Red else Color.White,
            style = TextStyle(
                fontSize = 10.sp,
                color = Color.Gray
            ),
            minLines = 2,
            maxLines = 2,
        )
    }
}

@Composable
fun CategoryHeader(title: String, isExpanded: Boolean, onHeaderClick: () -> Unit) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHeaderClick() }
            .padding(8.dp),
        style = TextStyle(
            fontSize = 14.sp,
            color = Color.Gray
        ),
        textAlign = TextAlign.Center
    )
    // Flecha hacia arriba/abajo para mostrar el estado de expansión
}
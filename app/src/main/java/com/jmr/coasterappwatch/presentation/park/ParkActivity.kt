package com.jmr.coasterappwatch.presentation.park

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.jmr.coasterappwatch.R
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
                onClickedRide(selectedRide)
            }
        }
    }

    private fun onClickedRide(selectedRide: Ride) {
        // Manejo de clic en atracción
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        goBackToMain()
    }

    private fun goBackToMain() {
        clearSelectedParkInfo()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun clearSelectedParkInfo() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("selected_park_info_id")
        editor.apply()
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
    val expandedLands = remember { mutableStateOf<Map<Int, Boolean>>(emptyMap()) }
    val isCategoryExpanded = remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        park.landList?.forEach { land ->
            item {
                LandItem(
                    land = land,
                    onLandClick = {
                        expandedLands.value = expandedLands.value.toMutableMap().apply {
                            put(land.id, !(expandedLands.value[land.id] ?: false))
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (expandedLands.value[land.id] == true) {
                items(land.rideList.orEmpty()) { ride ->
                    RideItem(ride = ride, onClick = onRideClick)
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        val rideList = park.rideList.orEmpty()
        if (rideList.isNotEmpty() && park.landList.isNullOrEmpty()) {
            if (isCategoryExpanded.value) {
                items(rideList) { ride ->
                    RideItem(ride = ride, onClick = onRideClick)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun LandItem(land: Land, onLandClick: () -> Unit) {
    val fontSizeLandName = 14.sp
    val fontSizeLandPadding = 4.dp
    val fontSizeLandColor =
        Color(ContextCompat.getColor(LocalContext.current, R.color.secondary_text))

    val backgroundColor = Color(ContextCompat.getColor(LocalContext.current, R.color.secondary))
    val backgroundShape = CircleShape
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.Center)
                .background(
                    color = backgroundColor,
                    shape = backgroundShape
                )
                .clickable { onLandClick() }
                .padding(fontSizeLandPadding)
        ) {
            Text(
                text = "--- ${land.name} ---",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                style = TextStyle(
                    fontSize = fontSizeLandName,
                    color = fontSizeLandColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
    }
}

@Composable
fun RideItem(ride: Ride, onClick: (Ride) -> Unit) {
    val fontSizeRideName = 12.sp
    val fontSizeRideTime = 14.sp

    Row(
        modifier = Modifier
            .width(216.dp)
            .clickable { onClick(ride) }
            .padding(20.dp, 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = ride.name ?: "-",
            color =
            if (ride.waitTime == null || ride.waitTime <= 0) Color.Red
            else if (!ride.isFavourite) Color.White
            else Color(
                ContextCompat.getColor(
                    LocalContext.current, R.color.primary
                )
            ),
            modifier = Modifier.weight(1f),
            style = TextStyle(
                fontSize = fontSizeRideName,
                color = Color.Gray
            ),
            textAlign = TextAlign.Center,
            minLines = 1,
            maxLines = 2,
        )
        Text(
            text = if (ride.waitTime == null || ride.waitTime <= 0) "CLOSED" else "${ride.waitTime} min",
            color = if (ride.waitTime == null || ride.waitTime <= 0) Color.Red else if (!ride.isFavourite) Color.White
            else Color(
                ContextCompat.getColor(
                    LocalContext.current, R.color.primary
                )
            ),
            modifier = Modifier.padding(4.dp, 0.dp),
            style = TextStyle(
                fontSize = fontSizeRideTime,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            ),
            minLines = 1,
            maxLines = 1,
        )
    }
}

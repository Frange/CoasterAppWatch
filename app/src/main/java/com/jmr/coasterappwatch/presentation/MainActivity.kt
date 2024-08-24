package com.jmr.coasterappwatch.presentation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import com.jmr.coasterappwatch.domain.base.AppResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

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
                ParkInfoListScreen(viewModel) { parkInfoId ->
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
fun ParkInfoListScreen(viewModel: QueueViewModel, onParkInfoSelected: (Int) -> Unit) {
    val parkInfoListResult by viewModel.parkInfoList.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.requestAllParkList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val result = parkInfoListResult) {
            is AppResult.Success -> {
                LazyColumn {
                    items(result.data) { parkInfo ->
                        parkInfo.id?.let { id ->
                            Text(
                                text = parkInfo.name ?: "-",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onParkInfoSelected(id)
                                    }
                                    .padding(16.dp)
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

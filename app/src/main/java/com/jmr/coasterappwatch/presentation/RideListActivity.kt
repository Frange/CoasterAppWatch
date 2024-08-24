package com.jmr.coasterappwatch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jmr.coasterappwatch.domain.base.AppResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideListActivity : ComponentActivity() {
    private val viewModel: QueueViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val companyId = intent.getIntExtra("park_info_id", -1)

        if (companyId == -1) {
            finish()
            return
        }

        setContent {
            RideListScreen(viewModel)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

@Composable
fun RideListScreen(viewModel: QueueViewModel) {
    val rideListResult by viewModel.rideList.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.requestRideList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val result = rideListResult) {
            is AppResult.Success -> {
                LazyColumn {
                    items(result.data) { company ->
                        company.id?.let { id ->
                            Text(
                                text = company.name ?: "-",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onRideSelected(id)
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

fun onRideSelected(id: Int) {

}

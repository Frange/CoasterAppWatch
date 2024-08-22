package com.jmr.coasterappwatch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

//@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//    private val viewModel: QueueViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContent {
//            val rideList by viewModel.rideList.collectAsState(emptyList())
//            val isRefreshing by viewModel.isRefreshing.collectAsState()
//            val pullRefreshState = rememberPullRefreshState(
//                refreshing = isRefreshing,
//                onRefresh = { viewModel.refreshRides() }
//            )
//
//            Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
//                LazyColumn(modifier = Modifier.fillMaxSize()) {
//                    items(rideList) { ride ->
//                        RideListItem(ride = ride) { selectedRide ->
//                            // Handle ride item click
//                        }
//                    }
//                }
//
//                PullRefreshIndicator(
//                    refreshing = isRefreshing,
//                    state = pullRefreshState,
//                    modifier = Modifier.align(Alignment.TopCenter)
//                )
//            }
//        }
    }
}
package com.jmr.coasterappwatch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jmr.coasterappwatch.domain.base.AppResult
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.foundation.*

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: QueueViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hacer la solicitud inicial
        viewModel.requestCompanyList()

        setContent {
            // Observar la lista de compañías
            val companyListResult by viewModel.companyList.observeAsState()
            val isRefreshing by viewModel.isRefreshing.collectAsState()

            val pullRefreshState = rememberPullRefreshState(
                refreshing = isRefreshing,
                onRefresh = { viewModel.requestCompanyList() } // Volver a hacer la solicitud
            )

            // UI
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                companyListResult?.let { result ->
                    when (result) {
                        is AppResult.Success -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(result.data) { company ->
                                    Text(text = company.name ?: "-") // Ajusta según tu modelo y UI
                                }
                            }
                        }

                        is AppResult.Error -> {
                            Text(text = "Error: $result")
                        }

                        is AppResult.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }

                        is AppResult.Exception -> TODO()
                    }
                }

                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

package com.jmr.coasterappwatch.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.coasterappwatch.data.repository.queue.QueueRepository
import com.jmr.coasterappwatch.data.store.FavoriteManager
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.ParkInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val queueRepository: QueueRepository,
    private val favoriteManager: FavoriteManager
) : ViewModel() {

    // Lista de parques reactiva
    val parkInfoList: StateFlow<AppResult<List<ParkInfo>>> = queueRepository
        .requestAllParkList()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppResult.Loading()
        )

    val lastParkId: StateFlow<String?> = favoriteManager.lastParkId
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    fun toggleParkFavorite(parkId: Int) {
        viewModelScope.launch {
            favoriteManager.toggleParkFavorite(parkId.toString())
        }
    }

    fun saveLastPark(parkId: Int) {
        viewModelScope.launch {
            favoriteManager.saveLastPark(parkId.toString())
        }
    }

    fun clearLastPark() {
        viewModelScope.launch { favoriteManager.clearLastPark() }
    }
}
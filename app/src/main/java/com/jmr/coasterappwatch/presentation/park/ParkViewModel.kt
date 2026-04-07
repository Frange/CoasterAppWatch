package com.jmr.coasterappwatch.presentation.park

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.coasterappwatch.data.store.FavoriteManager
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Park
import com.jmr.coasterappwatch.data.repository.queue.QueueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParkViewModel @Inject constructor(
    private val repository: QueueRepository,
    private val favoriteManager: FavoriteManager
) : ViewModel() {

    private val _parkState = MutableStateFlow<AppResult<Park>>(AppResult.Loading())
    val parkState: StateFlow<AppResult<Park>> = _parkState.asStateFlow()

    fun loadParkDetails(parkId: Int) {
        viewModelScope.launch {
            repository.requestParkList(parkId)
                .collect { result ->
                    _parkState.value = result
                }
        }
    }

    fun toggleRideFavorite(rideId: Int) {
        viewModelScope.launch {
            favoriteManager.toggleRideFavorite(rideId.toString())
        }
    }

}
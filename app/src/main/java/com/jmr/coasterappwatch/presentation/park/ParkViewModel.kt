package com.jmr.coasterappwatch.presentation.park

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Park
import com.jmr.coasterappwatch.data.repository.queue.QueueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParkViewModel @Inject constructor(
    private val repository: QueueRepository
) : ViewModel() {

    private val _parkState = MutableStateFlow<AppResult<Park>>(AppResult.Loading())
    val parkState: StateFlow<AppResult<Park>> = _parkState.asStateFlow()

    fun loadParkDetails(parkId: Int) {
        viewModelScope.launch {
            _parkState.value = AppResult.Loading()

            repository.requestParkList(parkId)
                .catch { exception ->
                    _parkState.value = AppResult.Error(exception)
                }
                .collect { result: AppResult<Park> ->
                    _parkState.value = result
                }
        }
    }
}
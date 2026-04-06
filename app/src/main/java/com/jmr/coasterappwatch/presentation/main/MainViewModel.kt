package com.jmr.coasterappwatch.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.coasterappwatch.data.repository.queue.QueueRepository
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.ParkInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val queueRepository: QueueRepository
) : ViewModel() {

    private val _parkInfoList = MutableStateFlow<AppResult<List<ParkInfo>>>(AppResult.Loading())
    val parkInfoList: StateFlow<AppResult<List<ParkInfo>>> = _parkInfoList.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun requestAllParkList() {
        viewModelScope.launch {
            _isRefreshing.value = true
            queueRepository.requestAllParkList()
                .catch { exception ->
                    _isRefreshing.value = false
                    _parkInfoList.value = AppResult.Error(exception)
                }
                .collect { result ->
                    _isRefreshing.value = false
                    _parkInfoList.value = result
                }
        }
    }
}
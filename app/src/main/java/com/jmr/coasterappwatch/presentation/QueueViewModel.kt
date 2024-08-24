package com.jmr.coasterappwatch.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.coasterappwatch.data.repository.queue.QueueRepository
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.ParkInfo
import com.jmr.coasterappwatch.domain.model.Ride
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val queueRepository: QueueRepository
) : ViewModel() {

    private val _parkInfoList = MutableLiveData<AppResult<List<ParkInfo>>>()
    val parkInfoList: LiveData<AppResult<List<ParkInfo>>> = _parkInfoList

    private val _rideList = MutableLiveData<AppResult<List<Ride>>>()
    val rideList: LiveData<AppResult<List<Ride>>> = _rideList

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun requestAllParkList() {
        _isRefreshing.value = true
        viewModelScope.launch {
            queueRepository.requestAllParkList()
                .catch { exception ->
                    _isRefreshing.value = false
                    _parkInfoList.postValue(AppResult.Error(exception))
                }
                .collect { result ->
                    _isRefreshing.value = false
                    _parkInfoList.postValue(result)
                }
        }
    }

    fun requestRideList() {
        _isRefreshing.value = true
        viewModelScope.launch {
            queueRepository.requestRideList()
                .catch { exception ->
                    _isRefreshing.value = false
                    _rideList.postValue(AppResult.Error(exception))
                }
                .collect { result ->
                    _isRefreshing.value = false
                    _rideList.postValue(result)
                }
        }
    }
}
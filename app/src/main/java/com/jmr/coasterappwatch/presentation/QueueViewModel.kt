package com.jmr.coasterappwatch.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.coasterappwatch.data.repository.queue.QueueRepository
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Coaster
import com.jmr.coasterappwatch.domain.model.Company
import com.jmr.coasterappwatch.domain.model.Park
import com.jmr.coasterappwatch.domain.model.Ride
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val queueRepository: QueueRepository
) : ViewModel() {

    private val _companyList = MutableLiveData<AppResult<List<Company>>>()
    val companyList: LiveData<AppResult<List<Company>>> = _companyList

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun requestCompanyList() {
        _isRefreshing.value = true

        viewModelScope.launch {
            queueRepository.requestCompanyList()
                .catch {
                    _isRefreshing.value = false
                    val exception = it
                    Log.v("Exception", "Exception", exception)
                }.collect {
                    _isRefreshing.value = false
                    _companyList.postValue(it)
                }
        }
    }

    private fun loadRides() {
        viewModelScope.launch {
            queueRepository.requestCoaster(0, sortedByTime = false)
                .collect { result ->
                    when (result) {
                        is AppResult.Success -> {
//                            if (result.data.rideList != null) {
//                                _rideList.value = result.data.rideList!!
//                            }
                        }

                        is AppResult.Error -> {
                            // Handle error
                        }

                        is AppResult.Loading -> {
                            _isRefreshing.value = true
                        }

                        is AppResult.Exception -> {
                            // Handle exception
                        }
                    }
                }
        }
    }

    fun refreshRides() {
        viewModelScope.launch {
            _isRefreshing.value = true
            queueRepository.requestCoaster(0, sortedByTime = false)
                .collect { result ->
                    when (result) {
                        is AppResult.Success -> {
//                            if (result.data.rideList != null) {
//                                _rideList.value = result.data.rideList!!
//                            }
                        }

                        is AppResult.Error -> {
                            // Handle error
                        }

                        is AppResult.Loading -> {
                            _isRefreshing.value = true
                        }

                        is AppResult.Exception -> {
                            // Handle exception
                        }
                    }
                }
            _isRefreshing.value = false
        }
    }

}

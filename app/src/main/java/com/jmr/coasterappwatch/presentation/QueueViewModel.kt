package com.jmr.coasterappwatch.presentation

import android.util.Log
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
//    private val requestCompanyListUseCase: RequestCompanyListUseCase,
//    private val requestCoasterUseCase: RequestCoasterUseCase,
//    private val requestParkListUseCase: RequestParkListUseCase,
    private val queueRepository: QueueRepository
) : ViewModel() {


    private val companyList = MutableLiveData<AppResult<List<Company>>>()
    private val parkList = MutableLiveData<AppResult<List<Park>>>()
    private val coaster = MutableLiveData<AppResult<Coaster>>()
//    private val _rideList = MutableStateFlow<List<Ride>>(emptyList())

//    val rideList: StateFlow<List<Ride>> = _rideList.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
//        loadRides()
//        requestCompanyList()
    }

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
                    companyList.postValue(it)
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

    fun getCompanyList(): MutableLiveData<AppResult<List<Company>>> {
        return companyList
    }

    fun getParkList(): MutableLiveData<AppResult<List<Park>>> {
        return parkList
    }

    fun getCoaster(): MutableLiveData<AppResult<Coaster>> {
        return coaster
    }
}

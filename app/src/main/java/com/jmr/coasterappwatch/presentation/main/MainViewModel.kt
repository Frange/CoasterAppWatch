package com.jmr.coasterappwatch.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.coasterappwatch.data.repository.queue.QueueRepository
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Park
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

    private val _parkInfoList = MutableLiveData<AppResult<List<ParkInfo>>>()
    val parkInfoList: LiveData<AppResult<List<ParkInfo>>> = _parkInfoList

    private val _parkList = MutableLiveData<AppResult<List<Park>>>()
    val parkList: LiveData<AppResult<List<Park>>> = _parkList

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
}
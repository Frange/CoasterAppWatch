package com.jmr.coasterappwatch.presentation.park

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.coasterappwatch.data.repository.queue.QueueRepository
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Park
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ParkViewModel @Inject constructor(
    private val queueRepository: QueueRepository
) : ViewModel() {

    private val _park = MutableLiveData<AppResult<Park>>()
    val park: LiveData<AppResult<Park>> = _park

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun requestRideList(id: Int) {
        _isRefreshing.value = true
        viewModelScope.launch {
            queueRepository.requestParkList(id = id)
                .catch { exception ->
                    _isRefreshing.value = false
                    _park.postValue(AppResult.Error(exception))
                }
                .collect { result ->
                    _isRefreshing.value = false
                    _park.postValue(result)
                }
        }
    }
}
package com.jmr.coasterappwatch.data.api.model.ride

import com.jmr.coasterappwatch.data.repository.QueueRepository
import com.jmr.coasterappwatch.domain.model.AppResult
import com.jmr.coasterappwatch.domain.model.Ride
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform


class RideModelImpl @Inject constructor(
    private val repository: QueueRepository
) : RideModel {

    override fun get(): Flow<AppResult<List<Ride>>> {
        return repository.requestRideList().transform { result ->
            emit(result)
        }
    }

}
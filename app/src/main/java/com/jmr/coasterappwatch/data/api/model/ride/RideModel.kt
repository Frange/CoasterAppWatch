package com.jmr.coasterappwatch.data.api.model.ride

import com.jmr.coasterappwatch.domain.model.AppResult
import com.jmr.coasterappwatch.domain.model.Ride
import kotlinx.coroutines.flow.Flow


interface RideModel {

    fun get(): Flow<AppResult<List<Ride>>>

}
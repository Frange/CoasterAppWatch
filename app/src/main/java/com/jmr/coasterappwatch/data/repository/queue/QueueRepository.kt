package com.jmr.coasterappwatch.data.repository.queue

import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Company
import com.jmr.coasterappwatch.domain.model.Park
import com.jmr.coasterappwatch.domain.model.ParkInfo
import com.jmr.coasterappwatch.domain.model.Ride
import kotlinx.coroutines.flow.Flow

interface QueueRepository {

    fun requestAllParkList(): Flow<AppResult<List<ParkInfo>>>

    fun requestParkInfoList(position: Int): Flow<AppResult<List<ParkInfo>>>
    fun requestParkList(position: Int): Flow<AppResult<Park>>
    fun requestRideList(): Flow<AppResult<List<Ride>>>

    fun getCurrentCompanyList(): List<Company>
    fun getCurrentCoasterList(): Park
    fun getCurrentParkList(): List<ParkInfo>


}

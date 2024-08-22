package com.jmr.coasterappwatch.data.repository.queue

import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Coaster
import com.jmr.coasterappwatch.domain.model.Company
import com.jmr.coasterappwatch.domain.model.Park
import com.jmr.coasterappwatch.domain.model.Ride
import kotlinx.coroutines.flow.Flow

interface QueueRepository {

    fun requestCompanyList(): Flow<AppResult<List<Company>>>
    fun requestParkList(position: Int): Flow<AppResult<List<Park>>>
    fun requestCoaster(position: Int, sortedByTime: Boolean): Flow<AppResult<Coaster>>
    fun requestRideList(): Flow<AppResult<List<Ride>>>

    fun getCurrentCompanyList(): List<Company>
    fun getCurrentCoasterList(): Coaster
    fun getCurrentParkList(): List<Park>

}

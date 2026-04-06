package com.jmr.coasterappwatch.data.repository.queue

import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Company
import com.jmr.coasterappwatch.domain.model.Park
import com.jmr.coasterappwatch.domain.model.ParkInfo
import kotlinx.coroutines.flow.Flow

interface QueueRepository {
    fun requestAllParkList(): Flow<AppResult<List<ParkInfo>>>
    fun requestParkList(id: Int): Flow<AppResult<Park>>
}

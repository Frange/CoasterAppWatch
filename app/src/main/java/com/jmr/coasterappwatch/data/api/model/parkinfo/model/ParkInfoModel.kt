package com.jmr.coasterappwatch.data.api.model.parkinfo.model

import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.ParkInfo
import kotlinx.coroutines.flow.Flow

interface ParkInfoModel {

    fun get(): Flow<AppResult<List<ParkInfo>>>

    fun get(id: Int): Flow<AppResult<List<ParkInfo>>>

}

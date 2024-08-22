package com.jmr.coasterappwatch.data.api.model.park

import com.jmr.coasterappwatch.domain.model.AppResult
import com.jmr.coasterappwatch.domain.model.Park
import kotlinx.coroutines.flow.Flow

interface ParkModel {

    fun get(id: Int): Flow<AppResult<List<Park>>>

}

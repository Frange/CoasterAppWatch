package com.jmr.coasterappwatch.data.api.model.coaster

import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Coaster
import kotlinx.coroutines.flow.Flow

interface CoasterModel {

    fun get(position: Int, sortedByTime: Boolean): Flow<AppResult<Coaster>>

}

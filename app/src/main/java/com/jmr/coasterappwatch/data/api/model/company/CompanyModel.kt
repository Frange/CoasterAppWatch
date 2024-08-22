package com.jmr.coasterappwatch.data.api.model.company

import com.jmr.coasterappwatch.domain.model.AppResult
import com.jmr.coasterappwatch.domain.model.Company
import kotlinx.coroutines.flow.Flow

interface CompanyModel {

    fun get(): Flow<AppResult<List<Company>>>
    fun get(id: Int): Flow<AppResult<List<Company>>>

}
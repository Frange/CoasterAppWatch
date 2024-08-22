package com.jmr.coasterappwatch.data.api.model.coaster

import com.jmr.coasterappwatch.data.repository.QueueRepository
import com.jmr.coasterappwatch.domain.model.AppResult
import com.jmr.coasterappwatch.domain.model.Coaster
import kotlinx.coroutines.flow.*

class CoasterModelImpl @Inject constructor(
    private val repository: QueueRepository
) : CoasterModel {

    override fun get(position: Int, sortedByTime: Boolean): Flow<AppResult<Coaster>> {
        return repository.requestCoaster(position, sortedByTime).transform { result ->
            emit(result)
        }
    }

}
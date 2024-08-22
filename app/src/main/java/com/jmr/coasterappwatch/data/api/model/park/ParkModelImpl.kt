package com.jmr.coasterappwatch.data.api.model.park

import com.jmr.coasterappwatch.data.repository.QueueRepository
import com.jmr.coasterappwatch.domain.model.AppResult
import com.jmr.coasterappwatch.domain.model.Park
import kotlinx.coroutines.flow.*

class ParkModelImpl @Inject constructor(
    private val repository: QueueRepository
) : ParkModel {

    override fun get(position: Int): Flow<AppResult<List<Park>>> {
        return repository.requestParkList(position).transform { result ->
            emit(result)
        }
    }
}
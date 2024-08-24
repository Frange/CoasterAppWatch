package com.jmr.coasterappwatch.data.api.model.parkinfo.model

import com.jmr.coasterappwatch.data.repository.queue.QueueRepository
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.ParkInfo
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ParkInfoModelImpl @Inject constructor(
    private val repository: QueueRepository
) : ParkInfoModel {

    override fun get(): Flow<AppResult<List<ParkInfo>>> {
        return repository.requestAllParkList().transform { result ->
            emit(result)
        }
    }

    override fun get(id: Int): Flow<AppResult<List<ParkInfo>>> {
        return repository.requestParkInfoList(id).transform { result ->
            emit(result)
        }
    }
}
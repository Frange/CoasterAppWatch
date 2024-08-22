package com.jmr.coasterappwatch.data.api.model.park

import com.jmr.coasterappwatch.data.repository.queue.QueueRepository
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Park
import kotlinx.coroutines.flow.*
import javax.inject.Inject

//class ParkModelImpl @Inject constructor(
//    private val repository: QueueRepository
//) : ParkModel {
//
//    override fun get(position: Int): Flow<AppResult<List<Park>>> {
//        return repository.requestParkList(position).transform { result ->
//            emit(result)
//        }
//    }
//}
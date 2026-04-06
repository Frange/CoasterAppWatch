package com.jmr.coasterappwatch.data.api.model.park // Ajusta según tu paquete real

import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.model.Park
import com.jmr.coasterappwatch.data.repository.queue.QueueRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ParkModelImpl @Inject constructor(
    private val repository: QueueRepository
) : ParkModel {

    override fun get(position: Int): Flow<AppResult<Park>> {
        return repository.requestParkList(position)
    }
}
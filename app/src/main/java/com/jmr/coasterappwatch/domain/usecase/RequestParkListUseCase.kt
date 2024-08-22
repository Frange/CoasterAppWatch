package com.jmr.coasterappwatch.domain.usecase

import com.jmr.coasterappwatch.data.api.model.park.ParkModel
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.base.FlowUseCaseWithParams
import com.jmr.coasterappwatch.domain.model.Park
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestParkListUseCase @Inject constructor(
    private val parkModel: ParkModel
) : FlowUseCaseWithParams<RequestParkListUseCase.Parameters, AppResult<List<Park>>>() {

    public override fun execute(parameters: Parameters): Flow<AppResult<List<Park>>> {
        return parkModel.get(parameters.id)
    }

    class Parameters(
        val id: Int
    )
}
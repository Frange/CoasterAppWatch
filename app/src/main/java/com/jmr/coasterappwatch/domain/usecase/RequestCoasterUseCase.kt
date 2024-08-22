package com.jmr.coasterappwatch.domain.usecase

import com.jmr.coasterappwatch.data.api.model.coaster.CoasterModel
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.base.FlowUseCaseWithParams
import com.jmr.coasterappwatch.domain.model.Coaster
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestCoasterUseCase @Inject constructor(
    private val coasterModel: CoasterModel
) : FlowUseCaseWithParams<RequestCoasterUseCase.Parameters, AppResult<Coaster>>() {

    public override fun execute(parameters: Parameters): Flow<AppResult<Coaster>> {
        return coasterModel.get(parameters.id, parameters.sortedByTime)
    }

    class Parameters(
        val id: Int,
        val sortedByTime: Boolean
    )
}
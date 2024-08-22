package com.jmr.coasterappwatch.domain.usecase

import com.jmr.coasterappwatch.data.api.model.company.CompanyModel
import com.jmr.coasterappwatch.domain.base.AppResult
import com.jmr.coasterappwatch.domain.base.FlowUseCase
import com.jmr.coasterappwatch.domain.model.Company
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RequestCompanyListUseCase @Inject constructor(
    private val companyModel: CompanyModel
) : FlowUseCase<AppResult<List<Company>>>() {

    public override fun execute(): Flow<AppResult<List<Company>>> {
        return companyModel.get()
    }
}
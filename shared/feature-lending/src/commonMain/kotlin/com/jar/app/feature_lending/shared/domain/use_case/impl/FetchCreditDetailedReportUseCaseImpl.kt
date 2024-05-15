package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchCreditDetailedReportUseCase

internal class FetchCreditDetailedReportUseCaseImpl(
    private val lendingRepository: LendingRepository
) : FetchCreditDetailedReportUseCase {
    override suspend fun fetchCreditDetailedReport(type:String) = lendingRepository.fetchCreditDetailedReportData(type)
}
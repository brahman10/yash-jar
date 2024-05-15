package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchCreditReportSummaryDataUseCase

internal class FetchCreditReportSummaryDataUseCaseImpl(
    private val lendingRepository: LendingRepository
) : FetchCreditReportSummaryDataUseCase {
    override suspend fun fetchCreditReportSummary() = lendingRepository.fetchCreditReportSummary()
}
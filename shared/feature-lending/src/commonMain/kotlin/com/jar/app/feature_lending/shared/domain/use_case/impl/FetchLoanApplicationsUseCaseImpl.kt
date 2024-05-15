package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanApplicationsUseCase

internal class FetchLoanApplicationsUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
): FetchLoanApplicationsUseCase {

    override suspend fun fetchLoanApplications() = lendingRepository.fetchLoanApplications()

}
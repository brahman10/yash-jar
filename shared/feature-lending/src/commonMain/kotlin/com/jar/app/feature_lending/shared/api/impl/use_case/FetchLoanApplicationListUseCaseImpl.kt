package com.jar.app.feature_lending.shared.api.impl.use_case

import com.jar.app.feature_lending.shared.api.usecase.FetchLoanApplicationListUseCase
import com.jar.app.feature_lending.shared.data.repository.LendingRepository

internal class FetchLoanApplicationListUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : FetchLoanApplicationListUseCase {

    override suspend fun fetchLoanApplicationList() = lendingRepository.fetchLoanApplicationList()

}
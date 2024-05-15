package com.jar.app.feature_lending.shared.api.impl.use_case

import com.jar.app.feature_lending.shared.api.usecase.FetchLoanProgressStatusV2UseCase
import com.jar.app.feature_lending.shared.data.repository.LendingRepository

internal class FetchLoanProgressStatusV2UseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : FetchLoanProgressStatusV2UseCase {
    override suspend fun getLoanProgressStatus(loanId: String) =
        lendingRepository.getLoanProgressStatus(loanId)
}
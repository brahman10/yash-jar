package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase

internal class FetchLoanDetailsV2UseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : FetchLoanDetailsV2UseCase {

    override suspend fun getLoanDetails(loanId: String, checkPoint: String?) =
        lendingRepository.getLoanDetails(loanId, checkPoint)

}
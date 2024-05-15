package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchRepaymentDetailsUseCase

internal class FetchRepaymentDetailsUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : FetchRepaymentDetailsUseCase {

    override suspend fun getRepaymentDetails(loanId: String) = lendingRepository.getRepaymentDetails(loanId)

}
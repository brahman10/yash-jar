package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchLendingAgreementUseCase

internal class FetchLendingAgreementUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
): FetchLendingAgreementUseCase {

    override suspend fun fetchLendingAgreement(loanId: String) = lendingRepository.fetchLendingAgreement(loanId)

}
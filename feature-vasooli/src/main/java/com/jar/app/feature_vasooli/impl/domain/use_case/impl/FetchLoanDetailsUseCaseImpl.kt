package com.jar.app.feature_vasooli.impl.domain.use_case.impl

import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchLoanDetailsUseCase

internal class FetchLoanDetailsUseCaseImpl constructor(
    private val vasooliRepository: VasooliRepository
): FetchLoanDetailsUseCase {

    override suspend fun fetchLoanDetails(loanId: String) =
        vasooliRepository.fetchLoanDetails(loanId)

}
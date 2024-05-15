package com.jar.app.feature_vasooli.impl.domain.use_case.impl

import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchRepaymentHistoryUseCase

internal class FetchRepaymentHistoryUseCaseImpl constructor(
    private val vasooliRepository: VasooliRepository
): FetchRepaymentHistoryUseCase {

    override suspend fun fetchRepaymentHistory(loanId: String) =
        vasooliRepository.fetchRepaymentHistory(loanId)

}
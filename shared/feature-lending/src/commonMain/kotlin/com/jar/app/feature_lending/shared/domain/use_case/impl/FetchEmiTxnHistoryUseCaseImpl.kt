package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchEmiTxnHistoryUseCase

internal class FetchEmiTxnHistoryUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : FetchEmiTxnHistoryUseCase {

    override suspend fun getEmiTxnHistory(loanInd: String, txnType: String) = lendingRepository.getEmiTxnHistory(loanInd, txnType)

}
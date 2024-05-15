package com.jar.app.feature_vasooli.impl.domain.use_case.impl

import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchLoansListUseCase

internal class FetchLoansListUseCaseImpl constructor(
    private val vasooliRepository: VasooliRepository
): FetchLoansListUseCase {

    override suspend fun fetchLoansList() = vasooliRepository.fetchLoansList()

}
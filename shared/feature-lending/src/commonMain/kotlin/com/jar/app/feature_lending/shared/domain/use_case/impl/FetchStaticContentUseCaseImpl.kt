package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase

internal class FetchStaticContentUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : FetchStaticContentUseCase {

    override suspend fun fetchLendingStaticContent(loanId: String?, staticContentType: String) = lendingRepository.fetchLendingStaticContent(loanId, staticContentType)

}
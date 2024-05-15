package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchLendingFaqUseCase

internal class FetchLendingFaqUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
): FetchLendingFaqUseCase {

    override suspend fun fetchLendingFaq(contentType: String) = lendingRepository.fetchLendingFaq(contentType)

}
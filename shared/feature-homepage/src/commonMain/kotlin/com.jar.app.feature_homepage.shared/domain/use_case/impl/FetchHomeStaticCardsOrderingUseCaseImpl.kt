package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeStaticCardsOrderingUseCase

internal class FetchHomeStaticCardsOrderingUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchHomeStaticCardsOrderingUseCase {
    override suspend fun fetchHomeStaticCardsOrdering() =
        homeRepository.fetchHomeStaticCardsOrdering()
}
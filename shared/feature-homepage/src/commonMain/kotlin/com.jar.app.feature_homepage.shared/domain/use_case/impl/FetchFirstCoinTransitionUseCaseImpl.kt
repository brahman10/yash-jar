package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinTransitionUseCase

internal class FetchFirstCoinTransitionUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchFirstCoinTransitionUseCase {

    override suspend fun fetchFirstCoinTransitionPageData() =
        homeRepository.fetchFirstCoinTransitionPageData()
}
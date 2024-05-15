package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchRoundOffCardDataUseCase

internal class FetchRoundOffCardDataUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchRoundOffCardDataUseCase {

    override suspend fun fetchRoundOffCardData() = homeRepository.fetchRoundOffCardData()
}
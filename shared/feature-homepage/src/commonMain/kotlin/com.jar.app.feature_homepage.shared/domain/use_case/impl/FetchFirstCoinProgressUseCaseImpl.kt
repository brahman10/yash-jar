package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinProgressUseCase

internal class FetchFirstCoinProgressUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchFirstCoinProgressUseCase {
    override suspend fun fetchFirstCoinProgress() =
        homeRepository.fetchFirstCoinProgressData()
}
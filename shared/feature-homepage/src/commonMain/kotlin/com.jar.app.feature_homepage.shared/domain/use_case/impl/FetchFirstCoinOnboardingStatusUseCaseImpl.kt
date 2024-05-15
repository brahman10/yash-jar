package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinOnboardingStatusUseCase

internal class FetchFirstCoinOnboardingStatusUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchFirstCoinOnboardingStatusUseCase {

    override suspend fun sendFirstCoinOnboardingStatus() =
        homeRepository.sendFirstCoinOnboardingStatus()
}
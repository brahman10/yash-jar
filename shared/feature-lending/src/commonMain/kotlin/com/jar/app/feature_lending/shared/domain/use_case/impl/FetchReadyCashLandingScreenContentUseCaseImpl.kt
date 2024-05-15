package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchReadyCashLandingScreenContentUseCase
import com.jar.app.feature_lending.shared.util.LendingConstants

internal class FetchReadyCashLandingScreenContentUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : FetchReadyCashLandingScreenContentUseCase {
    override suspend fun fetchReadyCashLandingScreenContent() =
        lendingRepository.fetchReadyCashLandingScreenData(LendingConstants.StaticContentType.LANDING_SCREEN)
}
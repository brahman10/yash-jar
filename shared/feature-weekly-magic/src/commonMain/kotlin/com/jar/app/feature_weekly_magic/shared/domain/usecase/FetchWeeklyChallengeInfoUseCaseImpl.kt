package com.jar.app.feature_weekly_magic.shared.domain.usecase

import com.jar.app.feature_weekly_magic.shared.domain.repository.WeeklyChallengeRepositoryInternal

internal class FetchWeeklyChallengeInfoUseCaseImpl constructor(private val weeklyMagicRepositoryInternal: WeeklyChallengeRepositoryInternal) :
    FetchWeeklyChallengeInfoUseCase {

    override suspend fun fetchWeeklyChallengeInfo() =
        weeklyMagicRepositoryInternal.fetchWeeklyChallengeInfo()
}
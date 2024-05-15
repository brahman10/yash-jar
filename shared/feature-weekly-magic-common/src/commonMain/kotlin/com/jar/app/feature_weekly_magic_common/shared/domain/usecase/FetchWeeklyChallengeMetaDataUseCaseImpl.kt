package com.jar.app.feature_weekly_magic_common.shared.domain.usecase

import com.jar.app.feature_weekly_magic_common.shared.domain.repository.WeeklyChallengeRepositoryExternal

internal class FetchWeeklyChallengeMetaDataUseCaseImpl constructor(private val weeklyMagicRepositoryExternal: WeeklyChallengeRepositoryExternal) :
    FetchWeeklyChallengeMetaDataUseCase {

    override suspend fun fetchWeeklyChallengeMetaData(includeView: Boolean) =
        weeklyMagicRepositoryExternal.fetchWeeklyChallengesMetaData(includeView)
}
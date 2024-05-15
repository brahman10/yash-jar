package com.jar.app.feature_weekly_magic_common.shared.domain.usecase

import com.jar.app.feature_weekly_magic_common.shared.domain.repository.WeeklyChallengeRepositoryExternal

internal class FetchWeeklyChallengeDetailUseCaseImpl constructor(private val weeklyChallengeRepositoryExternal: WeeklyChallengeRepositoryExternal) :
    FetchWeeklyChallengeDetailUseCase {

    override suspend fun fetchWeeklyChallengeDetailForToday() =
        weeklyChallengeRepositoryExternal.fetchWeeklyChallengeDetail()

    override suspend fun fetchWeeklyChallengeDetailById(challengeId: String) =
        weeklyChallengeRepositoryExternal.fetchWeeklyChallengeDetail(challengeId)
}
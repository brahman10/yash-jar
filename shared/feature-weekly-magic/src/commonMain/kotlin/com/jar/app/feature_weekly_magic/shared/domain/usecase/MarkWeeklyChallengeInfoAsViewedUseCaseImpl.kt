package com.jar.app.feature_weekly_magic.shared.domain.usecase

import com.jar.app.feature_weekly_magic.shared.domain.repository.WeeklyChallengeRepositoryInternal

internal class MarkWeeklyChallengeInfoAsViewedUseCaseImpl constructor(private val weeklyMagicRepositoryInternal: WeeklyChallengeRepositoryInternal) :
    MarkWeeklyChallengeInfoAsViewedUseCase {

    override suspend fun markWeeklyChallengeInfoAsViewed(challengeId: String) =
        weeklyMagicRepositoryInternal.markWeeklyChallengeAsViewed(challengeId)

}
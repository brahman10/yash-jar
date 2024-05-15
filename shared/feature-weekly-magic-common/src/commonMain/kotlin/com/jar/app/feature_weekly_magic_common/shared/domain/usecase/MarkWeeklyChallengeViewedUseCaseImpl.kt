package com.jar.app.feature_weekly_magic_common.shared.domain.usecase

import com.jar.app.feature_weekly_magic_common.shared.domain.repository.WeeklyChallengeRepositoryExternal

internal class MarkWeeklyChallengeViewedUseCaseImpl constructor(private val weeklyMagicRepositoryInternal: WeeklyChallengeRepositoryExternal) :
    MarkWeeklyChallengeViewedUseCase {

    override suspend fun markCurrentWeeklyChallengeViewed(challengeId: String) =
        weeklyMagicRepositoryInternal.markCurrentWeeklyChallengeAsViewed(challengeId)

    override suspend fun markPreviousWeeklyChallengeViewed(challengeId: String) =
        weeklyMagicRepositoryInternal.markPreviousWeeklyChallengeAsViewed(challengeId)

    override suspend fun markPreviousWeeklyChallengeStoryViewed(challengeId: String) =
        weeklyMagicRepositoryInternal.markPreviousWeeklyChallengeStoryAsViewed(challengeId)
}
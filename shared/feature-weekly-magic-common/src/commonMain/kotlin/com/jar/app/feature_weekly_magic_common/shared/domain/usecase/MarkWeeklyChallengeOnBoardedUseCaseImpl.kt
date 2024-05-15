package com.jar.app.feature_weekly_magic_common.shared.domain.usecase

import com.jar.app.feature_weekly_magic_common.shared.domain.repository.WeeklyChallengeRepositoryExternal

internal class MarkWeeklyChallengeOnBoardedUseCaseImpl constructor(private val weeklyMagicRepositoryExternal: WeeklyChallengeRepositoryExternal) :
    MarkWeeklyChallengeOnBoardedUseCase {

    override suspend fun markWeeklyChallengeOnBoarded() =
        weeklyMagicRepositoryExternal.markWeeklyChallengeOnBoarded()
}
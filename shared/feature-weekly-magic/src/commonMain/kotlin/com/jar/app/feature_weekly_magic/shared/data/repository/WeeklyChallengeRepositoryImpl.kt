package com.jar.app.feature_weekly_magic.shared.data.repository

import com.jar.app.feature_weekly_magic.shared.data.network.WeeklyChallengeDataSource
import com.jar.app.feature_weekly_magic.shared.domain.repository.WeeklyChallengeRepositoryInternal

internal class WeeklyChallengeRepositoryImpl constructor(
    private val weeklyMagicDataSource: WeeklyChallengeDataSource
) : WeeklyChallengeRepositoryInternal {


    override suspend fun fetchWeeklyChallengeInfo() = getFlowResult {
        weeklyMagicDataSource.fetchWeeklyChallengeInfo()
    }

    override suspend fun markWeeklyChallengeAsViewed(challengeId: String) = getFlowResult {
        weeklyMagicDataSource.markWeeklyChallengeInfoAsViewed(challengeId)
    }

}
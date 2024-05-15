package com.jar.app.feature_weekly_magic_common.shared.data.repository

import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_weekly_magic_common.shared.data.network.WeeklyChallengeDataSource
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData
import com.jar.app.feature_weekly_magic_common.shared.domain.repository.WeeklyChallengeRepositoryExternal
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import kotlinx.coroutines.flow.Flow

internal class WeeklyChallengeRepositoryImpl constructor(
    private val weeklyMagicDataSource: WeeklyChallengeDataSource,
    private val prefsApi: PrefsApi,
    private val serializer: Serializer
) : WeeklyChallengeRepositoryExternal {

    override suspend fun fetchWeeklyChallengeDetail(challengeId: String?) = getFlowResult {
        weeklyMagicDataSource.fetchWeeklyChallengeDetails(challengeId)
    }

    override suspend fun fetchWeeklyChallengesMetaData(includeView: Boolean): Flow<RestClientResult<ApiResponseWrapper<WeeklyChallengeMetaData?>>> =
        getFlowResultV2(
            fetchResultFromServer = weeklyMagicDataSource.fetchWeeklyChallengesMetaData(includeView),
            storeResultInCache = { key, value ->
                prefsApi.setStringData(key, serializer.encodeToString(value))
            },
            fetchResultFromCache = {
                prefsApi.getStringData(it)
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun markPreviousWeeklyChallengeAsViewed(challengeId: String) = getFlowResult {
        weeklyMagicDataSource.markPreviousWeeklyChallengeIsViewed(challengeId)
    }

    override suspend fun markPreviousWeeklyChallengeStoryAsViewed(challengeId: String) =
        getFlowResult {
            weeklyMagicDataSource.markPreviousWeeklyChallengeStoryIsViewed(challengeId)
        }

    override suspend fun markCurrentWeeklyChallengeAsViewed(challengeId: String) = getFlowResult {
        weeklyMagicDataSource.markCurrentWeeklyChallengeIsViewed(challengeId)
    }

    override suspend fun markWeeklyChallengeOnBoarded() = getFlowResult {
        weeklyMagicDataSource.markWeeklyChallengeOnBoarded()
    }

}
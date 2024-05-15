package com.jar.app.feature_weekly_magic_common.shared.data.network

import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeDetail
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants.Endpoints
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

internal class WeeklyChallengeDataSource constructor(
    private val client: HttpClient
) :
    BaseDataSource() {

    suspend fun fetchWeeklyChallengeDetails(challengeId: String? = null) =
        getResult<ApiResponseWrapper<WeeklyChallengeDetail?>> {
            client.get {
                url(Endpoints.FETCH_WEEKLY_CHALLENGE_DETAIL)
                if (challengeId.isNullOrBlank().not())
                    parameter("challengeId", challengeId)
            }
        }

    suspend fun fetchWeeklyChallengesMetaData(includeView: Boolean) =
        getResultV2<ApiResponseWrapper<WeeklyChallengeMetaData?>>(
            getCachingKey = {
                "weekly_challenge_meta_data"
            },
            apiCall = {
                client.get {
                    url(Endpoints.FETCH_WEEKLY_CHALLENGE_META_DATA)
                    parameter("includeView", includeView)
                }
            }
        )

    suspend fun markCurrentWeeklyChallengeIsViewed(challengeId: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.put {
                url(Endpoints.MARK_CURRENT_WEEKLY_CHALLENGE_VIEWED)
                parameter("challengeId", challengeId)
            }
        }

    suspend fun markPreviousWeeklyChallengeIsViewed(challengeId: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.put {
                url(Endpoints.MARK_PREVIOUS_WEEKLY_CHALLENGE_VIEWED)
                parameter("challengeId", challengeId)
            }
        }

    suspend fun markPreviousWeeklyChallengeStoryIsViewed(challengeId: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.put {
                url(Endpoints.MARK_WEEKLY_CHALLENGE_STORY_VIEWED)
                parameter("challengeId", challengeId)
            }
        }

    suspend fun markWeeklyChallengeOnBoarded() =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.MARK_WEEKLY_CHALLENGE_ONBOARDED)
            }
        }
}
package com.jar.app.feature_weekly_magic.shared.data.network

import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import com.jar.app.feature_weekly_magic.shared.util.WeeklyMagicConstants.Endpoints
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeInfo

class WeeklyChallengeDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchWeeklyChallengeInfo() = getResult<ApiResponseWrapper<WeeklyChallengeInfo?>> {
        client.get {
            url(Endpoints.FETCH_WEEKLY_CHALLENGE_INFO)
        }
    }

    suspend fun markWeeklyChallengeInfoAsViewed(challengeId: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.put {
                url(Endpoints.MARK_WEEKLY_CHALLENGE_INFO_VIEWED)
                parameter("challengeId", challengeId)
            }
        }

}
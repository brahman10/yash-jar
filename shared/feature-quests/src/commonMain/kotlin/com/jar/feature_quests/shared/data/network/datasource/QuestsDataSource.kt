package com.jar.feature_quests.shared.data.network.datasource;

import com.jar.feature_quests.shared.domain.model.QuestionAnswersData
import com.jar.feature_quests.shared.domain.model.QuestsDashboardData
import com.jar.feature_quests.shared.domain.model.RewardsResponse
import com.jar.feature_quests.shared.domain.model.SubmitAnswerData
import com.jar.feature_quests.shared.domain.model.WelcomeRewardData
import com.jar.feature_quests.shared.domain.model.request.SubmitAnswerRequestData
import com.jar.feature_quests.shared.util.QuestsConstants
import com.jar.feature_quests.shared.util.QuestsConstants.Endpoints.REWARDS_PAGE
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.*

internal class QuestsDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {
    suspend fun fetchWelcomeReward() = getResult<ApiResponseWrapper<WelcomeRewardData?>> {
        client.get { url(QuestsConstants.Endpoints.WELCOME_REWARD) }
    }

    suspend fun unlockWelcomeReward() = getResult<ApiResponseWrapper<Unit?>> {
        client.post { url(QuestsConstants.Endpoints.UNLOCK_WELCOME_REWARD) }
    }

    suspend fun fetchHomePage() = getResult<ApiResponseWrapper<QuestsDashboardData>> {
        client.get { url(QuestsConstants.Endpoints.HOME_PAGE) }
    }

    suspend fun fetchQuestRewards() = getResult<ApiResponseWrapper<RewardsResponse?>> {
        client.get {
            url(REWARDS_PAGE)
        }
    }

    suspend fun getQuizGameQuestion() = getResult<ApiResponseWrapper<QuestionAnswersData?>> {
        client.get { url(QuestsConstants.Endpoints.QUIZ_GAME_QUESTION) }
    }

    suspend fun markAnswer(data: SubmitAnswerRequestData) = getResult<ApiResponseWrapper<SubmitAnswerData?>> {
        client.post {
            url(QuestsConstants.Endpoints.MARK_ANSWER)
            setBody(data)
        }
    }

    suspend fun markGameInProgress(gameType: String) = getResult<ApiResponseWrapper<Unit?>> {
        client.put {
            url(QuestsConstants.Endpoints.MARK_IN_PROGRESS)
            parameter("gameType", gameType)
        }
    }
}

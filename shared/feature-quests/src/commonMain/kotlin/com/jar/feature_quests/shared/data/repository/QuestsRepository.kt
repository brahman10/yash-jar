package com.jar.feature_quests.shared.data.repository

import com.jar.feature_quests.shared.domain.model.QuestionAnswersData
import com.jar.feature_quests.shared.domain.model.QuestsDashboardData
import com.jar.feature_quests.shared.domain.model.SubmitAnswerData
import com.jar.feature_quests.shared.domain.model.RewardsResponse
import com.jar.feature_quests.shared.domain.model.WelcomeRewardData
import com.jar.feature_quests.shared.domain.model.request.SubmitAnswerRequestData
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface QuestsRepository : BaseRepository {
    suspend fun fetchWelcomeReward(): Flow<RestClientResult<ApiResponseWrapper<WelcomeRewardData?>>>
    suspend fun unlockWelcomeReward(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
    suspend fun fetchHomePage(): Flow<RestClientResult<ApiResponseWrapper<QuestsDashboardData>>>
    suspend fun getQuizGameQuestion(): Flow<RestClientResult<ApiResponseWrapper<QuestionAnswersData?>>>
    suspend fun markAnswer(data: SubmitAnswerRequestData): Flow<RestClientResult<ApiResponseWrapper<SubmitAnswerData?>>>
    suspend fun fetchQuestRewards(): Flow<RestClientResult<ApiResponseWrapper<RewardsResponse?>>>

    suspend fun markGameInProgress(gameType: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}
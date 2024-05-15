package com.jar.app.feature_goal_based_saving.shared.data.repository

import com.jar.app.feature_goal_based_saving.shared.data.model.*
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface GoalBasedSavingRepository: BaseRepository {
    suspend fun fetchGoalIntroPageStaticResponse(): Flow<RestClientResult<ApiResponseWrapper<GoalSavingsIntoPage>>>
    suspend fun fetchGoalNameScreenResponse(): Flow<RestClientResult<ApiResponseWrapper<GoalFirstQuestionResponse>>>
    suspend fun fetchGoalAmountScreenDetails(): Flow<RestClientResult<ApiResponseWrapper<GoalAmountResponse>>>
    suspend fun fetchGoalDurationScreenDetails(amount: Int): Flow<RestClientResult<ApiResponseWrapper<GoalDurationResponse>>>
    suspend fun fetchDailyAmount(amount: Int, duration: Int): Flow<RestClientResult<ApiResponseWrapper<CalculateDailyAmountResponse>>>
    suspend fun fetchMergeGoalResposnse(): Flow<RestClientResult<ApiResponseWrapper<MergeGoalResponse>>>
    suspend fun fetchMandateResposnse(amount: Int, savingsType: String): Flow<RestClientResult<ApiResponseWrapper<MandateInfo>>>
    suspend fun fetchAbandonScreenResposnse(): Flow<RestClientResult<ApiResponseWrapper<AbandonedScreenResponse>>>
    suspend fun createGoal(createGoalRequest: CreateGoalRequest): Flow<RestClientResult<ApiResponseWrapper<CreateGoalResponse>>>
    suspend fun fetchQnA(): Flow<RestClientResult<ApiResponseWrapper<QnAResponse>>>
    suspend fun endGoal(goalEndRequest: GoalEndRequest): Flow<RestClientResult<ApiResponseWrapper<GoalEndResponse>>>
    suspend fun fetchMangeGoal(): Flow<RestClientResult<ApiResponseWrapper<AbandonedScreenResponse>>>
    suspend fun fetchEndGoalScreenResponse(): Flow<RestClientResult<ApiResponseWrapper<EndScreenResponse>>>
    suspend fun fetchHomefeedScreen(): Flow<RestClientResult<ApiResponseWrapper<HomefeedGoalProgressReponse>>>
    suspend fun fetchGBSSettings(): Flow<RestClientResult<ApiResponseWrapper<GBSSettingResponse>>>
    suspend fun endScreenViewed(goalId: String): Flow<RestClientResult<ApiResponseWrapper<EndScreenViewedResponse?>>>
    suspend fun getGoalTransactionResponse(goalId: String): Flow<RestClientResult<ApiResponseWrapper<GoalStatusResponse>>>
    suspend fun updateDailyGoalRecurringAmount(amount: Float): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>>
}
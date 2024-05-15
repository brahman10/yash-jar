package com.jar.app.feature_savings_common.shared.data.repository

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_savings_common.shared.domain.model.*
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import kotlinx.coroutines.flow.Flow

interface SavingsCommonRepository : BaseRepository {

    suspend fun fetchSavingsDetails(savingsType: SavingsType): Flow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>

    suspend fun disableSavings(savingsType: SavingsType): Flow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>

    suspend fun fetchSavingSetupInfo(
        savingsSubscriptionType: SavingsSubscriptionType,
        savingsType: SavingsType,
        savingStateContext: String?
    ): Flow<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>

    suspend fun updateUserSavings(updateUserSavingRequest: UpdateUserSavingRequest): Flow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>

    suspend fun manageSavingsPreference(
        savingsType: SavingsType,
        enableAutoSave: Boolean
    ): Flow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>

    suspend fun fetchGoalBasedSavingSettings(): Flow<RestClientResult<ApiResponseWrapper<GoalBasedSavingDetails>>>
}
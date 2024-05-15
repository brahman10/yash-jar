package com.jar.app.feature_savings_common.shared.domain.repository

import com.jar.app.feature_savings_common.shared.data.network.SavingsCommonDataSource
import com.jar.app.feature_savings_common.shared.data.repository.SavingsCommonRepository
import com.jar.app.feature_savings_common.shared.domain.model.*
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

class SavingsCommonRepositoryImpl constructor(
    private val savingsCommonDataSource: SavingsCommonDataSource
) : SavingsCommonRepository {

    override suspend fun fetchSavingsDetails(savingsType: SavingsType) = getFlowResult {
        savingsCommonDataSource.fetchSavingsDetails(savingsType)
    }

    override suspend fun disableSavings(savingsType: SavingsType) = getFlowResult {
        savingsCommonDataSource.disableSavings(savingsType)
    }

    override suspend fun fetchSavingSetupInfo(
        savingsSubscriptionType: SavingsSubscriptionType,
        savingsType: SavingsType,
        savingStateContext: String?
    ) = getFlowResult {
        savingsCommonDataSource.fetchSavingSetupInfo(
            savingsSubscriptionType,
            savingsType,
            savingStateContext
        )
    }

    override suspend fun updateUserSavings(updateUserSavingRequest: UpdateUserSavingRequest) =
        getFlowResult {
            savingsCommonDataSource.updateUserSavings(updateUserSavingRequest)
        }

    override suspend fun manageSavingsPreference(
        savingsType: SavingsType,
        enableAutoSave: Boolean
    ) = getFlowResult {
        savingsCommonDataSource.manageSavingsPreference(savingsType, enableAutoSave)
    }

    override suspend fun fetchGoalBasedSavingSettings(): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_savings_common.shared.domain.model.GoalBasedSavingDetails>>> {
        return getFlowResult {
            savingsCommonDataSource.fetchGoalBasedSavingSettings()
        }
    }
}
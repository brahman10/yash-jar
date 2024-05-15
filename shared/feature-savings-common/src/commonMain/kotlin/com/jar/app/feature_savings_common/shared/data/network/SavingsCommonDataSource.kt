package com.jar.app.feature_savings_common.shared.data.network

import com.jar.app.feature_savings_common.shared.domain.model.*
import com.jar.app.feature_savings_common.shared.util.SavingsConstants.Endpoints
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

class SavingsCommonDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchSavingsDetails(savingsType: SavingsType) =
        getResult<ApiResponseWrapper<UserSavingsDetails>> {
            client.get {
                url(Endpoints.FETCH_SAVING_DETAILS)
                parameter("savingsType", savingsType.name)
            }
        }

    suspend fun disableSavings(savingsType: SavingsType) =
        getResult<ApiResponseWrapper<UserSavingsDetails>> {
            client.get {
                url(Endpoints.DISABLE_SAVINGS)
                parameter("savingsType", savingsType.name)
            }
        }

    suspend fun fetchSavingSetupInfo(
        savingsSubscriptionType: SavingsSubscriptionType,
        savingsType: SavingsType,
        savingStateContext: String?
    ) = getResult<ApiResponseWrapper<SavingSetupInfo>> {
        client.get {
            url(Endpoints.FETCH_SAVING_SETUP_INFO)
            parameter("subscriptionType", savingsSubscriptionType.name)
            parameter("savingsType", savingsType.name)
            parameter("context", savingStateContext)
        }
    }

    suspend fun updateUserSavings(updateUserSavingRequest: UpdateUserSavingRequest) =
        getResult<ApiResponseWrapper<UserSavingsDetails>> {
            client.post {
                url(Endpoints.UPDATE_SAVING_DETAILS)
                setBody(updateUserSavingRequest)
            }
        }

    suspend fun manageSavingsPreference(savingsType: SavingsType, enableAutoSave: Boolean) =
        getResult<ApiResponseWrapper<UserSavingsDetails>> {
            client.get {
                url(Endpoints.MANAGE_SAVING_PREFERENCE)
                parameter("enableAutoSave", enableAutoSave)
                parameter("savingsType", savingsType.name)
            }
        }

    suspend fun fetchGoalBasedSavingSettings() = getResult<ApiResponseWrapper<GoalBasedSavingDetails>> {
        client.get {
            url(Endpoints.GOAL_BASED_SAVING_SETTINGS)
        }
    }
}
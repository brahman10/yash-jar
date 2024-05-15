package com.jar.app.feature_goal_based_saving.shared.data.network

import com.jar.app.feature_goal_based_saving.shared.data.model.*
import com.jar.app.feature_goal_based_saving.shared.data.network.GoalBasedSavingNetworkConstants.FETCH_GOAL_AMOUNT_BREAKDOWN_SCREEN_URL
import com.jar.app.feature_goal_based_saving.shared.data.network.GoalBasedSavingNetworkConstants.FETCH_GOAL_AMOUNT_SCREEN_URL
import com.jar.app.feature_goal_based_saving.shared.data.network.GoalBasedSavingNetworkConstants.FETCH_GOAL_DURATION_SCREEN_URL
import com.jar.app.feature_goal_based_saving.shared.data.network.GoalBasedSavingNetworkConstants.FETCH_GOAL_NAME_SCREEN_URL
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

internal class GoalBasedSavingDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {
    val base = "/v2/api/dashboard/static"
    suspend fun fetchIntroPageStaticData() =
        getResult<ApiResponseWrapper<GoalSavingsIntoPage>> {
            client.get {
                url(base)
                parameter("contentType", "SAVINGS_GOAL_INTRO_SCREEN")
            }
        }
    suspend fun fetchGoalNameScreenResponse() =
        getResult<ApiResponseWrapper<GoalFirstQuestionResponse>> {
            client.get {
                url(FETCH_GOAL_NAME_SCREEN_URL)
            }
        }
    suspend fun fetchGoalAmountScreenResponse() =
        getResult<ApiResponseWrapper<GoalAmountResponse>> {
            client.get {
                url(FETCH_GOAL_AMOUNT_SCREEN_URL)
            }
        }
    suspend fun fetchGoalDurationScreenResponse(amount: Int) =
        getResult<ApiResponseWrapper<GoalDurationResponse>> {
            client.get {
                url(FETCH_GOAL_DURATION_SCREEN_URL)
                parameter("amount", amount)
            }
        }

    suspend fun fetchDailyCalculatedAmountResponse(amount: Int, duration: Int) =
        getResult<ApiResponseWrapper<CalculateDailyAmountResponse>> {
            client.get {
                url(FETCH_GOAL_AMOUNT_BREAKDOWN_SCREEN_URL)
                parameter("amount", amount)
                parameter("month", duration)
            }
        }

    suspend fun fetchMergeGoalResponse() =
        getResult<ApiResponseWrapper<MergeGoalResponse>> {
            client.get {
                url("/v1/api/goal/preSetup/getMergePlansPage")
            }
        }

    suspend fun fetchMandateInfoResponse(amount: Int, savingsType: String) =
        getResult<ApiResponseWrapper<MandateInfo>> {
            client.get {
                url("/v1/api/autopay/mandateResetRequired")
                parameter("amount", amount)
                parameter("savingsType", savingsType)
            }
        }

    suspend fun fetchAbandonScreenResponse() =
        getResult<ApiResponseWrapper<AbandonedScreenResponse>> {
            client.get {
                url("/v1/api/goal/preSetup/getAbandonedPage")
            }
        }

    suspend fun createGoal(createGoalRequest: CreateGoalRequest?) =
        getResult<ApiResponseWrapper<CreateGoalResponse>> {
            client.post {
                url("/v1/api/goal/preSetup/createGoal")
                setBody(createGoalRequest)
            }
        }

    suspend fun fetchQnA() =
        getResult<ApiResponseWrapper<QnAResponse>> {
            client.get {
                url("/v1/api/goal/postSetup/getEndGoalQna")
            }
        }

    suspend fun endGoal(goalEndRequest: GoalEndRequest) =
        getResult<ApiResponseWrapper<GoalEndResponse>> {
            client.put {
                url("/v1/api/goal/postSetup/endGoal")
                parameter("goalId", goalEndRequest.goalId)
                parameter("message", goalEndRequest.message)
            }
        }

    suspend fun fetchMangeGoal() = getResult<ApiResponseWrapper<AbandonedScreenResponse>> {
        client.post {
            url("/v1/api/goal/preSetup/createGoal")
        }
    }

    suspend fun fetchEndGoalScreenResponse() = getResult<ApiResponseWrapper<EndScreenResponse>> {
        client.get {
            url(" /v1/api/goal/postSetup/getEndGoalAbandonedPopup")
        }
    }

    suspend fun fetchHomefeedScreen() = getResult<ApiResponseWrapper<HomefeedGoalProgressReponse>> {
        client.get {
            url("/v1/api/goal/postSetup/getHomefeedProgress")
        }
    }

    suspend fun fetchGBSSettings() = getResult<ApiResponseWrapper<GBSSettingResponse>> {
        client.get {
            url("/v1/api/goal/postSetup/getSettingsProgress")
        }
    }

    suspend fun endScreenViewed(goalId: String)  = getResult<ApiResponseWrapper<EndScreenViewedResponse?>> {
        client.put {
            url("/v1/api/goal/postSetup/updateEndStateViewed")
            parameter("goalId", goalId)
        }
    }

    suspend fun getGoalTransactionResponse(goalId: String) = getResult<ApiResponseWrapper<GoalStatusResponse>> {
        client.get {
            url("/v1/api/goal/setup/status")
            parameter("goalId", goalId)
        }
    }

    suspend fun updateDailyGoalRecurringAmount(amount: Float)  = getResult<ApiResponseWrapper<DailyInvestmentStatus?>> {
        client.get {
            url("v2/api/dashboard/recurring")
            parameter("amount", amount)
        }
    }
}
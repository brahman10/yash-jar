package com.jar.app.feature_post_setup.data.network

import com.jar.app.core_base.domain.model.GenericFaqList
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.toJsonElement
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_post_setup.domain.model.DSFailureInfo
import com.jar.app.feature_post_setup.domain.model.UserPostSetupData
import com.jar.app.feature_post_setup.domain.model.calendar.CalendarDataResp
import com.jar.app.feature_post_setup.domain.model.calendar.CalendarSavingOperations
import com.jar.app.feature_post_setup.domain.model.setting.PostSetupQuickActionList
import com.jar.app.feature_post_setup.util.PostSetupConstants.Endpoints
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

internal class PostSetupDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    companion object {
        private const val LANDING = "LANDING"
        private const val FAQ = "FAQ"
        private const val DS_QUICK_ACTIONS = "DS_QUICK_ACTIONS"
        private const val DS_OPERATIONS = "DS_OPERATIONS"
    }

    suspend fun fetchPostSetupUserData() =
        getResult<ApiResponseWrapper<UserPostSetupData>> {
            client.get {
                url(Endpoints.FETCH_POST_SETUP_USER_DATA)
                parameter("type", SavingsType.DAILY_SAVINGS.name)
            }
        }

    suspend fun fetchPostSetupCalendarData(startDate: String, endDate: String) =
        getResult<ApiResponseWrapper<CalendarDataResp>> {
            client.post {
                url(Endpoints.FETCH_POST_SETUP_CALENDAR_DATA)
                setBody(
                    JsonObject(
                        mapOf(
                            "type" to SavingsType.DAILY_SAVINGS.name.toJsonElement(),
                            "startDate" to startDate.toJsonElement(),
                            "endDate" to endDate.toJsonElement()
                        )
                    )
                )
            }
        }

    suspend fun fetchPostSetupQuickActions() =
        getResult<ApiResponseWrapper<PostSetupQuickActionList>> {
            client.get {
                url(Endpoints.FETCH_POST_SETUP_QUICK_ACTIONS)
                parameter("contentType", DS_QUICK_ACTIONS)
            }
        }

    suspend fun fetchPostSetupFaq() =
        getResult<ApiResponseWrapper<GenericFaqList>> {
            client.get {
                url(Endpoints.FETCH_POST_SETUP_FAQ)
                parameter("contentType", FAQ)
            }
        }

    suspend fun fetchPostSetupSavingOperations() =
        getResult<ApiResponseWrapper<CalendarSavingOperations>> {
            client.get {
                url(Endpoints.FETCH_POST_SETUP_SAVING_OPERATION)
                parameter("contentType", DS_OPERATIONS)
            }
        }

    suspend fun initiatePaymentForFailedTransactions(
        amount: Float,
        paymentProvider: String,
        type: String,
        roundOffsLinked: List<String>
    ) = getResult<ApiResponseWrapper<InitiatePaymentResponse>> {
        client.post {
            url(Endpoints.INITIATE_PAYMENT_FOR_FAILED_TRANSACTIONS)
            setBody(
                JsonObject(
                    mapOf(
                        "txnAmt" to amount.toJsonElement(),
                        "paymentProvider" to paymentProvider.toJsonElement(),
                        "type" to type.toJsonElement(),
                        "roundOffsLinked" to JsonArray(
                            roundOffsLinked.map { it.toJsonElement() }
                        )
                    )
                )
            )
        }
    }

    suspend fun fetchPostSetupFailureInfo() =
        getResult<ApiResponseWrapper<DSFailureInfo?>> {
            client.get {
                url(Endpoints.FETCH_POST_SETUP_FAILURE_INFO)
                parameter("type", SavingsType.DAILY_SAVINGS.name)
            }
        }
}
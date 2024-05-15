package com.jar.app.feature_health_insurance.shared.data.network

import com.jar.app.feature_health_insurance.shared.data.models.CreateProposalRequest
import com.jar.app.feature_health_insurance.shared.data.models.CreateProposalResponse
import com.jar.app.feature_health_insurance.shared.data.models.IncompleteProposal
import com.jar.app.feature_health_insurance.shared.data.models.InitiateInsurancePlanResponse
import com.jar.app.feature_health_insurance.shared.data.models.InitiateInsuranceProposalRequest
import com.jar.app.feature_health_insurance.shared.data.models.add_details.AddDetailsScreenStaticDataResponse
import com.jar.app.feature_health_insurance.shared.data.models.benefits.BenefitsDetailsResponse
import com.jar.app.feature_health_insurance.shared.data.models.landing1.LandingPageResponse1
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceTransactionsData
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.ManageScreenData
import com.jar.app.feature_health_insurance.shared.data.models.payment_status.PaymentStatusResponse
import com.jar.app.feature_health_insurance.shared.data.models.plan_comparison.PlanComparisonResponse
import com.jar.app.feature_health_insurance.shared.data.models.select_premium.SelectPremiumResponse
import com.jar.app.feature_health_insurance.shared.data.models.transaction_details.InsuranceTransactionDetails
import com.jar.app.feature_health_insurance.shared.util.Constants
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

internal class HealthInsuranceDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchInsurancePlans(orderId: String) =
        getResult<ApiResponseWrapper<SelectPremiumResponse>> {
            client.get {
                url(Constants.Endpoints.FETCH_PREMIUM_DETAILS)
                parameter("orderId", orderId)
            }
        }

    suspend fun fetchLandingScreenDetails() =
        getResult<ApiResponseWrapper<LandingPageResponse1>> {
            client.get {
                url(Constants.Endpoints.FETCH_LANDING_DETAILS)
            }
        }

    suspend fun fetchBenefitsDetails(insuranceId: String?) =
        getResult<ApiResponseWrapper<BenefitsDetailsResponse>> {
            client.get {
                url(Constants.Endpoints.FETCH_BENEFITS)
                parameter("insuranceId", insuranceId)
            }
        }


    suspend fun fetchIncompleteProposal() =
        getResult<ApiResponseWrapper<IncompleteProposal>> {
            client.get {
                url(Constants.Endpoints.FETCH_INCOMPLETE_PROPOSAL)
            }
        }

    suspend fun getPaymentStatus(insuranceId: String) =
        getResult<ApiResponseWrapper<PaymentStatusResponse?>> {
            client.get {
                url(Constants.Endpoints.FETCH_PAYMENT_STATUS)
                parameter("insuranceId", insuranceId)
            }
        }

    suspend fun getPlanComparisons(providerId: String) =
        getResult<ApiResponseWrapper<PlanComparisonResponse>> {
            client.get {
                url(Constants.Endpoints.FETCH_PLAN_COMPARISONS)
                parameter("providerId", providerId)
            }
        }

    suspend fun initiateInsuranceProposal(
        maxAge: Int, adultCnt: Int, kidCnt: Int
    ) =
        getResult<ApiResponseWrapper<InitiateInsurancePlanResponse>> {
            client.post {
                url(Constants.Endpoints.INITIATE_INSURANCE_PROPOSAL)
                setBody(
                    InitiateInsuranceProposalRequest(
                        maxAge,
                        adultCnt,
                        kidCnt
                    )
                )
            }
        }


    suspend fun createProposal(createProposalRequest: CreateProposalRequest) =
        getResult<ApiResponseWrapper<CreateProposalResponse?>> {
            client.post {
                url(Constants.Endpoints.CREATE_PROPOSAL)
                setBody(createProposalRequest)
            }
        }

    suspend fun fetchAddDetailsScreenStaticDataResponse() =
        getResult<ApiResponseWrapper<AddDetailsScreenStaticDataResponse>> {
            client.get {
                url(Constants.Endpoints.FETCH_ADD_DETAILS_SCREEN_STATIC_DATA)
            }
        }

    suspend fun fetchPaymentConfig(insuranceId: String) =
        getResult<ApiResponseWrapper<InitiatePaymentResponse?>> {
            client.get {
                url(Constants.Endpoints.FETCH_PAYMENT_CONFIG)
                parameter("insuranceId", insuranceId)
            }
        }

    suspend fun fetchManageScreenData(insuranceId: String) =
        getResult<ApiResponseWrapper<ManageScreenData?>> {
            client.get {
                url(Constants.Endpoints.FETCH_MANAGE_SCREEN_DATA)
                parameter("insuranceId", insuranceId)
            }
        }

    suspend fun fetchInsuranceTransactions(insuranceId: String, page: Int, size: Int) =
        getResult<ApiResponseWrapper<InsuranceTransactionsData?>> {
            client.get {
                url(Constants.Endpoints.FETCH_INSURANCE_TRANSACTIONS)
                parameter("insuranceId", insuranceId)
                parameter("page", page)
                parameter("size", size)
            }
        }

    suspend fun fetchInsuranceTransactionDetails(transactionId: String) =
        getResult<ApiResponseWrapper<InsuranceTransactionDetails>> {
            client.get {
                url(Constants.Endpoints.FETCH_INSURANCE_TRANSACTION_DETAILS)
                parameter("transactionId", transactionId)
            }
        }
}
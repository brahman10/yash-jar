package com.jar.app.feature_health_insurance.shared.domain.repository

import com.jar.app.feature_health_insurance.shared.data.models.CreateProposalRequest
import com.jar.app.feature_health_insurance.shared.data.models.CreateProposalResponse
import com.jar.app.feature_health_insurance.shared.data.models.IncompleteProposal
import com.jar.app.feature_health_insurance.shared.data.models.InitiateInsurancePlanResponse
import com.jar.app.feature_health_insurance.shared.data.models.add_details.AddDetailsScreenStaticDataResponse
import com.jar.app.feature_health_insurance.shared.data.models.add_details.CreateInsuranceProposalResponse
import com.jar.app.feature_health_insurance.shared.data.models.add_details.InitiateProposalRequest
import com.jar.app.feature_health_insurance.shared.data.models.add_details.InitiateProposalResponse
import com.jar.app.feature_health_insurance.shared.data.models.benefits.BenefitsDetailsResponse
import com.jar.app.feature_health_insurance.shared.data.models.landing1.LandingPageResponse1
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceTransactionsData
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.ManageScreenData
import com.jar.app.feature_health_insurance.shared.data.models.payment_status.PaymentStatusResponse
import com.jar.app.feature_health_insurance.shared.data.models.plan_comparison.PlanComparisonResponse
import com.jar.app.feature_health_insurance.shared.data.models.select_premium.SelectPremiumResponse
import com.jar.app.feature_health_insurance.shared.data.models.transaction_details.InsuranceTransactionDetails
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface HealthInsuranceRepository : BaseRepository {

    suspend fun fetchInsurancePlans(orderId: String): Flow<RestClientResult<ApiResponseWrapper<SelectPremiumResponse>>>

    suspend fun fetchLandingScreenDetails(): Flow<RestClientResult<ApiResponseWrapper<LandingPageResponse1>>>

    suspend fun fetchBenefitsDetails(insuranceId: String?): Flow<RestClientResult<ApiResponseWrapper<BenefitsDetailsResponse>>>

    suspend fun fetchIncompleteProposal(): Flow<RestClientResult<ApiResponseWrapper<IncompleteProposal>>>

    suspend fun fetchPaymentStatus(insuranceId: String): Flow<RestClientResult<ApiResponseWrapper<PaymentStatusResponse?>>>

    suspend fun fetchPlanComparisons(providerId: String): Flow<RestClientResult<ApiResponseWrapper<PlanComparisonResponse>>>

    suspend fun initiateInsuranceProposal(initiateProposalRequest: InitiateProposalRequest): Flow<RestClientResult<ApiResponseWrapper<InitiateProposalResponse>>>

    suspend fun createProposal(): Flow<RestClientResult<ApiResponseWrapper<CreateInsuranceProposalResponse>>>

    suspend fun fetchPaymentConfig(insuranceId: String): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>

    suspend fun initiateInsurancePlanResponse(
        maxAge: Int,
        adultCnt: Int,
        kidCnt: Int
    ): Flow<RestClientResult<ApiResponseWrapper<InitiateInsurancePlanResponse>>>

    suspend fun createProposal(createProposalRequest: CreateProposalRequest): Flow<RestClientResult<ApiResponseWrapper<CreateProposalResponse?>>>

    suspend fun fetchAddDetailsScreenStaticData(): Flow<RestClientResult<ApiResponseWrapper<AddDetailsScreenStaticDataResponse>>>
    suspend fun fetchManageScreenData(insuranceId: String): Flow<RestClientResult<ApiResponseWrapper<ManageScreenData?>>>
    suspend fun fetchInsuranceTransactions(insuranceId: String, page: Int, size: Int): RestClientResult<ApiResponseWrapper<InsuranceTransactionsData?>>
    suspend fun fetchInsuranceTransactionDetails(transactionId: String): Flow<RestClientResult<ApiResponseWrapper<InsuranceTransactionDetails>>>

}
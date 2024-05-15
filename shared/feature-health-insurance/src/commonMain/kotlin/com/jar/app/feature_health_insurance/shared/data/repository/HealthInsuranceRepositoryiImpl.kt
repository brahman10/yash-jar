package com.jar.app.feature_health_insurance.shared.data.repository

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
import com.jar.app.feature_health_insurance.shared.data.network.HealthInsuranceDataSource
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class HealthInsuranceRepositoryImpl constructor(
    private val healthInsuranceDataSource: HealthInsuranceDataSource
) : HealthInsuranceRepository {
    override suspend fun fetchInsurancePlans(orderId: String): Flow<RestClientResult<ApiResponseWrapper<SelectPremiumResponse>>> =
        getFlowResult {
            healthInsuranceDataSource.fetchInsurancePlans(orderId)
        }

    override suspend fun fetchLandingScreenDetails(): Flow<RestClientResult<ApiResponseWrapper<LandingPageResponse1>>> =
        getFlowResult {
            healthInsuranceDataSource.fetchLandingScreenDetails()
        }

    override suspend fun fetchBenefitsDetails(insuranceId: String?): Flow<RestClientResult<ApiResponseWrapper<BenefitsDetailsResponse>>> =
        getFlowResult {
            healthInsuranceDataSource.fetchBenefitsDetails(insuranceId)
        }

    override suspend fun fetchIncompleteProposal(): Flow<RestClientResult<ApiResponseWrapper<IncompleteProposal>>> =
        getFlowResult {
            healthInsuranceDataSource.fetchIncompleteProposal()
        }

    override suspend fun fetchPaymentStatus(insuranceId: String): Flow<RestClientResult<ApiResponseWrapper<PaymentStatusResponse?>>> =
        getFlowResult {
            healthInsuranceDataSource.getPaymentStatus(insuranceId)
        }

    override suspend fun fetchPlanComparisons(providerId: String): Flow<RestClientResult<ApiResponseWrapper<PlanComparisonResponse>>> =
        getFlowResult {
            healthInsuranceDataSource.getPlanComparisons(providerId)
        }


    override suspend fun initiateInsuranceProposal(initiateProposalRequest: InitiateProposalRequest): Flow<RestClientResult<ApiResponseWrapper<InitiateProposalResponse>>> {
        TODO()
    }

    override suspend fun createProposal(): Flow<RestClientResult<ApiResponseWrapper<CreateInsuranceProposalResponse>>> {
        TODO()
    }

    override suspend fun createProposal(createProposalRequest: CreateProposalRequest): Flow<RestClientResult<ApiResponseWrapper<CreateProposalResponse?>>> {
        return getFlowResult {
            healthInsuranceDataSource.createProposal(createProposalRequest)
        }
    }

    override suspend fun fetchPaymentConfig(insuranceId: String) =
        getFlowResult {
            healthInsuranceDataSource.fetchPaymentConfig(insuranceId)
        }

    override suspend fun initiateInsurancePlanResponse(
        maxAge: Int,
        adultCnt: Int,
        kidCnt: Int
    ): Flow<RestClientResult<ApiResponseWrapper<InitiateInsurancePlanResponse>>> =
        getFlowResult {
            healthInsuranceDataSource.initiateInsuranceProposal(maxAge, adultCnt, kidCnt)
        }

    override suspend fun fetchAddDetailsScreenStaticData(): Flow<RestClientResult<ApiResponseWrapper<AddDetailsScreenStaticDataResponse>>> =
        getFlowResult {
            healthInsuranceDataSource.fetchAddDetailsScreenStaticDataResponse()
        }

    override suspend fun fetchManageScreenData(insuranceId: String): Flow<RestClientResult<ApiResponseWrapper<ManageScreenData?>>> =
        getFlowResult {
            healthInsuranceDataSource.fetchManageScreenData(insuranceId)
        }

    override suspend fun fetchInsuranceTransactions(
        insuranceId: String,
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<InsuranceTransactionsData?>> =
        healthInsuranceDataSource.fetchInsuranceTransactions(insuranceId, page, size)

    override suspend fun fetchInsuranceTransactionDetails(
        transactionId: String,
    ): Flow<RestClientResult<ApiResponseWrapper<InsuranceTransactionDetails>>> =
        getFlowResult { healthInsuranceDataSource.fetchInsuranceTransactionDetails(transactionId) }



}
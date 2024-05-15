package com.jar.app.feature_gold_lease.shared.data.repository

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseFaq
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseLandingDetails
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseRiskFactor
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseTermsAndConditions
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseTransaction
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2Details
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2Filters
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2GoldOptions
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2InitiateRequest
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2InitiateResponse
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2JewellerDetails
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2JewellerListing
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2MyOrders
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2OrderSummary
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2OrderSummaryScreenData
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2PlanList
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2StatusResponse
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2UserLeases
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface GoldLeaseRepository : BaseRepository {
    suspend fun fetchGoldLeaseRetryData(leaseId: String): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2OrderSummaryScreenData?>>>

    suspend fun fetchGoldLeaseStatus(leaseId: String): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2StatusResponse?>>>

    suspend fun fetchGoldLeaseV2Transactions(leaseId: String): Flow<RestClientResult<ApiResponseWrapper<List<GoldLeaseTransaction>?>>>

    suspend fun fetchUserLeaseDetails(leaseId: String): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2Details?>>>

    suspend fun fetchGoldLeaseV2MyOrders(): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2MyOrders?>>>

    suspend fun fetchUserLeases(page: Int, size: Int, userLeasesFilter: String): RestClientResult<ApiResponseWrapper<GoldLeaseV2UserLeases?>>

    suspend fun initiateGoldLeaseV2(goldLeaseV2InitiateRequest: GoldLeaseV2InitiateRequest): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2InitiateResponse?>>>

    suspend fun fetchGoldLeaseOrderSummary(assetLeaseConfigId: String): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2OrderSummary?>>>

    suspend fun fetchGoldLeaseGoldOptions(planId: String): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2GoldOptions?>>>

    suspend fun fetchGoldLeaseJewellerListings(): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2JewellerListing?>>>

    suspend fun fetchGoldLeasePlanFilters(): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2Filters?>>>

    suspend fun fetchGoldLeasePlans(leasePlanListingFilter: String, pageNo: Int, pageSize: Int): RestClientResult<ApiResponseWrapper<GoldLeaseV2PlanList?>>

    suspend fun fetchJewellerDetails(jewellerId: String): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2JewellerDetails?>>>

    suspend fun fetchGoldLeaseLandingDetails(): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseLandingDetails?>>>

    suspend fun fetchGoldLeaseFaqs(): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseFaq?>>>

    suspend fun fetchGoldLeaseTermsAndConditions(): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseTermsAndConditions?>>>

    suspend fun fetchGoldLeaseRiskFactors(): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseRiskFactor?>>>

}
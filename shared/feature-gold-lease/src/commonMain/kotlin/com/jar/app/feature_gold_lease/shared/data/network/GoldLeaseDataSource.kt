package com.jar.app.feature_gold_lease.shared.data.network

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
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseConstants.Endpoints
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

class GoldLeaseDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchGoldLeaseRetryData(leaseId: String) =
        getResult<ApiResponseWrapper<GoldLeaseV2OrderSummaryScreenData?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_LEASE_RETRY)
                parameter("leaseId", leaseId)
            }
        }

    suspend fun fetchGoldLeaseStatus(leaseId: String) =
        getResult<ApiResponseWrapper<GoldLeaseV2StatusResponse?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_LEASE_STATUS)
                parameter("leaseId", leaseId)
            }
        }

    suspend fun fetchGoldLeaseV2Transactions(leaseId: String) =
        getResult<ApiResponseWrapper<List<GoldLeaseTransaction>?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_LEASE_V2_TRANSACTIONS)
                parameter("leaseId", leaseId)
            }
        }

    suspend fun fetchUserLeaseDetails(leaseId: String) =
        getResult<ApiResponseWrapper<GoldLeaseV2Details?>> {
            client.get {
                url(Endpoints.FETCH_USER_LEASE_DETAILS)
                parameter("leaseId", leaseId)
            }
        }

    suspend fun fetchGoldLeaseV2MyOrders() =
        getResult<ApiResponseWrapper<GoldLeaseV2MyOrders?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_LEASE_MY_ORDERS)
            }
        }

    suspend fun fetchUserLeases(page: Int, size: Int, userLeasesFilter: String) =
        getResult<ApiResponseWrapper<GoldLeaseV2UserLeases?>> {
            client.get {
                url(Endpoints.FETCH_USER_LEASE)
                parameter("page", page)
                parameter("size", size)
                parameter("userLeasesFilter", userLeasesFilter)
            }
        }

    suspend fun initiateGoldLeaseV2(goldLeaseV2InitiateRequest: GoldLeaseV2InitiateRequest) =
        getResult<ApiResponseWrapper<GoldLeaseV2InitiateResponse?>> {
            client.post {
                url(Endpoints.INITIATE_GOLD_LEASE_V2)
                setBody(goldLeaseV2InitiateRequest)
            }
        }

    suspend fun fetchGoldLeaseOrderSummary(assetLeaseConfigId: String) =
        getResult<ApiResponseWrapper<GoldLeaseV2OrderSummary?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_LEASE_ORDER_SUMMARY)
                parameter("assetLeaseConfigId", assetLeaseConfigId)
            }
        }

    suspend fun fetchGoldLeaseGoldOptions(planId: String) =
        getResult<ApiResponseWrapper<GoldLeaseV2GoldOptions?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_LEASE_OPTIONS)
                parameter("planId", planId)
            }
        }

    suspend fun fetchGoldLeaseJewellerListings() =
        getResult<ApiResponseWrapper<GoldLeaseV2JewellerListing?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_LEASE_JEWELLER_LISTING)
            }
        }

    suspend fun fetchGoldLeasePlanFilters() =
        getResult<ApiResponseWrapper<GoldLeaseV2Filters?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_LEASE_PLAN_FILTERS)
            }
        }

    suspend fun fetchGoldLeasePlans(leasePlanListingFilter: String, pageNo: Int, pageSize: Int) =
        getResult<ApiResponseWrapper<GoldLeaseV2PlanList?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_LEASE_PLANS)
                parameter("leasePlanListingFilter", leasePlanListingFilter)
                parameter("size", pageSize)
                parameter("page", pageNo)
            }
        }

    suspend fun fetchJewellerDetails(jewellerId: String) =
        getResult<ApiResponseWrapper<GoldLeaseV2JewellerDetails?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_LEASE_JEWELLER_DETAILS)
                parameter("jewellerId", jewellerId)
            }
        }

    suspend fun fetchGoldLeaseLandingDetails() =
        getResult<ApiResponseWrapper<GoldLeaseLandingDetails?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_LEASE_LANDING_DETAILS)
            }
        }

    suspend fun fetchGoldLeaseFaqs() =
        getResult<ApiResponseWrapper<GoldLeaseFaq?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_LEASE_FAQS)
            }
        }

    suspend fun fetchGoldLeaseTermsAndConditions() =
        getResult<ApiResponseWrapper<GoldLeaseTermsAndConditions?>> {
            client.get {
                url(Endpoints.FETCH_LEASE_TERMS_AND_CONDITIONS)
            }
        }

    suspend fun fetchGoldLeaseRiskFactors() =
        getResult<ApiResponseWrapper<GoldLeaseRiskFactor?>> {
            client.get {
                url(Endpoints.FETCH_LEASE_RISK_FACTORS)
            }
        }
}
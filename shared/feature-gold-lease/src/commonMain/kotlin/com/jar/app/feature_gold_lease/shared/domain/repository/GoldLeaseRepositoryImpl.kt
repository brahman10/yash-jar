package com.jar.app.feature_gold_lease.shared.domain.repository

import com.jar.app.feature_gold_lease.shared.data.network.GoldLeaseDataSource
import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2InitiateRequest

internal class GoldLeaseRepositoryImpl constructor(
    private val goldLeaseDataSource: GoldLeaseDataSource
) : GoldLeaseRepository {

    override suspend fun fetchGoldLeaseRetryData(leaseId: String) = getFlowResult {
        goldLeaseDataSource.fetchGoldLeaseRetryData(leaseId)
    }

    override suspend fun fetchGoldLeaseStatus(leaseId: String) = getFlowResult {
        goldLeaseDataSource.fetchGoldLeaseStatus(leaseId = leaseId)
    }

    override suspend fun fetchGoldLeaseV2Transactions(leaseId: String) = getFlowResult {
        goldLeaseDataSource.fetchGoldLeaseV2Transactions(leaseId = leaseId)
    }

    override suspend fun fetchUserLeaseDetails(leaseId: String) = getFlowResult {
        goldLeaseDataSource.fetchUserLeaseDetails(leaseId)
    }

    override suspend fun fetchGoldLeaseV2MyOrders() = getFlowResult {
        goldLeaseDataSource.fetchGoldLeaseV2MyOrders()
    }

    override suspend fun fetchUserLeases(
        page: Int, size: Int, userLeasesFilter: String
    ) = goldLeaseDataSource.fetchUserLeases(page, size, userLeasesFilter)

    override suspend fun initiateGoldLeaseV2(goldLeaseV2InitiateRequest: GoldLeaseV2InitiateRequest) = getFlowResult {
        goldLeaseDataSource.initiateGoldLeaseV2(goldLeaseV2InitiateRequest)
    }

    override suspend fun fetchGoldLeaseOrderSummary(assetLeaseConfigId: String) = getFlowResult {
        goldLeaseDataSource.fetchGoldLeaseOrderSummary(assetLeaseConfigId)
    }

    override suspend fun fetchGoldLeaseGoldOptions(planId: String) = getFlowResult {
        goldLeaseDataSource.fetchGoldLeaseGoldOptions(planId)
    }

    override suspend fun fetchGoldLeaseJewellerListings() = getFlowResult {
        goldLeaseDataSource.fetchGoldLeaseJewellerListings()
    }

    override suspend fun fetchGoldLeasePlanFilters() = getFlowResult {
        goldLeaseDataSource.fetchGoldLeasePlanFilters()
    }

    override suspend fun fetchGoldLeasePlans(leasePlanListingFilter: String, pageNo: Int, pageSize: Int) =
        goldLeaseDataSource.fetchGoldLeasePlans(leasePlanListingFilter = leasePlanListingFilter, pageNo = pageNo, pageSize = pageSize)

    override suspend fun fetchJewellerDetails(jewellerId: String) = getFlowResult {
        goldLeaseDataSource.fetchJewellerDetails(jewellerId = jewellerId)
    }

    override suspend fun fetchGoldLeaseLandingDetails() = getFlowResult {
        goldLeaseDataSource.fetchGoldLeaseLandingDetails()
    }

    override suspend fun fetchGoldLeaseFaqs() = getFlowResult {
        goldLeaseDataSource.fetchGoldLeaseFaqs()
    }

    override suspend fun fetchGoldLeaseTermsAndConditions() = getFlowResult {
        goldLeaseDataSource.fetchGoldLeaseTermsAndConditions()
    }

    override suspend fun fetchGoldLeaseRiskFactors() = getFlowResult {
        goldLeaseDataSource.fetchGoldLeaseRiskFactors()
    }
}
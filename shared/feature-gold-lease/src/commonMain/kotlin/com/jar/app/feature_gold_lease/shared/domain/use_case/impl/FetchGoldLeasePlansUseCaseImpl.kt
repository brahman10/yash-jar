package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeasePlansUseCase

internal class FetchGoldLeasePlansUseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchGoldLeasePlansUseCase{
    override suspend fun fetchGoldLeasePlans(
        leasePlanListingFilter: String,
        pageNo: Int,
        pageSize: Int
    ) = goldLeaseRepository.fetchGoldLeasePlans(
        leasePlanListingFilter = leasePlanListingFilter, pageNo = pageNo, pageSize = pageSize
    )
}
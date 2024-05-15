package com.jar.app.feature_gold_lease.shared.domain.use_case

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2PlanList
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

interface FetchGoldLeasePlansUseCase {

    suspend fun fetchGoldLeasePlans(leasePlanListingFilter: String, pageNo: Int, pageSize: Int): RestClientResult<ApiResponseWrapper<GoldLeaseV2PlanList?>>

}
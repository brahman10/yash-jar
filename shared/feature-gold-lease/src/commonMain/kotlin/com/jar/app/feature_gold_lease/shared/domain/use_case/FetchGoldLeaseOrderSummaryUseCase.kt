package com.jar.app.feature_gold_lease.shared.domain.use_case

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2OrderSummary
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchGoldLeaseOrderSummaryUseCase {

    suspend fun fetchGoldLeaseOrderSummary(assetLeaseConfigId: String): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2OrderSummary?>>>

}
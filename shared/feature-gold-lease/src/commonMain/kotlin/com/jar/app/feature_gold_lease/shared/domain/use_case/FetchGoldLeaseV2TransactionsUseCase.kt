package com.jar.app.feature_gold_lease.shared.domain.use_case

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseTransaction
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchGoldLeaseV2TransactionsUseCase {

    suspend fun fetchGoldLeaseV2Transactions(leaseId: String): Flow<RestClientResult<ApiResponseWrapper<List<GoldLeaseTransaction>?>>>

}
package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.app.feature_transaction.shared.domain.model.TransactionData
import com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

interface FetchMyOrdersUseCase {
    suspend fun fetchMyOrders(request: TransactionListingRequest): RestClientResult<ApiResponseWrapper<List<TransactionData>>>
}
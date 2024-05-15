package com.jar.app.feature_gold_delivery.shared.domain.use_case

import com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchOrderDetailUseCase {
    suspend fun fetchOrderDetail(orderId: String, assetSourceType: String, assetTxnId: String): Flow<RestClientResult<ApiResponseWrapper<NewTransactionDetails?>>>
}
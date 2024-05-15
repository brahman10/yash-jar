package com.jar.app.feature_transaction.shared.domain.use_case

import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.TransactionDetailsV5Data
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchNewTransactionDetailsUseCase {

    suspend fun fetchNewTxnDetails(orderId: String, assetSourceType: String, assetTxnId: String):
            Flow<RestClientResult<ApiResponseWrapper<TransactionDetailsV5Data?>>>

}
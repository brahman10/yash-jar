package com.jar.app.feature_transaction.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_transaction.shared.domain.model.TransactionData
import com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest

interface IFetchTransactionListingUseCase {
    suspend fun fetchTransactionListing(request: TransactionListingRequest): RestClientResult<ApiResponseWrapper<List<TransactionData>>?>
}
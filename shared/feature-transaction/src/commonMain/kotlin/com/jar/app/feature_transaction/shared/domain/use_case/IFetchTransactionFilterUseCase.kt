package com.jar.app.feature_transaction.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_transaction.shared.domain.model.FilterResponse
import kotlinx.coroutines.flow.Flow

interface IFetchTransactionFilterUseCase {
    suspend fun fetchTransactionFilters(): Flow<RestClientResult<ApiResponseWrapper<List<com.jar.app.feature_transaction.shared.domain.model.FilterResponse>>>>
}
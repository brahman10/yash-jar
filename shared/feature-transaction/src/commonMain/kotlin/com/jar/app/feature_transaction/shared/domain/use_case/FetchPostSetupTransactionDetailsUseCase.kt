package com.jar.app.feature_transaction.shared.domain.use_case

import com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchPostSetupTransactionDetailsUseCase {
    suspend fun fetchPostSetupTransactionDetails(
        id: String
    ): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails>>>
}
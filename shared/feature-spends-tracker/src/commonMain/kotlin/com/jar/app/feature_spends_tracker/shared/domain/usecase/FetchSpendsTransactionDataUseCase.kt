package com.jar.app.feature_spends_tracker.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_spends_tracker.shared.domain.model.spends_transaction_data.SpendsTransactionData

interface FetchSpendsTransactionDataUseCase {
    suspend fun fetchSpendsTransactionData(
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<List<SpendsTransactionData>>>
}
package com.jar.app.feature_transaction.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_transaction.shared.domain.model.WinningData

interface IFetchWinningListingUseCase {
    suspend fun fetchWinningListing(pageNo: Int, pageSize: Int):
            RestClientResult<ApiResponseWrapper<List<com.jar.app.feature_transaction.shared.domain.model.WinningData>>>
}
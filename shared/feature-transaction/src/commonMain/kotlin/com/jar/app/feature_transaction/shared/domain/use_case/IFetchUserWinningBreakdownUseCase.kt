package com.jar.app.feature_transaction.shared.domain.use_case

import com.jar.app.feature_transaction.shared.domain.model.UserWinningBreakdownModel
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface IFetchUserWinningBreakdownUseCase {

    suspend fun fetchUserWinningBreakdown(): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.UserWinningBreakdownModel>>>
}
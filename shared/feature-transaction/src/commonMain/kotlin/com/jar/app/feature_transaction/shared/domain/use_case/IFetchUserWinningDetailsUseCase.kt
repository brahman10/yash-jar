package com.jar.app.feature_transaction.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_transaction.shared.domain.model.UserWinningDetailsRes
import kotlinx.coroutines.flow.Flow

interface IFetchUserWinningDetailsUseCase {

    suspend fun fetchUserWinningDetails(): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.UserWinningDetailsRes>>>
}
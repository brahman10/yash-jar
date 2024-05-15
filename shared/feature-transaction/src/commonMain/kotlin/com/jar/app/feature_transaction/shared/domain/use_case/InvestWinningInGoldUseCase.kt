package com.jar.app.feature_transaction.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_transaction.shared.domain.model.InvestWinningInGoldRequest
import kotlinx.coroutines.flow.Flow

interface InvestWinningInGoldUseCase {

    suspend fun investWinningInGold(investWinningInGoldRequest: com.jar.app.feature_transaction.shared.domain.model.InvestWinningInGoldRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

}
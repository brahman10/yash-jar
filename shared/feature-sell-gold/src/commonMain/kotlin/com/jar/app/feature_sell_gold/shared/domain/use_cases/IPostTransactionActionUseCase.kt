package com.jar.app.feature_sell_gold.shared.domain.use_cases

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_sell_gold.shared.domain.models.RetryPayoutResponse
import com.jar.app.feature_sell_gold_common.shared.TransactionActionType
import kotlinx.coroutines.flow.Flow

interface IPostTransactionActionUseCase {
    suspend fun postTransactionAction(
        type: TransactionActionType,
        orderId: String,
        vpa: String
    ): Flow<RestClientResult<ApiResponseWrapper<RetryPayoutResponse?>>>
}
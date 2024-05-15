package com.jar.app.feature_transaction.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_sell_gold_common.shared.TransactionActionType
import com.jar.app.feature_sell_gold_common.shared.WithdrawalAcceptedResponse
import kotlinx.coroutines.flow.Flow

interface PostTransactionActionUseCase {
    suspend fun postTransactionAction(
        type: TransactionActionType,
        orderId: String,
        vpa: String
    ): Flow<RestClientResult<ApiResponseWrapper<WithdrawalAcceptedResponse>>>
}
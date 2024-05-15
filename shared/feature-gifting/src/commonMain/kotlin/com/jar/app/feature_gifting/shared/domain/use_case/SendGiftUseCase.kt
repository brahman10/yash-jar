package com.jar.app.feature_gifting.shared.domain.use_case

import com.jar.app.feature_gifting.shared.domain.model.SendGiftGoldRequest
import com.jar.app.feature_one_time_payments_common.shared.SendGiftGoldResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface SendGiftUseCase {

    suspend fun sendGift(sendGiftGoldRequest: SendGiftGoldRequest): Flow<RestClientResult<ApiResponseWrapper<SendGiftGoldResponse?>>>
}
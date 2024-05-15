package com.jar.app.feature_gifting.shared.domain.repository

import com.jar.app.feature_gifting.shared.domain.model.GoldGiftReceivedResponse
import com.jar.app.feature_gifting.shared.domain.model.SendGiftGoldRequest
import com.jar.app.feature_gifting.shared.domain.model.GiftGoldOptions
import com.jar.app.feature_one_time_payments_common.shared.SendGiftGoldResponse
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface GiftGoldRepository : BaseRepository {

    suspend fun sendGiftGold(sendGiftGoldRequest: SendGiftGoldRequest): Flow<RestClientResult<ApiResponseWrapper<SendGiftGoldResponse?>>>

    suspend fun fetchReceivedGift(): Flow<RestClientResult<ApiResponseWrapper<List<GoldGiftReceivedResponse>>>>

    suspend fun markReceivedGiftViewed(giftingId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchGiftGoldOptions(): Flow<RestClientResult<ApiResponseWrapper<GiftGoldOptions>>>
}
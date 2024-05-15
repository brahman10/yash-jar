package com.jar.app.feature_gifting.shared.data.network

import com.jar.app.feature_gifting.shared.domain.model.GiftGoldOptions
import com.jar.app.feature_gifting.shared.domain.model.GoldGiftReceivedResponse
import com.jar.app.feature_gifting.shared.domain.model.SendGiftGoldRequest
import com.jar.app.feature_gifting.shared.util.Constants.Endpoints
import com.jar.app.feature_one_time_payments_common.shared.SendGiftGoldResponse
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

internal class GiftGoldDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun sendGiftGold(sendGiftGoldRequest: SendGiftGoldRequest) =
        getResult<ApiResponseWrapper<SendGiftGoldResponse?>> {
            client.post {
                url(Endpoints.SEND_GOLD_GIFT)
                setBody(sendGiftGoldRequest)
            }
        }

    suspend fun fetchReceivedGift() =
        getResult<ApiResponseWrapper<List<GoldGiftReceivedResponse>>> {
            client.get {
                url(Endpoints.FETCH_RECEIVED_GIFTS)
            }
        }

    suspend fun markReceivedGiftViewed(giftingId: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.put {
                url(Endpoints.MARK_RECEIVED_GIFT_VIEWED)
                parameter("giftingId", giftingId)
            }
        }

    suspend fun fetchGiftGoldOptions() =
        getResult<ApiResponseWrapper<GiftGoldOptions>> {
            client.get {
                url(Endpoints.FETCH_GOLD_GIFT_OPTIONS)
                parameter("contentType", "GIFT_GOLD_OPTIONS")
            }
        }
}
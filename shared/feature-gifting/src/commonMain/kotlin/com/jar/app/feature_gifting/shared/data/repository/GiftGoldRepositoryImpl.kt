package com.jar.app.feature_gifting.shared.data.repository

import com.jar.app.feature_gifting.shared.data.network.GiftGoldDataSource
import com.jar.app.feature_gifting.shared.domain.model.SendGiftGoldRequest
import com.jar.app.feature_gifting.shared.domain.repository.GiftGoldRepository

internal class GiftGoldRepositoryImpl constructor(
    private val giftGoldDataSource: GiftGoldDataSource
) : GiftGoldRepository {

    override suspend fun sendGiftGold(sendGiftGoldRequest: SendGiftGoldRequest) =
        getFlowResult { giftGoldDataSource.sendGiftGold(sendGiftGoldRequest) }

    override suspend fun fetchReceivedGift() =
        getFlowResult { giftGoldDataSource.fetchReceivedGift() }

    override suspend fun markReceivedGiftViewed(giftingId: String) = getFlowResult {
        giftGoldDataSource.markReceivedGiftViewed(giftingId)
    }

    override suspend fun fetchGiftGoldOptions() = getFlowResult {
        giftGoldDataSource.fetchGiftGoldOptions()
    }
}
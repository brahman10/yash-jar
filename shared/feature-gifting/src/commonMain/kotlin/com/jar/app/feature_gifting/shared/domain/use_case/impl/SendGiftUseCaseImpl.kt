package com.jar.app.feature_gifting.shared.domain.use_case.impl

import com.jar.app.feature_gifting.shared.domain.model.SendGiftGoldRequest
import com.jar.app.feature_gifting.shared.domain.repository.GiftGoldRepository
import com.jar.app.feature_gifting.shared.domain.use_case.SendGiftUseCase

internal class SendGiftUseCaseImpl constructor(
    private val giftGoldRepository: GiftGoldRepository
) : SendGiftUseCase {

    override suspend fun sendGift(sendGiftGoldRequest: SendGiftGoldRequest) =
        giftGoldRepository.sendGiftGold(sendGiftGoldRequest)
}
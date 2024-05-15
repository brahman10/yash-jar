package com.jar.app.feature_gifting.shared.domain.use_case.impl

import com.jar.app.feature_gifting.shared.domain.repository.GiftGoldRepository
import com.jar.app.feature_gifting.shared.domain.use_case.MarkGiftViewedUseCase

internal class MarkGiftViewedUseCaseImpl constructor(
    private val giftGoldRepository: GiftGoldRepository
) : MarkGiftViewedUseCase {

    override suspend fun markReceivedGiftViewed(giftingId: String) =
        giftGoldRepository.markReceivedGiftViewed(giftingId)
}
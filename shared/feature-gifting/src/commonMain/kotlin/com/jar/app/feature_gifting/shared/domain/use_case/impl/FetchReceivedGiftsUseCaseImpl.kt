package com.jar.app.feature_gifting.shared.domain.use_case.impl

import com.jar.app.feature_gifting.shared.domain.repository.GiftGoldRepository
import com.jar.app.feature_gifting.shared.domain.use_case.FetchReceivedGiftsUseCase

internal class FetchReceivedGiftsUseCaseImpl constructor(
    private val giftGoldRepository: GiftGoldRepository
) : FetchReceivedGiftsUseCase {

    override suspend fun fetchReceivedGift() = giftGoldRepository.fetchReceivedGift()
}
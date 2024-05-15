package com.jar.app.feature_gifting.shared.domain.use_case.impl

import com.jar.app.feature_gifting.shared.domain.repository.GiftGoldRepository
import com.jar.app.feature_gifting.shared.domain.use_case.FetchGiftGoldOptionsUseCase

internal class FetchGiftGoldOptionsUseCaseImpl constructor(
    private val giftGoldRepository: GiftGoldRepository
) : FetchGiftGoldOptionsUseCase {

    override suspend fun fetchGiftGoldOptions() = giftGoldRepository.fetchGiftGoldOptions()
}
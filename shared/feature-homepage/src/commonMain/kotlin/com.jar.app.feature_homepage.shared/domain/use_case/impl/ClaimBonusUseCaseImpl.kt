package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.ClaimBonusUseCase

internal class ClaimBonusUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : ClaimBonusUseCase {
    override suspend fun claimBonus(orderId: String) = homeRepository.claimBonus(orderId)
}
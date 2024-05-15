package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseLandingDetailsUseCase

internal class FetchGoldLeaseLandingDetailsUseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchGoldLeaseLandingDetailsUseCase {
    override suspend fun fetchGoldLeaseLandingDetails() = goldLeaseRepository.fetchGoldLeaseLandingDetails()
}
package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseGoldOptionsUseCase

internal class FetchGoldLeaseGoldOptionsUseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchGoldLeaseGoldOptionsUseCase{
    override suspend fun fetchGoldLeaseGoldOptions(planId: String) = goldLeaseRepository.fetchGoldLeaseGoldOptions(planId = planId)
}
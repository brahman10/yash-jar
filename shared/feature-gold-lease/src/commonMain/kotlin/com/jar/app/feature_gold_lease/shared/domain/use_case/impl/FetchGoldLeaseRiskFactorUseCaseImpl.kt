package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseRiskFactorUseCase

internal class FetchGoldLeaseRiskFactorUseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchGoldLeaseRiskFactorUseCase{
    override suspend fun fetchGoldLeaseRiskFactors() = goldLeaseRepository.fetchGoldLeaseRiskFactors()
}
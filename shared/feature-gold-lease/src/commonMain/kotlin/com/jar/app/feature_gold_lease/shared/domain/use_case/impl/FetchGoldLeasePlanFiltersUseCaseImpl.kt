package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeasePlanFiltersUseCase

internal class FetchGoldLeasePlanFiltersUseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchGoldLeasePlanFiltersUseCase{
    override suspend fun fetchGoldLeasePlanFilters() = goldLeaseRepository.fetchGoldLeasePlanFilters()
}
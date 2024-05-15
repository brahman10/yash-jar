package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseOrderSummaryUseCase

internal class FetchGoldLeaseOrderSummaryUseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchGoldLeaseOrderSummaryUseCase {
    override suspend fun fetchGoldLeaseOrderSummary(assetLeaseConfigId: String) = goldLeaseRepository.fetchGoldLeaseOrderSummary(assetLeaseConfigId)
}
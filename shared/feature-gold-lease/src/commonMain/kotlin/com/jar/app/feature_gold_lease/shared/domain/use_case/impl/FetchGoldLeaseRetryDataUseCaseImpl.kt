package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseRetryDataUseCase

internal class FetchGoldLeaseRetryDataUseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchGoldLeaseRetryDataUseCase {

    override suspend fun fetchGoldLeaseRetryData(leaseId: String) = goldLeaseRepository.fetchGoldLeaseRetryData(leaseId)

}
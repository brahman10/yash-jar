package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseV2TransactionsUseCase

internal class FetchGoldLeaseV2TransactionsImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchGoldLeaseV2TransactionsUseCase {

    override suspend fun fetchGoldLeaseV2Transactions(leaseId: String) =
        goldLeaseRepository.fetchGoldLeaseV2Transactions(leaseId = leaseId)

}
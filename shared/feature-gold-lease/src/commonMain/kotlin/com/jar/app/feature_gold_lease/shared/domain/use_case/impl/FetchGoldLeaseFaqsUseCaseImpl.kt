package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseFaqsUseCase
internal class FetchGoldLeaseFaqsUseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchGoldLeaseFaqsUseCase {
    override suspend fun fetchGoldLeaseFaqs() = goldLeaseRepository.fetchGoldLeaseFaqs()
}
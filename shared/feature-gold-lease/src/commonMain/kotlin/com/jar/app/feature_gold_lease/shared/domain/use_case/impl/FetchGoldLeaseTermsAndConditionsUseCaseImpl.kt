package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseTermsAndConditionsUseCase

internal class FetchGoldLeaseTermsAndConditionsUseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchGoldLeaseTermsAndConditionsUseCase{
    override suspend fun fetchGoldLeaseTermsAndConditions() = goldLeaseRepository.fetchGoldLeaseTermsAndConditions()
}
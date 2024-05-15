package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchUserLeasesUseCase

internal class FetchUserLeasesUseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): FetchUserLeasesUseCase{

    override suspend fun fetchUserLeases(
        page: Int, size: Int, userLeasesFilter: String
    ) = goldLeaseRepository.fetchUserLeases(page, size, userLeasesFilter)

}
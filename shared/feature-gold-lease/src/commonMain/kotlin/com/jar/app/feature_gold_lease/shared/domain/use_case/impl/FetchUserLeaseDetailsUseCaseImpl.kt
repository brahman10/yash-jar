package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchUserLeaseDetailsUseCase

internal class FetchUserLeaseDetailsUseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
) : FetchUserLeaseDetailsUseCase{

    override suspend fun fetchUserLeaseDetails(leaseId: String) = goldLeaseRepository.fetchUserLeaseDetails(leaseId)

}
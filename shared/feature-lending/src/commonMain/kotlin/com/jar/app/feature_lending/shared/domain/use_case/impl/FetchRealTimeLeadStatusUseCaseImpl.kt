package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchRealTimeLeadStatusUseCase

internal class FetchRealTimeLeadStatusUseCaseImpl(
    private val lendingRepository: LendingRepository
) : FetchRealTimeLeadStatusUseCase {
    override suspend fun fetchRealTimeLeadStatus() = lendingRepository.getRealTimeLeadStatus()
}
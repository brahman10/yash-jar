package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchRealTimeCreditDetailsUseCase

internal class FetchRealTimeCreditDetailsUseCaseImpl(
    private val lendingRepository: LendingRepository
) : FetchRealTimeCreditDetailsUseCase {
    override suspend fun fetchRealTimeCreditDetails() = lendingRepository.getRealTimeCreditDetails()
}
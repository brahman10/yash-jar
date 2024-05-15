package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchEmiPlansUseCase

internal class FetchEmiPlansUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : FetchEmiPlansUseCase {

    override suspend fun fetchEmiPlans(amount: Float) = lendingRepository.fetchEmiPlans(amount)

}
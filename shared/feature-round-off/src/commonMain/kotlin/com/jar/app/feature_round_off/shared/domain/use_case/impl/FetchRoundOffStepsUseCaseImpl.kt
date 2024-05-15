package com.jar.app.feature_round_off.shared.domain.use_case.impl

import com.jar.app.feature_round_off.shared.data.repository.RoundOffRepository
import com.jar.app.feature_round_off.shared.domain.use_case.FetchRoundOffStepsUseCase

internal class FetchRoundOffStepsUseCaseImpl constructor(
    private val roundOffRepository: RoundOffRepository
) :
    FetchRoundOffStepsUseCase {
    override suspend fun fetchRoundOffSetupSteps() = roundOffRepository.fetchRoundOffSetupSteps()
}
package com.jar.app.feature_round_off.shared.domain.use_case.impl

import com.jar.app.feature_round_off.shared.data.repository.RoundOffRepository
import com.jar.app.feature_round_off.shared.domain.use_case.FetchInitialRoundOffUseCase

internal class FetchInitialRoundOffUseCaseImpl constructor(
    private val roundOffRepository: RoundOffRepository
) : FetchInitialRoundOffUseCase {

    override suspend fun initialRoundOffsData(type: String) =
        roundOffRepository.initialRoundOffsData(type)

}
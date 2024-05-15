package com.jar.app.feature_gold_sip.shared.domain.use_case.impl

import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase

internal class UpdateGoldSipDetailsUseCaseImpl constructor(
    private val goldSipRepository: com.jar.app.feature_gold_sip.shared.data.repository.GoldSipRepository
) : UpdateGoldSipDetailsUseCase {
    override suspend fun updateGoldSipDetails(updateSipDetails: com.jar.app.feature_gold_sip.shared.domain.model.UpdateSipDetails) =
        goldSipRepository.updateGoldSipDetails(updateSipDetails)
}
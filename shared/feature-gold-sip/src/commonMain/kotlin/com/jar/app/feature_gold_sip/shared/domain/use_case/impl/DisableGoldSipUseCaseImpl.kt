package com.jar.app.feature_gold_sip.shared.domain.use_case.impl

import com.jar.app.feature_gold_sip.shared.domain.use_case.DisableGoldSipUseCase

internal class DisableGoldSipUseCaseImpl constructor(
    private val goldSipRepository: com.jar.app.feature_gold_sip.shared.data.repository.GoldSipRepository
) : DisableGoldSipUseCase {
    override suspend fun disableGoldSip() = goldSipRepository.disableGoldSip()
}
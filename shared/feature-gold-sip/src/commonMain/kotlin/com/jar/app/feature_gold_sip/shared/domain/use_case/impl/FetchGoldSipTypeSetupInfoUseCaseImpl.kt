package com.jar.app.feature_gold_sip.shared.domain.use_case.impl

import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipTypeSetupInfoUseCase

internal class FetchGoldSipTypeSetupInfoUseCaseImpl constructor(
    private val goldSipRepository: com.jar.app.feature_gold_sip.shared.data.repository.GoldSipRepository
) : FetchGoldSipTypeSetupInfoUseCase {
    override suspend fun fetchGoldSipTypeSetupInfo(subscriptionType: String) =
        goldSipRepository.fetchGoldSipTypeSetupInfo(subscriptionType)
}
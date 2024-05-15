package com.jar.app.feature_gold_sip.shared.domain.use_case.impl

import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipIntroUseCase

internal class FetchGoldSipIntroUseCaseImpl constructor(
    private val goldSipRepository: com.jar.app.feature_gold_sip.shared.data.repository.GoldSipRepository
) : FetchGoldSipIntroUseCase {

    override suspend fun fetchGoldSipIntro() = goldSipRepository.fetchGoldSipIntro()

}
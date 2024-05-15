package com.jar.app.feature_gold_sip.shared.domain.use_case.impl

import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchIsEligibleForGoldSipUseCase

internal class FetchIsEligibleForGoldSipUseCaseImpl constructor(
    private val goldSipRepository: com.jar.app.feature_gold_sip.shared.data.repository.GoldSipRepository
) : FetchIsEligibleForGoldSipUseCase {

    override suspend fun fetchIsEligibleForGoldSip() = goldSipRepository.fetchIsEligibleForGoldSip()

}
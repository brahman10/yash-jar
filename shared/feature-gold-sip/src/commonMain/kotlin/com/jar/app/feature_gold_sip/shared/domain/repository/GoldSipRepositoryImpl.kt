package com.jar.app.feature_gold_sip.shared.domain.repository

import com.jar.app.feature_gold_sip.shared.data.network.GoldSipDataSource
import com.jar.app.feature_gold_sip.shared.data.repository.GoldSipRepository

internal class GoldSipRepositoryImpl constructor(
    private val goldSipDataSource: GoldSipDataSource
) : GoldSipRepository {

    override suspend fun fetchGoldSipIntro() = getFlowResult {
        goldSipDataSource.fetchGoldSipIntro()
    }

    override suspend fun fetchIsEligibleForGoldSip() = getFlowResult {
        goldSipDataSource.fetchIsEligibleForGoldSip()
    }

    override suspend fun fetchGoldSipDetails() = getFlowResult {
        goldSipDataSource.fetchGoldSipDetails()
    }

    override suspend fun updateGoldSipDetails(updateSipDetails: com.jar.app.feature_gold_sip.shared.domain.model.UpdateSipDetails) = getFlowResult {
        goldSipDataSource.updateGoldSipDetails(updateSipDetails)
    }

    override suspend fun disableGoldSip() = getFlowResult {
        goldSipDataSource.disableGoldSip()
    }

    override suspend fun fetchGoldSipTypeSetupInfo(subscriptionType: String) = getFlowResult {
        goldSipDataSource.fetchGoldSipTypeSetupInfo(subscriptionType)
    }
}
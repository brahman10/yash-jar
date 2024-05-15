package com.jar.app.feature_gold_lease.shared.domain.use_case.impl

import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2InitiateRequest
import com.jar.app.feature_gold_lease.shared.domain.use_case.InitiateGoldLeaseV2UseCase

internal class InitiateGoldLeaseV2UseCaseImpl constructor(
    private val goldLeaseRepository: GoldLeaseRepository
): InitiateGoldLeaseV2UseCase {

    override suspend fun initiateGoldLeaseV2(goldLeaseV2InitiateRequest: GoldLeaseV2InitiateRequest) =
        goldLeaseRepository.initiateGoldLeaseV2(goldLeaseV2InitiateRequest)

}
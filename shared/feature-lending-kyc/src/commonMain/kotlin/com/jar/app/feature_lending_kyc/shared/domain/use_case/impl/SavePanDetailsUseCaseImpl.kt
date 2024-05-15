package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.SavePanDetailsUseCase
import kotlinx.serialization.json.JsonObject

internal class SavePanDetailsUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
) : SavePanDetailsUseCase {
    override suspend fun savePanDetails(
        jsonObject: JsonObject,
        kycFeatureFlowType: KycFeatureFlowType
    ) = lendingKycRepository.savePanDetails(jsonObject, kycFeatureFlowType)
}
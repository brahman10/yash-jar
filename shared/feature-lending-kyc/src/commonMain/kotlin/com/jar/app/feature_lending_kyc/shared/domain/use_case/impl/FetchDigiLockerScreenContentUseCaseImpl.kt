package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerScreenContentUseCase

internal class FetchDigiLockerScreenContentUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
): FetchDigiLockerScreenContentUseCase {



    override suspend fun fetchDigiLockerScreenContent(
        applicationId: String,
        kycFeatureFlowType: KycFeatureFlowType
    ) =
        lendingKycRepository.fetchDigiLockerScreenContent(applicationId, kycFeatureFlowType)

}
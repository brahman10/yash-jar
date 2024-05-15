package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerVerificationStatusUseCase

internal class FetchDigiLockerVerificationStatusUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
) : FetchDigiLockerVerificationStatusUseCase {
    override suspend fun fetchDigiLockerVerificationStatus(
        kycFeatureFlowType: KycFeatureFlowType,
        shouldEnablePinless: Boolean
    ) =
        lendingKycRepository.fetchDigiLockerVerificationStatus(kycFeatureFlowType, shouldEnablePinless)


}
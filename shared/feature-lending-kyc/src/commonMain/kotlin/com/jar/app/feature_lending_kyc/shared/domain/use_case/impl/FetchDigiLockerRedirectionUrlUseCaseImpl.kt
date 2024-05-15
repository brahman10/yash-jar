package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerRedirectionUrlUseCase

internal class FetchDigiLockerRedirectionUrlUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
) : FetchDigiLockerRedirectionUrlUseCase {
    override suspend fun fetchDigiLockerRedirectionUrl(
        kycFeatureFlowType: KycFeatureFlowType,
        shouldEnablePinless: Boolean
    ) =
        lendingKycRepository.fetchRedirectionUrlForVerificationThroughDigiLocker(
            kycFeatureFlowType,
            shouldEnablePinless
        )


}
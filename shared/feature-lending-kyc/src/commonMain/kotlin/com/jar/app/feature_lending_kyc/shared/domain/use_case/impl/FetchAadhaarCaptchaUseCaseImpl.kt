package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchAadhaarCaptchaUseCase

internal class FetchAadhaarCaptchaUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
): FetchAadhaarCaptchaUseCase {

    override suspend fun fetchAadhaarCaptcha(kycFeatureFlowType: KycFeatureFlowType) =
        lendingKycRepository.fetchAadhaarCaptcha(kycFeatureFlowType)
}
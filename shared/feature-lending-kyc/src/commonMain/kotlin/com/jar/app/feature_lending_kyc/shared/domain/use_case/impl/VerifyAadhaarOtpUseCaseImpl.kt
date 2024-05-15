package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.model.VerifyAadhaarOtpRequest
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyAadhaarOtpUseCase

internal class VerifyAadhaarOtpUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
): VerifyAadhaarOtpUseCase {

    override suspend fun verifyAadhaarOtp(verifyAadhaarOtpRequest: VerifyAadhaarOtpRequest, kycFeatureFlowType: KycFeatureFlowType) =
        lendingKycRepository.verifyAadhaarOtp(verifyAadhaarOtpRequest,kycFeatureFlowType)

}
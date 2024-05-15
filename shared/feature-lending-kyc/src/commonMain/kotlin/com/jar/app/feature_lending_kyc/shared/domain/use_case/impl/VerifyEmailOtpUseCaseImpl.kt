package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyEmailOtpUseCase

internal class VerifyEmailOtpUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
): VerifyEmailOtpUseCase {

    override suspend fun verifyEmailOtp(
        email: String,
        otp: String,
        kycFeatureFlowType: KycFeatureFlowType
    ) = lendingKycRepository.verifyEmailOtp(email, otp, kycFeatureFlowType)

}
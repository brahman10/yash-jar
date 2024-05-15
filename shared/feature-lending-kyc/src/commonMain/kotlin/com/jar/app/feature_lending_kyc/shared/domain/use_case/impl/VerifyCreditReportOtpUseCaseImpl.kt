package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyCreditReportOtpUseCase

internal class VerifyCreditReportOtpUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
) : VerifyCreditReportOtpUseCase {

    override suspend fun verifyCreditReportOtp(otp: String, kycFeatureFlowType: KycFeatureFlowType) =
        lendingKycRepository.verifyCreditReportOtp(otp, kycFeatureFlowType)

}
package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.model.VerifyOtpV2RequestData
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyCreditReportOtpV2UseCase

internal class VerifyCreditReportOtpV2UseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
) : VerifyCreditReportOtpV2UseCase {
    override suspend fun verifyCreditReportOtp(
        requestData: VerifyOtpV2RequestData,
        kycFeatureFlowType: KycFeatureFlowType
    ) =
        lendingKycRepository.verifyCreditReportOtpV2(requestData, kycFeatureFlowType)

}
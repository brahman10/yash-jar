package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestCreditReportOtpV2UseCase

internal class RequestCreditReportOtpV2UseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
): RequestCreditReportOtpV2UseCase {

    override suspend fun requestCreditReportOtp(featureFlowType: KycFeatureFlowType) =
        lendingKycRepository.requestCreditReportOtpV2(featureFlowType)
}
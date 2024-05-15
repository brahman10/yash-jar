package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestCreditReportOtpUseCase

internal class RequestCreditReportOtpUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
): RequestCreditReportOtpUseCase {

    override suspend fun requestCreditReportOtp(kycFeatureFlowType: KycFeatureFlowType) =
        lendingKycRepository.requestCreditReportOtp(kycFeatureFlowType)
}
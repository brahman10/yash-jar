package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestEmailOtpUseCase

internal class RequestEmailOtpUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
): RequestEmailOtpUseCase {

    override suspend fun requestEmailOtp(email: String, kycFeatureFlowType: KycFeatureFlowType) =
        lendingKycRepository.requestEmailOtp(email, kycFeatureFlowType)

}
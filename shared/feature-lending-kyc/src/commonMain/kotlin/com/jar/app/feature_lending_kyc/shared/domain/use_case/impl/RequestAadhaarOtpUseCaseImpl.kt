package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarOtpRequest
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestAadhaarOtpUseCase

internal class RequestAadhaarOtpUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
): RequestAadhaarOtpUseCase {

    override suspend fun requestAadhaarOtp(aadhaarOtpRequest: AadhaarOtpRequest, kycFeatureFlowType: KycFeatureFlowType) =
        lendingKycRepository.requestAadhaarOtp(aadhaarOtpRequest, kycFeatureFlowType)
}
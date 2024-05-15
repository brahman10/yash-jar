package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchVerifyAadhaarPanLinkageUseCase

internal class FetchVerifyAadhaarPanLinkageUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
) : FetchVerifyAadhaarPanLinkageUseCase {
    override suspend fun verifyAadhaarPanLinkage(kycFeatureFlowType: KycFeatureFlowType) = lendingKycRepository.verifyAadhaarPanLinkage(kycFeatureFlowType)
}
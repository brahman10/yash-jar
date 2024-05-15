package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.SaveAadhaarDetailsUseCase

internal class SaveAadhaarDetailsUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
) : SaveAadhaarDetailsUseCase {
    override suspend fun saveAadhaarDetails(kycFeatureFlowType: KycFeatureFlowType) =
        lendingKycRepository.saveAadhaarDetails(kycFeatureFlowType)
}
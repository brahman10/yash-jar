package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.model.DigilockerRedirectionData
import com.jar.app.feature_lending_kyc.shared.domain.use_case.UpdateDigiLockerRedirectionDataUseCase

internal class UpdateDigiLockerRedirectionDataUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
) : UpdateDigiLockerRedirectionDataUseCase {
    override suspend fun updateDigiLockerRedirection(kycFeatureFlowType: KycFeatureFlowType, redirectionData: DigilockerRedirectionData) =
        lendingKycRepository.updateDigiLockerRedirectData(kycFeatureFlowType, redirectionData)
}
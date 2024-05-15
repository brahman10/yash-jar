package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifySelfieUseCase

internal class VerifySelfieUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
) : VerifySelfieUseCase {

    override suspend fun verifySelfie(
        selfie: ByteArray,
        kycFeatureFlowType: KycFeatureFlowType,
        loanApplicationId: String?
    ) =
        lendingKycRepository.verifySelfie(selfie, kycFeatureFlowType, loanApplicationId)
}
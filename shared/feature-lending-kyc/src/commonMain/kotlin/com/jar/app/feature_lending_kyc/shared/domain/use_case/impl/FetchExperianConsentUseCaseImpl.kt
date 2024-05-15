package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchExperianConsentUseCase

internal class FetchExperianConsentUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
    ) : FetchExperianConsentUseCase {

    override suspend fun fetchExperianConsent(kycFeatureFlowType: KycFeatureFlowType) = lendingKycRepository.fetchExperianConsent(kycFeatureFlowType)
}
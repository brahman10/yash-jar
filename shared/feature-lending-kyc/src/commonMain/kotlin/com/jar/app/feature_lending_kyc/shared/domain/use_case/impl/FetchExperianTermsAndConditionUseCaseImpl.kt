package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchExperianTermsAndConditionUseCase

internal class FetchExperianTermsAndConditionUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
) : FetchExperianTermsAndConditionUseCase {
    override suspend fun fetchExperianTermsAndConditionUseCase(kycFeatureFlowType: KycFeatureFlowType) =
        lendingKycRepository.fetchExperianTermsAndConditions(kycFeatureFlowType)
}
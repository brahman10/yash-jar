package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchJarVerifiedUserPanUseCase

internal class FetchJarVerifiedUserPanUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
): FetchJarVerifiedUserPanUseCase {

    override suspend fun fetchJarVerifiedUserPan(kycFeatureFlowType: KycFeatureFlowType) =
        lendingKycRepository.fetchJarVerifiedUserPan(kycFeatureFlowType)
}
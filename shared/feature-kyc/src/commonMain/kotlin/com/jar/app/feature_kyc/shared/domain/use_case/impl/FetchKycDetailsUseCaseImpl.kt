package com.jar.app.feature_kyc.shared.domain.use_case.impl

import com.jar.app.feature_kyc.shared.data.repository.KycRepository
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycDetailsUseCase

internal class FetchKycDetailsUseCaseImpl constructor(
    private val kycRepository: KycRepository
): FetchKycDetailsUseCase {

    override suspend fun fetchKycDetails(kycContext: String?) = kycRepository.fetchKycDetails(kycContext = kycContext)

}
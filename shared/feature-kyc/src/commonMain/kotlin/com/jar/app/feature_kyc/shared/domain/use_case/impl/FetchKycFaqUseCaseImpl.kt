package com.jar.app.feature_kyc.shared.domain.use_case.impl

import com.jar.app.feature_kyc.shared.data.repository.KycRepository
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycFaqUseCase

internal class FetchKycFaqUseCaseImpl constructor(
    private val kycRepository: KycRepository
) : FetchKycFaqUseCase {

    override suspend fun fetchKycFaq(param: String) = kycRepository.fetchKycFaq(param)

}
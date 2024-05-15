package com.jar.app.feature_kyc.shared.domain.use_case.impl

import com.jar.app.feature_kyc.shared.data.repository.KycRepository
import com.jar.app.feature_kyc.shared.domain.model.ManualKycRequest
import com.jar.app.feature_kyc.shared.domain.use_case.PostManualKycRequestUseCase

internal class PostManualKycRequestUseCaseImpl constructor(
    private val kycRepository: KycRepository
): PostManualKycRequestUseCase {

    override suspend fun postManualKycRequest(manualKycRequest: ManualKycRequest,fetch: Boolean, kycContext: String?) =
        kycRepository.postManualKycRequest(manualKycRequest,fetch, kycContext = kycContext)

}
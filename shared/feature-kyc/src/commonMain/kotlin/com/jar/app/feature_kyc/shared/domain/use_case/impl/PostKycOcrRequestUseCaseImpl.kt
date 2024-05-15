package com.jar.app.feature_kyc.shared.domain.use_case.impl

import com.jar.app.feature_kyc.shared.api.use_case.PostKycOcrRequestUseCase
import com.jar.app.feature_kyc.shared.data.repository.KycRepository

internal class PostKycOcrRequestUseCaseImpl constructor(
    private val kycRepository: KycRepository
): PostKycOcrRequestUseCase {

    override suspend fun postKycOcrRequest(
        docType: String,
        byteArray: ByteArray,
        isKyc: Boolean
    ) = kycRepository.postKycOcrRequest(docType, byteArray, isKyc)

}
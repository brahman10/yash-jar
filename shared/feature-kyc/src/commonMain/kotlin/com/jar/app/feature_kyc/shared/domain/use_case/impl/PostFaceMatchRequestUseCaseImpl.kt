package com.jar.app.feature_kyc.shared.domain.use_case.impl

import com.jar.app.feature_kyc.shared.data.repository.KycRepository
import com.jar.app.feature_kyc.shared.api.use_case.PostFaceMatchRequestUseCase

internal class PostFaceMatchRequestUseCaseImpl constructor(
    private val kycRepository: KycRepository
): PostFaceMatchRequestUseCase {

    override suspend fun postFaceMatchRequest(docType: String, byteArray: ByteArray) = kycRepository.postFaceMatchRequest(docType, byteArray)

}
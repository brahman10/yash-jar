package com.jar.app.feature_kyc.shared.domain.use_case.impl

import com.jar.app.feature_kyc.shared.data.repository.KycRepository
import com.jar.app.feature_kyc.shared.domain.use_case.PostPanOcrRequestUseCase

internal class PostPanOcrRequestUseCaseImpl constructor(
    private val kycRepository: KycRepository
): PostPanOcrRequestUseCase {

    override suspend fun postKycOcrRequest(byteArray: ByteArray,)= kycRepository.postPanOcrRequest(byteArray)

}
package com.jar.app.feature_kyc.shared.domain.use_case.impl

import com.jar.app.feature_kyc.shared.data.repository.KycRepository
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycDocumentsListUseCase

internal class FetchKycDocumentsListUseCaseImpl constructor(
    private val kycRepository: KycRepository
): FetchKycDocumentsListUseCase {

    override suspend fun fetchKycDocumentsList() =
        kycRepository.fetchKycDocumentsList()

}
package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchKycAadhaarDetailsUseCase

internal class FetchKycAadhaarDetailsUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
): FetchKycAadhaarDetailsUseCase {

    override suspend fun fetchKycAadhaarDetails() =
        lendingKycRepository.fetchKycAadhaarDetails()

}
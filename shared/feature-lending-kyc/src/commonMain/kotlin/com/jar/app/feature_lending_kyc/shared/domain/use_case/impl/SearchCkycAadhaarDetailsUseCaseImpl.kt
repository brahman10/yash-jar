package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaarRequest
import com.jar.app.feature_lending_kyc.shared.domain.use_case.SearchCkycAadhaarDetailsUseCase

internal class SearchCkycAadhaarDetailsUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
) : SearchCkycAadhaarDetailsUseCase {

    override suspend fun searchCKycAadhaarDetails(kycAadhaarRequest: KycAadhaarRequest) =
        lendingKycRepository.searchCKycDetails(kycAadhaarRequest)

}
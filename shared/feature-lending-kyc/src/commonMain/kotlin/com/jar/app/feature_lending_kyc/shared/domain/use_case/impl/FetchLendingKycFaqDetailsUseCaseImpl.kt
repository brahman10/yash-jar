package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchLendingKycFaqDetailsUseCase

internal class FetchLendingKycFaqDetailsUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
): FetchLendingKycFaqDetailsUseCase {
    override suspend fun fetchLendingKycFaqDetails(param: String)= lendingKycRepository.fetchLendingKycFaqDetails(param)
}
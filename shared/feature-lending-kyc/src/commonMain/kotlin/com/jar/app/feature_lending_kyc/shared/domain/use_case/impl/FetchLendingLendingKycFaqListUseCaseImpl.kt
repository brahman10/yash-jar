package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchLendingKycFaqListUseCase

internal class FetchLendingLendingKycFaqListUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
): FetchLendingKycFaqListUseCase {
    override suspend fun fetchKycFaqList() = lendingKycRepository.fetchKycFaqList()
}
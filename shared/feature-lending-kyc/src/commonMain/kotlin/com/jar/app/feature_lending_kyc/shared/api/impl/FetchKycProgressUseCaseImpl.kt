package com.jar.app.feature_lending_kyc.shared.api.impl

import com.jar.app.feature_lending_kyc.shared.api.use_case.FetchKycProgressUseCase
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository

internal class FetchKycProgressUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
) : FetchKycProgressUseCase {

    override suspend fun fetchKycProgress() =
        lendingKycRepository.fetchKycProgress()
}
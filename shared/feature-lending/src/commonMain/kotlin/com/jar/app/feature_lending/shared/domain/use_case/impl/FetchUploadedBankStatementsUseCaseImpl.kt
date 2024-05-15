package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchUploadedBankStatementsUseCase

internal class FetchUploadedBankStatementsUseCaseImpl(
    private val lendingRepository: LendingRepository
) : FetchUploadedBankStatementsUseCase {
    override suspend fun fetchUploadedBankStatement() = lendingRepository.getBankStatement()
}
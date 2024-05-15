package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.UpdatePasswordRequest
import com.jar.app.feature_lending.shared.domain.use_case.UpdateBankStatementPasswordUseCase

internal class UpdateBankStatementPasswordUseCaseImpl(
    private val lendingRepository: LendingRepository
) : UpdateBankStatementPasswordUseCase {
    override suspend fun updateBankStatementPassword(
        updatePasswordRequest: UpdatePasswordRequest
    ) = lendingRepository.updateBankStatementPassword(updatePasswordRequest)
}
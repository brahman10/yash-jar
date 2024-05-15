package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.model.v2.BankAccount
import com.jar.app.feature_lending.shared.domain.use_case.UpdateBankDetailUseCase

internal class UpdateBankDetailUseCaseImpl(
    private val lendingRepository: LendingRepository
) : UpdateBankDetailUseCase {
    override suspend fun updateBankAccountDetails(bankAccount: BankAccount) =
        lendingRepository.updateBankDetails(bankAccount)
}
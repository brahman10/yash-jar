package com.jar.app.feature_savings_common.shared.domain.use_case.impl

import com.jar.app.feature_savings_common.shared.data.repository.SavingsCommonRepository
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase

internal class FetchUserSavingsDetailsUseCaseImpl constructor(
    private val savingsCommonRepository: SavingsCommonRepository
) : FetchUserSavingsDetailsUseCase {

    override suspend fun fetchSavingsDetails(savingsType: SavingsType) =
        savingsCommonRepository.fetchSavingsDetails(savingsType)
}
package com.jar.app.feature_savings_common.shared.domain.use_case.impl

import com.jar.app.feature_savings_common.shared.data.repository.SavingsCommonRepository
import com.jar.app.feature_savings_common.shared.domain.model.SavingsSubscriptionType
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchSavingsSetupInfoUseCase


internal class FetchSavingsSetupInfoUseCaseImpl constructor(
    private val savingsCommonRepository: SavingsCommonRepository
) : FetchSavingsSetupInfoUseCase {

    override suspend fun fetchSavingSetupInfo(
        savingsSubscriptionType: SavingsSubscriptionType,
        savingsType: SavingsType,
        savingStateContext: String
    ) = savingsCommonRepository.fetchSavingSetupInfo(
        savingsSubscriptionType,
        savingsType,
        savingStateContext
    )
}
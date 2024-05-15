package com.jar.app.feature_sell_gold.shared.domain.use_cases.impl

import com.jar.app.feature_sell_gold.shared.domain.repository.IWithdrawalRepository
import com.jar.app.feature_sell_gold.shared.domain.use_cases.FetchDrawerDetailsUseCase

internal class FetchDrawerDetailsUseCaseImpl constructor(
    private val repository: IWithdrawalRepository
) : FetchDrawerDetailsUseCase {
    override suspend operator fun invoke() = repository.fetchDrawerDetails()
}
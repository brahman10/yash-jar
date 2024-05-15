package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUpdateDailySavingAmountInfoUseCase

internal class FetchUpdateDailySavingAmountInfoUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchUpdateDailySavingAmountInfoUseCase {

    override suspend fun fetchUpdateDailySavingAmountInfo(includeView: Boolean) =
        homeRepository.fetchUpdateDailySavingAmountInfo(includeView)

}
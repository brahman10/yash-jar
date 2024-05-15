package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchDailySavingsCardUseCase

internal class FetchDailySavingsCardUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchDailySavingsCardUseCase {

    override suspend fun fetchDSCardData() =
        homeRepository.fetchDSCardData()
}
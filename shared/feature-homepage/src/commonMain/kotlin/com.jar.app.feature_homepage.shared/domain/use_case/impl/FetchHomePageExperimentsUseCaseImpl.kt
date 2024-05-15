package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomePageExperimentsUseCase

internal class FetchHomePageExperimentsUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchHomePageExperimentsUseCase {

    override suspend fun fetchHomePageExperiments() = homeRepository.fetchHomePageExperiments()

}
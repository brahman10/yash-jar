package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchAppWalkthroughUseCase

internal class FetchAppWalkthroughUseCaseImpl constructor(private val homeRepository: HomeRepository):
    FetchAppWalkthroughUseCase {
    override suspend fun fetchAppWalkthrough() = homeRepository.fetchAppWalkthrough()

}
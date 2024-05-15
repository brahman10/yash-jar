package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeFeedActionsUseCase

internal class FetchHomeFeedActionsUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchHomeFeedActionsUseCase {

    override suspend fun fetchHomeFeedActions() = homeRepository.fetchHomeFeedActions()
}
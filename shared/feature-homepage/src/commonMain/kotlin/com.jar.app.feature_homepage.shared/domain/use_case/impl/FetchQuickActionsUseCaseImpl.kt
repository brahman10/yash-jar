package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchQuickActionsUseCase

internal class FetchQuickActionsUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchQuickActionsUseCase {
    override suspend fun fetchQuickActions(type : String) =
        homeRepository.fetchQuickActions(type)
}
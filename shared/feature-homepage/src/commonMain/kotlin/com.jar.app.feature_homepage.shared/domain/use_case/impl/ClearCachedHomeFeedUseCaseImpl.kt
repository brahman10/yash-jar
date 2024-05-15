package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.ClearCachedHomeFeedUseCase

internal class ClearCachedHomeFeedUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : ClearCachedHomeFeedUseCase {
    override suspend fun clearAllHomeFeedData() {
        homeRepository.clearAllData()
    }
}
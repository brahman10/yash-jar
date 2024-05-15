package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeFeedImagesUseCase

internal class FetchHomeFeedImagesUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchHomeFeedImagesUseCase {
    override suspend fun fetchHomeFeedImages() =
        homeRepository.fetchHomeFeedImages()
}
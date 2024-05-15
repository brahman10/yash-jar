package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHelpVideosUseCase

internal class FetchHelpVideosUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchHelpVideosUseCase {
    override suspend fun fetchHelpVideos(language: String, includeView: Boolean) =
        homeRepository.fetchHelpVideos(language, includeView)
}
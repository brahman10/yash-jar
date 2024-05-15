package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeScreenBottomSheetPromptUseCase

internal class FetchHomeScreenBottomSheetPromptUseCaseImpl constructor(
    private val homeRepository: HomeRepository
): FetchHomeScreenBottomSheetPromptUseCase {
    override suspend fun fetchHomeScreenBottomSheetPrompt() =
        homeRepository.fetchHomeScreenBottomSheetPrompt()
}
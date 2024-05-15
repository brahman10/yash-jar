package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.UpdateAppWalkthroughCompletedUseCase

internal class UpdateAppWalkthroughCompletedUseCaseImpl constructor(private val homeRepository: HomeRepository) :
    UpdateAppWalkthroughCompletedUseCase {
    override suspend fun updateAppWalkthroughCompleted() =
        homeRepository.updateAppWalkthroughCompleted()
}
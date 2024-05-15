package com.jar.app.feature_post_setup.domain.use_case.impl

import com.jar.app.feature_post_setup.data.repository.PostSetupRepository
import com.jar.app.feature_post_setup.domain.use_case.FetchPostSetupQuickActionsUseCase

internal class FetchPostSetupQuickActionsUseCaseImpl constructor(private val postSetupRepository: PostSetupRepository) :
    FetchPostSetupQuickActionsUseCase {
    override suspend fun fetchPostSetupQuickActions() =
        postSetupRepository.fetchPostSetupQuickActions()
}
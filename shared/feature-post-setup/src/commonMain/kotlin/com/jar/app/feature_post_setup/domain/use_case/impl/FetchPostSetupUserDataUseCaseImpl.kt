package com.jar.app.feature_post_setup.domain.use_case.impl

import com.jar.app.feature_post_setup.data.repository.PostSetupRepository
import com.jar.app.feature_post_setup.domain.use_case.FetchPostSetupUserDataUseCase

internal class FetchPostSetupUserDataUseCaseImpl constructor(
    private val postSetupRepository: PostSetupRepository
) : FetchPostSetupUserDataUseCase {
    override suspend fun fetchPostSetupUserData() = postSetupRepository.fetchPostSetupUserData()
}
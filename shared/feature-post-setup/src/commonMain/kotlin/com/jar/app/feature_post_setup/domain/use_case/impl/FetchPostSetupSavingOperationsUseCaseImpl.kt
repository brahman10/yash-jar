package com.jar.app.feature_post_setup.domain.use_case.impl

import com.jar.app.feature_post_setup.data.repository.PostSetupRepository
import com.jar.app.feature_post_setup.domain.use_case.FetchPostSetupSavingOperationsUseCase

class FetchPostSetupSavingOperationsUseCaseImpl constructor(private val postSetupRepository: PostSetupRepository) :
    FetchPostSetupSavingOperationsUseCase {
    override suspend fun fetchPostSetupSavingOperations() =
        postSetupRepository.fetchPostSetupSavingOperations()
}
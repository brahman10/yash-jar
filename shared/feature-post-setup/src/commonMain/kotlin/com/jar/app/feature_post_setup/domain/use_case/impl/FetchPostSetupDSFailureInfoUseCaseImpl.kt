package com.jar.app.feature_post_setup.domain.use_case.impl

import com.jar.app.feature_post_setup.data.repository.PostSetupRepository
import com.jar.app.feature_post_setup.domain.use_case.FetchPostSetupDSFailureInfoUseCase

class FetchPostSetupDSFailureInfoUseCaseImpl constructor(private val postSetupRepository: PostSetupRepository) :
    FetchPostSetupDSFailureInfoUseCase {
    override suspend fun fetchPostSetupFailureInfo() =
        postSetupRepository.fetchPostSetupFailureInfo()
}
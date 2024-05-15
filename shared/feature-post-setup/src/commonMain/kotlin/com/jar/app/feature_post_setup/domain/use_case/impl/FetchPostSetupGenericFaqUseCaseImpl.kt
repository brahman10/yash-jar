package com.jar.app.feature_post_setup.domain.use_case.impl

import com.jar.app.feature_post_setup.data.repository.PostSetupRepository
import com.jar.app.feature_post_setup.domain.use_case.FetchPostSetupGenericFaqUseCase

internal class FetchPostSetupGenericFaqUseCaseImpl constructor(
    private val postSetupRepository: PostSetupRepository
) : FetchPostSetupGenericFaqUseCase {

    override suspend fun fetchPostSetupFaq() = postSetupRepository.fetchPostSetupFaq()

}
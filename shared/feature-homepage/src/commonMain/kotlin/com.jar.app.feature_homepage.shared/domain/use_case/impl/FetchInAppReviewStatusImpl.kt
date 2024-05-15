package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchInAppReviewStatusUseCase

internal class FetchInAppReviewStatusImpl constructor(
    private val homeRepository: HomeRepository
) : FetchInAppReviewStatusUseCase {

    override suspend fun fetchInAppReviewStatus()=
        homeRepository.fetchInAppReviewStatus()
}
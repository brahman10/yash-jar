package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.model.FeatureFlag
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFeatureViewUseCase

internal class FetchFeatureViewUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchFeatureViewUseCase {

    override suspend fun fetchFeature(featureFlag: com.jar.app.feature_homepage.shared.domain.model.FeatureFlag) =
        homeRepository.fetchFeature(featureFlag)
}
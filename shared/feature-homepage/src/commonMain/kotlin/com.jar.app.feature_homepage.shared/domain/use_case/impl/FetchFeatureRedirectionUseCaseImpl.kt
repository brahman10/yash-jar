package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFeatureRedirectionUseCase

class FetchFeatureRedirectionUseCaseImpl constructor(private val homeRepository: HomeRepository) :
    FetchFeatureRedirectionUseCase {
    override suspend fun fetchFeatureRedirectionData() = homeRepository.fetchFeatureRedirectionData()
}
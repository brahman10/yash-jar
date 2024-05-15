package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchPartnerBannerUseCase

internal class FetchPartnerBannerUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchPartnerBannerUseCase {

    override suspend fun fetchPartnerBanners(includeView: Boolean) =
        homeRepository.fetchPartnerBanners(includeView)
}
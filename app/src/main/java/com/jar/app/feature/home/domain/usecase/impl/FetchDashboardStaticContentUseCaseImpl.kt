package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.feature.home.domain.usecase.FetchDashboardStaticContentUseCase
import javax.inject.Inject

internal class FetchDashboardStaticContentUseCaseImpl @Inject constructor(
    private val homeRepository: HomeRepository,
) : FetchDashboardStaticContentUseCase {

    override suspend fun fetchDashboardStaticContent(staticContentType: StaticContentType) =
        homeRepository.fetchDashboardStaticContent(staticContentType)

}
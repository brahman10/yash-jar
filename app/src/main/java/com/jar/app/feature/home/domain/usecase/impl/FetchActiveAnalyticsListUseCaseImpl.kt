package com.jar.app.feature.home.domain.usecase.impl

import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.home.domain.usecase.FetchActiveAnalyticsListUseCase
import javax.inject.Inject

internal class FetchActiveAnalyticsListUseCaseImpl @Inject constructor(
    private val homeRepository: HomeRepository,
) : FetchActiveAnalyticsListUseCase {

    override suspend fun fetchActiveAnalyticsList() = homeRepository.fetchActiveAnalyticsList()

}
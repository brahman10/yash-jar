package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUpcomingPreNotificationUseCase

internal class FetchUpcomingPreNotificationUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : FetchUpcomingPreNotificationUseCase {
    override suspend fun fetchUpcomingPreNotification(includeView: Boolean) =
        homeRepository.fetchUpcomingPreNotification(includeView)
}
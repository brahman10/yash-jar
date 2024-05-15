package com.jar.app.feature_homepage.shared.domain.use_case.impl

import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.use_case.DismissUpcomingPreNotificationUseCase

internal class DismissUpcomingPreNotificationUseCaseImpl constructor(
    private val homeRepository: HomeRepository
) : DismissUpcomingPreNotificationUseCase {
    override suspend fun dismissUpcomingPreNotification(dismissalType: String,preNotificationIds: List<String>) =
        homeRepository.dismissUpcomingPreNotification(dismissalType,preNotificationIds)
}
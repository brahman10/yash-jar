package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.ScheduleBankUptimeNotificationUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class ScheduleBankUptimeNotificationUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : ScheduleBankUptimeNotificationUseCase {
    override suspend fun scheduleBankUptimeNotification(fipId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>> =
        lendingRepository.scheduleBankUptimeNotification(fipId)
}
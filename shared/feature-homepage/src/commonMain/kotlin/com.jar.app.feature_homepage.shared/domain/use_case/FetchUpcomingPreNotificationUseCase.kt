package com.jar.app.feature_homepage.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_homepage.shared.domain.model.PreNotifyAutopay
import kotlinx.coroutines.flow.Flow

interface FetchUpcomingPreNotificationUseCase {

    suspend fun fetchUpcomingPreNotification(includeView: Boolean = false): Flow<RestClientResult<ApiResponseWrapper<PreNotifyAutopay>>>

}
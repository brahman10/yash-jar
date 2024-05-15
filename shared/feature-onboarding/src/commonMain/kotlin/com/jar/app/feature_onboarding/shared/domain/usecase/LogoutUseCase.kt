package com.jar.app.feature_onboarding.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface LogoutUseCase {

    suspend fun logout(deviceId: String?, refreshToken: String?): Flow<RestClientResult<ApiResponseWrapper<String?>>>

}
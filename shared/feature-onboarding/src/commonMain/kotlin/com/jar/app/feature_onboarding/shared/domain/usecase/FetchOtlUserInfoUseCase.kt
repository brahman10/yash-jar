package com.jar.app.feature_onboarding.shared.domain.usecase

import com.jar.app.core_base.domain.model.UserResponseData
import com.jar.app.feature_user_api.domain.model.OTLLoginRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchOtlUserInfoUseCase {
    suspend fun fetchOtlUserInfo(
        otlLoginRequest: OTLLoginRequest
    ): Flow<RestClientResult<ApiResponseWrapper<UserResponseData?>>>
}
package com.jar.app.feature_onboarding.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_onboarding.shared.domain.model.TruecallerLoginRequest
import com.jar.app.core_base.domain.model.UserResponseData
import kotlinx.coroutines.flow.Flow

interface TruecallerLoginUseCase {

    suspend fun loginViaTruecaller(truecallerLoginRequest: com.jar.app.feature_onboarding.shared.domain.model.TruecallerLoginRequest): Flow<RestClientResult<ApiResponseWrapper<UserResponseData>>>

}
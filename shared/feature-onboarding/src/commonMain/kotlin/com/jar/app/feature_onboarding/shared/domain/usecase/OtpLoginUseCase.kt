package com.jar.app.feature_onboarding.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_user_api.domain.model.OTPLoginRequest
import com.jar.app.core_base.domain.model.UserResponseData
import com.jar.app.core_base.data.dto.UserResponseDTO
import kotlinx.coroutines.flow.Flow

interface OtpLoginUseCase {

    suspend fun loginViaOtp(otpLoginRequest: OTPLoginRequest): Flow<RestClientResult<ApiResponseWrapper<UserResponseDTO?>>>

}
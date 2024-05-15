package com.jar.app.feature_onboarding.shared.domain.usecase

import com.jar.app.feature_user_api.domain.model.RequestOtpData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface RequestOtpUseCase {

    suspend fun requestOtp(
        hasExperianConsent: Boolean,
        phoneNumber: String,
        countryCode: String
    ): Flow<RestClientResult<ApiResponseWrapper<RequestOtpData?>>>

    suspend fun requestOtpViaCall(
        phoneNumber: String,
        countryCode: String
    ): Flow<RestClientResult<ApiResponseWrapper<RequestOtpData?>>>

}
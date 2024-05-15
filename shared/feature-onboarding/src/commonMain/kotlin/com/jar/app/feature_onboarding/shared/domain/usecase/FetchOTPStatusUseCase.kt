package com.jar.app.feature_onboarding.shared.domain.usecase

import com.jar.app.feature_onboarding.shared.domain.model.OtpStatusResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchOTPStatusUseCase {

    suspend fun fetchOTPStatus(
        phoneNumber: String,
        countryCode: String
    ): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_onboarding.shared.domain.model.OtpStatusResponse>>>

}
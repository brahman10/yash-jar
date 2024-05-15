package com.jar.app.feature_onboarding.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_onboarding.shared.domain.model.GetPhoneRequest
import com.jar.app.feature_onboarding.shared.domain.model.PhoneNumberResponse
import kotlinx.coroutines.flow.Flow

interface IGetPhoneByDeviceUseCase {
    suspend fun getPhoneByDevice(getPhoneRequest: com.jar.app.feature_onboarding.shared.domain.model.GetPhoneRequest):
            Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_onboarding.shared.domain.model.PhoneNumberResponse>>>
}
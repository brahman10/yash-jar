package com.jar.app.feature_user_api.domain.use_case

import com.jar.app.feature_user_api.domain.model.PhoneNumberWithCountryCode
import com.jar.app.feature_user_api.domain.model.RequestOtpData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UpdateUserPhoneNumberUseCase {

    suspend fun updateUserPhoneNumber(phoneNumberWithCountryCode: PhoneNumberWithCountryCode): Flow<RestClientResult<ApiResponseWrapper<RequestOtpData>>>

}
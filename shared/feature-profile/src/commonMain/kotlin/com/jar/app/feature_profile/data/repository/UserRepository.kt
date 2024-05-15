package com.jar.app.feature_profile.data.repository

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_profile.domain.model.ProfileStaticData
import com.jar.app.feature_user_api.domain.model.RequestOtpData
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface UserRepository : BaseRepository {

    suspend fun requestOTP(
        phoneNumber: String,
        countryCode: String
    ): Flow<RestClientResult<ApiResponseWrapper<RequestOtpData?>>>

    suspend fun requestOTPViaCall(
        phoneNumber: String,
        countryCode: String
    ): Flow<RestClientResult<ApiResponseWrapper<RequestOtpData?>>>

    suspend fun fetchDashboardStaticContent(staticContentType: BaseConstants.StaticContentType): Flow<RestClientResult<ApiResponseWrapper<ProfileStaticData>>>

}
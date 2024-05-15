package com.jar.app.feature_profile.data.network

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_profile.domain.model.ProfileStaticData
import com.jar.app.feature_profile.util.Constants
import com.jar.app.feature_user_api.domain.model.RequestOtpData
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

internal class UserDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun requestOTP(phoneNumber: String, countryCode: String) =
        getResult<ApiResponseWrapper<RequestOtpData?>> {
            client.post {
                url(Constants.Endpoints.REQUEST_OTP)
                parameter("phoneNumber", phoneNumber)
                parameter("countryCode", countryCode)
            }
        }

    suspend fun requestOTPViaCall(phoneNumber: String, countryCode: String) =
        getResult<ApiResponseWrapper<RequestOtpData?>> {
            client.get {
                url(Constants.Endpoints.REQUEST_OTP_VIA_CALL)
                parameter("phoneNumber", phoneNumber)
                parameter("countryCode", countryCode)
            }
        }

    suspend fun fetchDashboardStaticContent(staticContentType: BaseConstants.StaticContentType) =
        getResult<ApiResponseWrapper<ProfileStaticData>> {
            client.get {
                url(Constants.Endpoints.FETCH_DASHBOARD_STATIC_CONTENT)
                parameter("contentType", staticContentType.name)
            }
        }
}
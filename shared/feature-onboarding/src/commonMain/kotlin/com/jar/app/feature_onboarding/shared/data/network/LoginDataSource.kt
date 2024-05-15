package com.jar.app.feature_onboarding.shared.data.network

import com.jar.app.core_base.domain.model.UserResponseData
import com.jar.app.core_base.data.dto.UserResponseDTO
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_onboarding.shared.domain.model.EligibleMandateApps
import com.jar.app.feature_onboarding.shared.domain.model.ExperianTCResponse
import com.jar.app.feature_onboarding.shared.domain.model.FaqStaticData
import com.jar.app.feature_onboarding.shared.domain.model.GetPhoneRequest
import com.jar.app.feature_onboarding.shared.domain.model.LanguageList
import com.jar.app.feature_onboarding.shared.domain.model.OnboardingStoryData
import com.jar.app.feature_onboarding.shared.domain.model.OtpStatusResponse
import com.jar.app.feature_onboarding.shared.domain.model.PhoneNumberResponse
import com.jar.app.feature_onboarding.shared.domain.model.SavingGoalPostRequest
import com.jar.app.feature_onboarding.shared.domain.model.SavingGoalsResponse
import com.jar.app.feature_onboarding.shared.domain.model.SavingGoalsV2Response
import com.jar.app.feature_onboarding.shared.domain.model.TruecallerLoginRequest
import com.jar.app.feature_onboarding.shared.domain.model.UserSavingPreferences
import com.jar.app.feature_onboarding.shared.util.OnboardingConstants.Endpoints
import com.jar.app.feature_user_api.domain.model.OTLLoginRequest
import com.jar.app.feature_user_api.domain.model.OTPLoginRequest
import com.jar.app.feature_user_api.domain.model.RequestOtpData
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class LoginDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun loginViaTruecaller(truecallerLoginRequest: TruecallerLoginRequest) =
        getResult<ApiResponseWrapper<UserResponseData>> {
            client.post {
                url(Endpoints.LOGIN_VIA_TRUECALLER)
                setBody(truecallerLoginRequest)
            }
        }

    suspend fun requestOTP(hasExperianConsent: Boolean, phoneNumber: String, countryCode: String) =
        getResult<ApiResponseWrapper<RequestOtpData?>> {
            client.post {
                url(Endpoints.REQUEST_OTP)
                parameter("hasExperianConsent", hasExperianConsent)
                parameter("phoneNumber", phoneNumber)
                parameter("countryCode", countryCode)
            }
        }

    suspend fun requestOTPViaCall(phoneNumber: String, countryCode: String) =
        getResult<ApiResponseWrapper<RequestOtpData?>> {
            client.get {
                url(Endpoints.REQUEST_OTP_VIA_CALL)
                parameter("phoneNumber", phoneNumber)
                parameter("countryCode", countryCode)
            }
        }

    suspend fun verifyOtp(otpLoginRequest: OTPLoginRequest) =
        getResult<ApiResponseWrapper<UserResponseDTO?>> {
            client.post {
                url(Endpoints.VERIFY_OTP)
                setBody(otpLoginRequest)
            }
        }

    suspend fun logout(deviceId: String?, refreshToken: String?) =
        getResult<ApiResponseWrapper<String?>> {
            client.post {
                url(Endpoints.LOGOUT)
                if (deviceId.isNullOrBlank().not())
                    parameter("deviceId", deviceId)
                if (refreshToken.isNullOrBlank().not())
                    parameter("refreshToken", refreshToken)
            }
        }

    suspend fun getPhoneByDevice(getPhoneRequest: GetPhoneRequest) =
        getResult<ApiResponseWrapper<PhoneNumberResponse>> {
            client.post {
                url(Endpoints.FETCH_PHONE_NUMBER_FOR_DEVICE)
                setBody(getPhoneRequest)
            }
        }

    suspend fun fetchOTPStatus(phoneNumber: String, countryCode: String) =
        getResult<ApiResponseWrapper<OtpStatusResponse>> {
            client.get {
                url(Endpoints.FETCH_OTP_STATUS)
                parameter("phoneNumber", phoneNumber)
                parameter("countryCode", countryCode)
            }
        }

    suspend fun fetchSavingGoals() =
        getResult<ApiResponseWrapper<SavingGoalsResponse>> {
            client.get {
                url(Endpoints.FETCH_SAVINGS_GOAL)
                parameter("contentType", "SAVINGS_GOAL")
            }
        }

    suspend fun postSavingGoals(reasonForSavingsSelectedList: SavingGoalPostRequest) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.POST_SAVING_GOAL)
                setBody(reasonForSavingsSelectedList)
            }
        }

    suspend fun getUserSavingPreferences() =
        getResult<ApiResponseWrapper<UserSavingPreferences?>> {
            client.get {
                url(Endpoints.FETCH_USER_SAVING_PREFERENCE)
            }
        }

    suspend fun getExperianTC() =
        getResult<ApiResponseWrapper<ExperianTCResponse>> {
            client.get {
                url(Endpoints.FETCH_EXPERIAN_TNC)
                parameter("contentType", "EXPERIAN_TNC")
            }
        }

    suspend fun fetchOnboardingStories() =
        getResult<ApiResponseWrapper<OnboardingStoryData>> {
            client.get {
                url(Endpoints.FETCH_ONBOARDING_STORIES)
                parameter("contentType", "ONBOARDING_STORIES")
            }
        }

    suspend fun fetchSupportedLanguages() =
        getResult<ApiResponseWrapper<LanguageList>> {
            client.get {
                url(Endpoints.FETCH_SUPPORTED_LANGUAGES)
            }
        }


    suspend fun fetchDashboardStaticContent(staticContentType: BaseConstants.StaticContentType) =
        getResult<ApiResponseWrapper<FaqStaticData>> {
            client.get {
                url(Endpoints.FETCH_DASHBOARD_STATIC_CONTENT)
                parameter("contentType", staticContentType.name)
            }
        }

    suspend fun fetchSavingGoalsV2() =
        getResult<ApiResponseWrapper<SavingGoalsV2Response>> {
            client.get {
                url(Endpoints.FETCH_SAVINGS_GOAL)
                parameter("contentType", "SAVINGS_GOAL_REVAMP")
            }
        }


    suspend fun fetchOtlUserInfo(otlLoginRequest: OTLLoginRequest) =
        getResult<ApiResponseWrapper<UserResponseData?>> {
            client.post {
                url(Endpoints.VERIFY_OTL)
                setBody(otlLoginRequest)
            }
        }
}
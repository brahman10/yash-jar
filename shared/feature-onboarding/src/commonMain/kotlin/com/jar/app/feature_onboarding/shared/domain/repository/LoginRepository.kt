package com.jar.app.feature_onboarding.shared.domain.repository

import com.jar.app.core_base.data.dto.UserResponseDTO
import com.jar.app.core_base.domain.model.UserResponseData
import com.jar.app.core_base.util.BaseConstants
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
import com.jar.app.feature_onboarding.shared.domain.model.UserSavingPreferences
import com.jar.app.feature_user_api.domain.model.OTLLoginRequest
import com.jar.app.feature_user_api.domain.model.OTPLoginRequest
import com.jar.app.feature_user_api.domain.model.RequestOtpData
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface LoginRepository : BaseRepository {

    suspend fun loginViaTruecaller(truecallerLoginRequest: com.jar.app.feature_onboarding.shared.domain.model.TruecallerLoginRequest): Flow<RestClientResult<ApiResponseWrapper<UserResponseData>>>

    suspend fun requestOTP(
        hasExperianConsent: Boolean,
        phoneNumber: String,
        countryCode: String
    ): Flow<RestClientResult<ApiResponseWrapper<RequestOtpData?>>>

    suspend fun requestOTPViaCall(
        phoneNumber: String,
        countryCode: String
    ): Flow<RestClientResult<ApiResponseWrapper<RequestOtpData?>>>

    suspend fun loginViaOtp(otpLoginRequest: OTPLoginRequest): Flow<RestClientResult<ApiResponseWrapper<UserResponseDTO?>>>

    suspend fun logout(
        deviceId: String?,
        refreshToken: String?
    ): Flow<RestClientResult<ApiResponseWrapper<String?>>>

    suspend fun getPhoneByDevice(getPhoneRequest: GetPhoneRequest): Flow<RestClientResult<ApiResponseWrapper<PhoneNumberResponse>>>

    suspend fun fetchOTPStatus(
        phoneNumber: String,
        countryCode: String
    ): Flow<RestClientResult<ApiResponseWrapper<OtpStatusResponse>>>

    suspend fun fetchSavingGoals(): Flow<RestClientResult<ApiResponseWrapper<SavingGoalsResponse>>>

    suspend fun postSavingGoals(
        reasonForSavingsSelectedList: SavingGoalPostRequest
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun getUserSavingPreferences(): Flow<RestClientResult<ApiResponseWrapper<UserSavingPreferences?>>>

    suspend fun fetchOnboardingStories(): Flow<RestClientResult<ApiResponseWrapper<OnboardingStoryData>>>

    suspend fun getExperianTC(): Flow<RestClientResult<ApiResponseWrapper<ExperianTCResponse>>>

    suspend fun fetchSupportedLanguages(): Flow<RestClientResult<ApiResponseWrapper<LanguageList>>>

    suspend fun fetchDashboardStaticContent(staticContentType: BaseConstants.StaticContentType): Flow<RestClientResult<ApiResponseWrapper<FaqStaticData>>>

    suspend fun fetchSavingGoalsV2(): Flow<RestClientResult<ApiResponseWrapper<SavingGoalsV2Response>>>

    suspend fun fetchOtlUserInfo(otlLoginRequest: OTLLoginRequest): Flow<RestClientResult<ApiResponseWrapper<UserResponseData?>>>
}

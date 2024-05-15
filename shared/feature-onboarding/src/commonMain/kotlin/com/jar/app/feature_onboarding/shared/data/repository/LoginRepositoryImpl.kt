package com.jar.app.feature_onboarding.shared.data.repository

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_onboarding.shared.data.network.LoginDataSource
import com.jar.app.feature_onboarding.shared.domain.model.EligibleMandateApps
import com.jar.app.feature_onboarding.shared.domain.model.GetPhoneRequest
import com.jar.app.feature_onboarding.shared.domain.model.SavingGoalPostRequest
import com.jar.app.feature_onboarding.shared.domain.model.TruecallerLoginRequest
import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_user_api.domain.model.OTLLoginRequest
import com.jar.app.feature_user_api.domain.model.OTPLoginRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class LoginRepositoryImpl constructor(private val loginDataSource: LoginDataSource) :
    LoginRepository {

    override suspend fun loginViaTruecaller(truecallerLoginRequest: TruecallerLoginRequest) =
        getFlowResult {
            loginDataSource.loginViaTruecaller(truecallerLoginRequest)
        }

    override suspend fun requestOTP(
        hasExperianConsent: Boolean,
        phoneNumber: String,
        countryCode: String
    ) = getFlowResult {
        loginDataSource.requestOTP(hasExperianConsent, phoneNumber, countryCode)
    }

    override suspend fun requestOTPViaCall(phoneNumber: String, countryCode: String) =
        getFlowResult {
            loginDataSource.requestOTPViaCall(phoneNumber, countryCode)
        }

    override suspend fun loginViaOtp(otpLoginRequest: OTPLoginRequest) = getFlowResult {
        loginDataSource.verifyOtp(otpLoginRequest)
    }

    override suspend fun logout(deviceId: String?, refreshToken: String?) = getFlowResult {
        loginDataSource.logout(deviceId, refreshToken)
    }

    override suspend fun getPhoneByDevice(getPhoneRequest: GetPhoneRequest) =
        getFlowResult { loginDataSource.getPhoneByDevice(getPhoneRequest) }

    override suspend fun fetchOTPStatus(phoneNumber: String, countryCode: String) =
        getFlowResult { loginDataSource.fetchOTPStatus(phoneNumber, countryCode) }


    override suspend fun fetchSavingGoals() = getFlowResult {
        loginDataSource.fetchSavingGoals()
    }

    override suspend fun postSavingGoals(reasonForSavingsSelectedList: SavingGoalPostRequest) =
        getFlowResult {
            loginDataSource.postSavingGoals(reasonForSavingsSelectedList)
        }

    override suspend fun getUserSavingPreferences() =
        getFlowResult {
            loginDataSource.getUserSavingPreferences()
        }

    override suspend fun fetchOnboardingStories() = getFlowResult {
        loginDataSource.fetchOnboardingStories()
    }

    override suspend fun getExperianTC() =
        getFlowResult { loginDataSource.getExperianTC() }

    override suspend fun fetchSupportedLanguages() =
        getFlowResult { loginDataSource.fetchSupportedLanguages() }

    override suspend fun fetchDashboardStaticContent(staticContentType: BaseConstants.StaticContentType) =
        getFlowResult { loginDataSource.fetchDashboardStaticContent(staticContentType) }

    override suspend fun fetchSavingGoalsV2() =
        getFlowResult { loginDataSource.fetchSavingGoalsV2() }

    override suspend fun fetchOtlUserInfo(otlLoginRequest: OTLLoginRequest) =
        getFlowResult { loginDataSource.fetchOtlUserInfo(otlLoginRequest) }

}
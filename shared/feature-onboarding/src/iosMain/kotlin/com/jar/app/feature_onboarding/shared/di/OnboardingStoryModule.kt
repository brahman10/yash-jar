package com.jar.app.feature_onboarding.shared.di

import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_homepage.shared.domain.use_case.ClearCachedHomeFeedUseCase
import com.jar.app.feature_onboarding.shared.data.network.LoginDataSource
import com.jar.app.feature_onboarding.shared.data.repository.LoginRepositoryImpl
import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchExperianTCUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchFaqStaticDataUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchOTPStatusUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchOnboardingStoriesUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSavingGoalsUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSavingGoalsV2UseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSupportedLanguagesUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchUserSavingPreferencesUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.IGetPhoneByDeviceUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.LogoutUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.OtpLoginUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.PostSavingGoalsUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.RequestOtpUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.TruecallerLoginUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.FetchExperianTCUseCaseImpl
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.FetchFaqStaticDataUseCaseImpl
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.FetchOTPStatusUseCaseImpl
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.FetchOnboardingStoriesUseCaseImpl
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.FetchSavingGoalsUseCaseImpl
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.FetchSavingGoalsV2UseCaseImpl
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.FetchSupportedLanguagesUseCaseImpl
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.FetchUserSavingPreferencesUseCaseImpl
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.GetPhoneByDeviceUseCaseImpl
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.LogoutUseCaseImpl
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.OtpLoginUseCaseImpl
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.PostSavingGoalsUseCaseImpl
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.RequestOtpUseCaseImpl
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.TruecallerLoginUseCaseImpl
import io.ktor.client.HttpClient

class OnboardingStoryModule(
    httpClient: HttpClient,
    clearCachedHomeFeedUseCase: ClearCachedHomeFeedUseCase,
    prefsApi: PrefsApi
) {

    private val loginDataSource by lazy {
        LoginDataSource(httpClient)
    }

    private val loginRepository by lazy {
        LoginRepositoryImpl(loginDataSource)
    }

    val truecallerLoginUseCase: TruecallerLoginUseCase by lazy {
        TruecallerLoginUseCaseImpl(loginRepository)
    }

    val requestOtpUseCase: RequestOtpUseCase by lazy {
        RequestOtpUseCaseImpl(loginRepository)
    }

    val otpLoginUseCase: OtpLoginUseCase by lazy {
        OtpLoginUseCaseImpl(loginRepository)
    }

    val logoutUseCase: LogoutUseCase by lazy {
        LogoutUseCaseImpl(
            loginRepository,
            clearCachedHomeFeedUseCase,
            httpClient,
            prefsApi
        )
    }

    val fetchPhoneByDeviceUseCase: IGetPhoneByDeviceUseCase by lazy {
        GetPhoneByDeviceUseCaseImpl(loginRepository)
    }

    val fetchOTPStatusUseCase: FetchOTPStatusUseCase by lazy {
        FetchOTPStatusUseCaseImpl(loginRepository)
    }

    val fetchSavingGoalsUseCase: FetchSavingGoalsUseCase by lazy {
        FetchSavingGoalsUseCaseImpl(loginRepository)
    }

    val postSavingGoalsUseCase: PostSavingGoalsUseCase by lazy {
        PostSavingGoalsUseCaseImpl(loginRepository)
    }

    val fetchUserSavingPreferencesUseCase: FetchUserSavingPreferencesUseCase by lazy {
        FetchUserSavingPreferencesUseCaseImpl(loginRepository)
    }

    val fetchOnboardingStoriesUseCase: FetchOnboardingStoriesUseCase by lazy {
        FetchOnboardingStoriesUseCaseImpl(loginRepository)
    }

    val fetchExperianTCUseCase: FetchExperianTCUseCase by lazy {
        FetchExperianTCUseCaseImpl(loginRepository)
    }

    val fetchSupportedLanguagesUseCase: FetchSupportedLanguagesUseCase by lazy {
        FetchSupportedLanguagesUseCaseImpl(loginRepository)
    }

    val fetchFaqStaticDataUseCase: FetchFaqStaticDataUseCase by lazy {
        FetchFaqStaticDataUseCaseImpl(loginRepository)
    }

    val provideSavingGoalsV2UseCase: FetchSavingGoalsV2UseCase by lazy{
        FetchSavingGoalsV2UseCaseImpl(loginRepository)
    }
}
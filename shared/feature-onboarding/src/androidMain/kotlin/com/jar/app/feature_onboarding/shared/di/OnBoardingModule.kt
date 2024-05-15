package com.jar.app.feature_onboarding.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_homepage.shared.domain.use_case.ClearCachedHomeFeedUseCase
import com.jar.app.feature_onboarding.shared.data.network.LoginDataSource
import com.jar.app.feature_onboarding.shared.data.repository.LoginRepositoryImpl
import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.*
import com.jar.app.feature_onboarding.shared.domain.usecase.impl.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class OnBoardingModule {

    @Provides
    @Singleton
    internal fun provideLoginDataSource(@AppHttpClient client: HttpClient): LoginDataSource {
        return LoginDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideLoginRepository(loginDataSource: LoginDataSource): LoginRepository {
        return LoginRepositoryImpl(loginDataSource)
    }


    @Provides
    @Singleton
    internal fun provideTruecallerLoginUseCase(loginRepository: LoginRepository): TruecallerLoginUseCase {
        return TruecallerLoginUseCaseImpl(loginRepository)
    }

    @Provides
    @Singleton
    internal fun provideRequestOtpUseCase(loginRepository: LoginRepository): RequestOtpUseCase {
        return RequestOtpUseCaseImpl(loginRepository)
    }

    @Provides
    @Singleton
    internal fun provideOtpLoginUseCase(loginRepository: LoginRepository): OtpLoginUseCase {
        return OtpLoginUseCaseImpl(loginRepository)
    }

    @Provides
    @Singleton
    internal fun provideGetPhoneByDeviceUseCase(loginRepository: LoginRepository): IGetPhoneByDeviceUseCase {
        return GetPhoneByDeviceUseCaseImpl(loginRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchOTPStatusUseCase(loginRepository: LoginRepository): FetchOTPStatusUseCase {
        return FetchOTPStatusUseCaseImpl(loginRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchSavingGoalsUseCase(loginRepository: LoginRepository): FetchSavingGoalsUseCase {
        return FetchSavingGoalsUseCaseImpl(loginRepository)
    }

    @Provides
    @Singleton
    internal fun providePostSavingGoalsUseCase(loginRepository: LoginRepository): PostSavingGoalsUseCase {
        return PostSavingGoalsUseCaseImpl(loginRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchUserSavingPreferencesUseCase(loginRepository: LoginRepository): FetchUserSavingPreferencesUseCase {
        return FetchUserSavingPreferencesUseCaseImpl(loginRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchOnboardingStoriesUseCase(loginRepository: LoginRepository): FetchOnboardingStoriesUseCase {
        return FetchOnboardingStoriesUseCaseImpl(loginRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchExperianTCUseCase(loginRepository: LoginRepository): FetchExperianTCUseCase {
        return FetchExperianTCUseCaseImpl(loginRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchSupportedLanguagesUseCase(loginRepository: LoginRepository): FetchSupportedLanguagesUseCase {
        return FetchSupportedLanguagesUseCaseImpl(loginRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchFaqStaticDataUseCase(loginRepository: LoginRepository): FetchFaqStaticDataUseCase {
        return FetchFaqStaticDataUseCaseImpl(loginRepository)
    }

    @Provides
    @Singleton
    internal fun provideLogoutUseCase(
        loginRepository: LoginRepository,
        clearCachedHomeFeedUseCase: ClearCachedHomeFeedUseCase,
        @AppHttpClient appHttpClient: HttpClient,
        prefsApi: PrefsApi
    ): LogoutUseCase {
        return LogoutUseCaseImpl(
            loginRepository,
            clearCachedHomeFeedUseCase,
            appHttpClient,
            prefsApi
        )
    }

    @Provides
    @Singleton
    internal fun provideFetchOtlUserInfoUseCase(loginRepository: LoginRepository): FetchOtlUserInfoUseCase {
        return FetchOtlUserInfoUseCaseImpl(loginRepository)
    }

    @Provides
    @Singleton
    internal fun provideSavingGoalsV2UseCase(loginRepository: LoginRepository): FetchSavingGoalsV2UseCase {
        return FetchSavingGoalsV2UseCaseImpl(loginRepository)
    }

}
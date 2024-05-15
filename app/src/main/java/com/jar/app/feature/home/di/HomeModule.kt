package com.jar.app.feature.home.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature.home.data.network.HomeDataSource
import com.jar.app.feature.home.data.network.UserDataSource
import com.jar.app.feature.home.data.repository.HomeRepository
import com.jar.app.feature.home.data.repository.UserRepository
import com.jar.app.feature.home.domain.repository.HomeRepositoryImpl
import com.jar.app.feature.home.domain.repository.UserRepositoryImpl
import com.jar.app.feature.home.domain.usecase.*
import com.jar.app.feature.home.domain.usecase.impl.*
import com.jar.app.feature.notification_list.domain.use_case.FetchNotificationMetaDataUseCase
import com.jar.app.feature.notification_list.domain.use_case.impl.FetchNotificationMetaDataUseCaseImpl
import com.jar.app.feature.promo_code.domain.use_case.FetchPromoCodeUseCase
import com.jar.app.feature.promo_code.domain.use_case.impl.FetchPromoCodeUseCaseImpl
import com.jar.app.feature.survey.domain.use_case.FetchUserSurveyUseCase
import com.jar.app.feature.survey.domain.use_case.SubmitUserSurveyUseCase
import com.jar.app.feature.survey.domain.use_case.impl.FetchUserSurveyUseCaseImpl
import com.jar.app.feature.survey.domain.use_case.impl.SubmitUserSurveyUseCaseImpl
import com.jar.app.feature_user_api.data.db.UserMetaLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class HomeModule {

    companion object {
        @Provides
        @Singleton
        internal fun provideHomeDataSource(@AppHttpClient client: HttpClient): HomeDataSource {
            return HomeDataSource(client)
        }

        @Provides
        @Singleton
        internal fun provideUserDataSource(@AppHttpClient client: HttpClient): UserDataSource {
            return UserDataSource(client)
        }

        @Provides
        @Singleton
        internal fun provideUserRepository(userDataSource: UserDataSource): UserRepository {
            return UserRepositoryImpl(userDataSource)
        }

        @Provides
        @Singleton
        internal fun provideHomeRepository(
            homeDataSource: HomeDataSource,
            userMetaLocalDataSource: UserMetaLocalDataSource
        ): HomeRepository {
            return HomeRepositoryImpl(homeDataSource, userMetaLocalDataSource)
        }

        @Provides
        @Singleton
        internal fun provideFetchDashboardStaticContentUseCase(homeRepository: HomeRepository): FetchDashboardStaticContentUseCase {
            return FetchDashboardStaticContentUseCaseImpl(homeRepository)
        }

        @Provides
        @Singleton
        internal fun provideUpdateUserDeviceDetailsUseCase(userRepository: UserRepository): UpdateUserDeviceDetailsUseCase {
            return UpdateUserDeviceDetailsUseCaseImpl(userRepository)
        }

        @Provides
        @Singleton
        internal fun provideFetchUserSurveyUseCase(homeRepository: HomeRepository): FetchUserSurveyUseCase {
            return FetchUserSurveyUseCaseImpl(homeRepository)
        }

        @Provides
        @Singleton
        internal fun provideSubmitUserSurveyUseCase(homeRepository: HomeRepository): SubmitUserSurveyUseCase {
            return SubmitUserSurveyUseCaseImpl(homeRepository)
        }

        @Provides
        @Singleton
        internal fun provideApplyPromoCodeUseCase(userRepository: UserRepository): ApplyPromoCodeUseCase {
            return ApplyPromoCodeUseCaseImpl(userRepository)
        }

        @Provides
        @Singleton
        internal fun provideUpdateFcmTokenUseCase(userRepository: UserRepository): UpdateFcmTokenUseCase {
            return UpdateFcmTokenUseCaseImpl(userRepository)
        }

        @Provides
        @Singleton
        internal fun provideUpdateSessionUseCase(homeRepository: HomeRepository): UpdateSessionUseCase {
            return UpdateSessionUseCaseImpl(homeRepository)
        }

        @Provides
        @Singleton
        internal fun provideFetchDowntimeUseCase(homeRepository: HomeRepository): FetchDowntimeUseCase {
            return FetchDowntimeUseCaseImpl(homeRepository)
        }

        @Provides
        @Singleton
        internal fun provideFetchUserRatingUseCase(userRepository: UserRepository): FetchUserRatingUseCase {
            return FetchUserRatingUseCaseImpl(userRepository)
        }

        @Provides
        @Singleton
        internal fun provideUpdateUserRatingUseCase(userRepository: UserRepository): UpdateUserRatingUseCase {
            return UpdateUserRatingUseCaseImpl(userRepository)
        }

        @Provides
        @Singleton
        internal fun provideFetchPromoCodeUseCase(homeRepository: HomeRepository): FetchPromoCodeUseCase {
            return FetchPromoCodeUseCaseImpl(homeRepository)
        }

        @Provides
        @Singleton
        internal fun provideFetchNotificationMetaDataUseCase(
            homeRepository: HomeRepository,
            userRepository: com.jar.app.feature_user_api.data.network.UserRepository
        ): FetchNotificationMetaDataUseCase {
            return FetchNotificationMetaDataUseCaseImpl(
                homeRepository,
                userRepository
            )
        }

        @Provides
        @Singleton
        internal fun provideFetchPopupMetaDataUseCase(
            homeRepository: HomeRepository,
            userRepository: com.jar.app.feature_user_api.data.network.UserRepository
        ): FetchPopupMetaDataUseCase {
            return FetchPopupMetaDataUseCaseImpl(
                homeRepository,
                userRepository
            )
        }

        @Provides
        @Singleton
        internal fun provideFetchActiveAnalyticsListUseCase(homeRepository: HomeRepository): FetchActiveAnalyticsListUseCase {
            return FetchActiveAnalyticsListUseCaseImpl(homeRepository)
        }

        @Provides
        @Singleton
        internal fun provideUpdateAdSourceDataUseCase(homeRepository: HomeRepository): UpdateAdSourceDataUseCase {
            return UpdateAdSourceDataUseCaseImpl(homeRepository)
        }

        @Provides
        @Singleton
        internal fun provideCaptureAppOpensUseCase(homeRepository: HomeRepository): CaptureAppOpensUseCase {
            return CaptureAppOpensUseCaseImpl(homeRepository)
        }

        @Provides
        @Singleton
        internal fun provideFetchForceUpdateUseCase(homeRepository: HomeRepository): FetchForceUpdateUseCase {
            return FetchForceUpdateUseCaseImpl(homeRepository)
        }

        @Provides
        @Singleton
        internal fun provideFetchIfKycRequiredUseCase (homeRepository: HomeRepository): FetchIfKycRequiredUseCase  {
            return FetchIfKycRequiredUseCaseImpl(homeRepository)
        }

        @Provides
        @Singleton
        internal fun provideFetchPublicStaticContentUseCase (homeRepository: HomeRepository): FetchPublicStaticContentUseCase  {
            return FetchPublicStaticContentUseCaseImpl(homeRepository)
        }
    }
}
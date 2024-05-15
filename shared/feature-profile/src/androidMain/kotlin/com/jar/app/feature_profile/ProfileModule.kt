package com.jar.app.feature_profile

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_profile.data.network.UserDataSource
import com.jar.app.feature_profile.data.repository.UserRepository
import com.jar.app.feature_profile.domain.repository.UserRepositoryImpl
import com.jar.app.feature_profile.domain.use_case.FetchDashboardStaticContentUseCase
import com.jar.app.feature_profile.domain.use_case.RequestOtpUseCase
import com.jar.app.feature_profile.domain.use_case.impl.FetchDashboardStaticContentUseCaseImpl
import com.jar.app.feature_profile.domain.use_case.impl.RequestOtpUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ProfileModule {

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
    internal fun provideFetchDashboardStaticContentUseCase(userRepository: UserRepository): FetchDashboardStaticContentUseCase {
        return FetchDashboardStaticContentUseCaseImpl(
            userRepository
        )
    }

    @Provides
    @Singleton
    internal fun provideRequestOtpUseCase(userRepository: UserRepository): RequestOtpUseCase {
        return RequestOtpUseCaseImpl(userRepository)
    }

}
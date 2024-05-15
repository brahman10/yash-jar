package com.jar.app.feature_weekly_magic.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_weekly_magic.shared.data.network.WeeklyChallengeDataSource
import com.jar.app.feature_weekly_magic.shared.domain.repository.WeeklyChallengeRepositoryInternal
import com.jar.app.feature_weekly_magic.shared.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class WeeklyChallengeDataModule {

    @Provides
    @Singleton
    internal fun provideCommonWeeklyChallengeDataModule(@AppHttpClient httpClient: HttpClient): CommonWeeklyChallengeDataModule {
        return CommonWeeklyChallengeDataModule(httpClient)
    }

    @Provides
    @Singleton
    internal fun provideWeeklyChallengeDataSource(commonWeeklyChallengeDataModule: CommonWeeklyChallengeDataModule): WeeklyChallengeDataSource {
        return commonWeeklyChallengeDataModule.weeklyChallengeDataSource
    }

    @Provides
    @Singleton
    internal fun provideWeeklyMagicRepositoryInternal(commonWeeklyChallengeDataModule: CommonWeeklyChallengeDataModule): WeeklyChallengeRepositoryInternal {
        return commonWeeklyChallengeDataModule.weeklyChallengeRepositoryInternal
    }

    @Provides
    @Singleton
    internal fun provideFetchWeeklyChallengeInfoUseCase(commonWeeklyChallengeDataModule: CommonWeeklyChallengeDataModule): FetchWeeklyChallengeInfoUseCase {
        return commonWeeklyChallengeDataModule.fetchWeeklyChallengeInfoUseCase
    }

    @Provides
    @Singleton
    internal fun provideMarkWeeklyChallengeInfoAsViewedUseCase(commonWeeklyChallengeDataModule: CommonWeeklyChallengeDataModule): MarkWeeklyChallengeInfoAsViewedUseCase {
        return commonWeeklyChallengeDataModule.markWeeklyChallengeInfoAsViewedUseCase
    }

}
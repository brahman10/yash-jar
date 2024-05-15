package com.jar.app.feature_weekly_magic_common.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_weekly_magic_common.shared.data.network.WeeklyChallengeDataSource
import com.jar.app.feature_weekly_magic_common.shared.data.repository.WeeklyChallengeRepositoryImpl
import com.jar.app.feature_weekly_magic_common.shared.domain.repository.WeeklyChallengeRepositoryExternal
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.*
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCaseImpl
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeOnBoardedUseCaseImpl
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeViewedUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeViewedUseCaseImpl
import com.jar.internal.library.jar_core_network.api.util.Serializer
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
    internal fun provideWeeklyMagicRepositoryExternal(weeklyChallengeDataSource: WeeklyChallengeDataSource, prefsApi: PrefsApi, serializer: Serializer): WeeklyChallengeRepositoryExternal {
        return WeeklyChallengeRepositoryImpl(weeklyChallengeDataSource, prefsApi, serializer)
    }

    @Provides
    @Singleton
    internal fun provideWeeklyChallengeDataSource(@AppHttpClient client: HttpClient): WeeklyChallengeDataSource {
        return WeeklyChallengeDataSource(client)
    }


    @Provides
    @Singleton
    internal fun provideFetchWeeklyChallengeDetailUseCase(weeklyChallengeRepositoryExternal: WeeklyChallengeRepositoryExternal): FetchWeeklyChallengeDetailUseCase {
        return FetchWeeklyChallengeDetailUseCaseImpl(weeklyChallengeRepositoryExternal)
    }

    @Provides
    @Singleton
    internal fun provideFetchWeeklyChallengeMetaDataUseCase(weeklyChallengeRepositoryExternal: WeeklyChallengeRepositoryExternal): FetchWeeklyChallengeMetaDataUseCase {
        return FetchWeeklyChallengeMetaDataUseCaseImpl(weeklyChallengeRepositoryExternal)
    }

    @Provides
    @Singleton
    internal fun provideMarkWeeklyChallengeOnBoardedUseCase(weeklyChallengeRepositoryExternal: WeeklyChallengeRepositoryExternal): MarkWeeklyChallengeOnBoardedUseCase {
        return MarkWeeklyChallengeOnBoardedUseCaseImpl(weeklyChallengeRepositoryExternal)
    }

    @Provides
    @Singleton
    internal fun provideMarkWeeklyChallengeAsViewedUseCase(weeklyChallengeRepositoryExternal: WeeklyChallengeRepositoryExternal): MarkWeeklyChallengeViewedUseCase {
        return MarkWeeklyChallengeViewedUseCaseImpl(weeklyChallengeRepositoryExternal)
    }
}
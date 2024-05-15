package com.jar.app.feature_story.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_story.data.InAppStoryRepository
import com.jar.app.feature_story.data.network.InAppStoryDataSource
import com.jar.app.feature_story.domain.repository.InAppStoryRepositoryImpl
import com.jar.app.feature_story.domain.use_cases.FetchInAppStoriesUseCase
import com.jar.app.feature_story.domain.use_cases.FetchPageByPageIdUseCase
import com.jar.app.feature_story.domain.use_cases.UpdateUserActionUseCase
import com.jar.app.feature_story.domain.use_cases.impl.FetchInAppStoriesUseCaseImpl
import com.jar.app.feature_story.domain.use_cases.impl.FetchPageByPageIdUseCaseImpl
import com.jar.app.feature_story.domain.use_cases.impl.UpdateUserActionUseCaseImpl

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class InAppStoryModule {
    @Provides
    @Singleton
    fun provideInAppStoryDataSource(@AppHttpClient client: HttpClient): InAppStoryDataSource {
        return InAppStoryDataSource(client)
    }


    @Provides
    @Singleton
    internal fun provideInAppStoryRepository(inAppStoryDataSource: InAppStoryDataSource): InAppStoryRepository {
        return InAppStoryRepositoryImpl(inAppStoryDataSource)
    }

    @Provides
    @Singleton
    fun provideFetchInAppStoriesUseCase(inAppStoryRepository: InAppStoryRepository): FetchInAppStoriesUseCase {
        return FetchInAppStoriesUseCaseImpl(inAppStoryRepository)
    }

    @Provides
    @Singleton
    fun provideUpdatePageLikedUseCase(inAppStoryRepository: InAppStoryRepository): UpdateUserActionUseCase {
        return UpdateUserActionUseCaseImpl(inAppStoryRepository)
    }
    @Provides
    @Singleton
    fun provideFetchPageByPageIdUseCase(inAppStoryRepository: InAppStoryRepository): FetchPageByPageIdUseCase {
        return FetchPageByPageIdUseCaseImpl(inAppStoryRepository)
    }

}
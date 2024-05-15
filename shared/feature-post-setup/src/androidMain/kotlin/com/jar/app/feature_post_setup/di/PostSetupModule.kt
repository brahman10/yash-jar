package com.jar.app.feature_post_setup.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_post_setup.data.network.PostSetupDataSource
import com.jar.app.feature_post_setup.data.repository.PostSetupRepository
import com.jar.app.feature_post_setup.domain.repository.PostSetupRepositoryImpl
import com.jar.app.feature_post_setup.domain.use_case.*
import com.jar.app.feature_post_setup.domain.use_case.impl.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class PostSetupModule {

    @Provides
    @Singleton
    internal fun providePostSetupDataSource(@AppHttpClient client: HttpClient): PostSetupDataSource {
        return PostSetupDataSource(client)
    }

    @Provides
    @Singleton
    internal fun providePostSetupRepository(postSetupDataSource: PostSetupDataSource): PostSetupRepository {
        return PostSetupRepositoryImpl(postSetupDataSource)
    }

    @Provides
    @Singleton
    internal fun provideFetchPostSetupUserDataUseCase(postSetupRepository: PostSetupRepository): FetchPostSetupUserDataUseCase {
        return FetchPostSetupUserDataUseCaseImpl(postSetupRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchPostSetupCalenderDataUseCase(postSetupRepository: PostSetupRepository): FetchPostSetupCalenderDataUseCase {
        return FetchPostSetupCalenderDataUseCaseImpl(postSetupRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchPostSetupQuickActionsUseCase(postSetupRepository: PostSetupRepository): FetchPostSetupQuickActionsUseCase {
        return FetchPostSetupQuickActionsUseCaseImpl(postSetupRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchPostSetupGenericFaqUseCase(postSetupRepository: PostSetupRepository): FetchPostSetupGenericFaqUseCase {
        return FetchPostSetupGenericFaqUseCaseImpl(postSetupRepository)
    }

    @Provides
    @Singleton
    internal fun provideInitiateFailedPaymentsUseCase(postSetupRepository: PostSetupRepository): InitiateFailedPaymentsUseCase {
        return InitiateFailedPaymentsUseCaseImpl(postSetupRepository)
    }

    @Provides
    @Singleton
    internal fun providePostSetupSavingOperationsUseCase(
        postSetupRepository: PostSetupRepository
    ): FetchPostSetupSavingOperationsUseCase {
        return FetchPostSetupSavingOperationsUseCaseImpl(postSetupRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchPostSetupDSFailureInfoUseCase(postSetupRepository: PostSetupRepository): FetchPostSetupDSFailureInfoUseCase {
        return FetchPostSetupDSFailureInfoUseCaseImpl(postSetupRepository)
    }

}
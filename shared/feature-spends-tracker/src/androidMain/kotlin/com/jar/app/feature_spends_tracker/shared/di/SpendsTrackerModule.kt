package com.jar.app.feature_spends_tracker.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_spends_tracker.shared.data.network.SpendsTrackerDataSource
import com.jar.app.feature_spends_tracker.shared.data.repository.SpendsTrackerRepository
import com.jar.app.feature_spends_tracker.shared.domain.repository.SpendsTrackerRepositoryImpl
import com.jar.app.feature_spends_tracker.shared.domain.usecase.FetchSpendsDataUseCase
import com.jar.app.feature_spends_tracker.shared.domain.usecase.FetchSpendsEducationDataUseCase
import com.jar.app.feature_spends_tracker.shared.domain.usecase.FetchSpendsTransactionDataUseCase
import com.jar.app.feature_spends_tracker.shared.domain.usecase.ReportTransactionUseCase
import com.jar.app.feature_spends_tracker.shared.domain.usecase.impl.FetchSpendsDataUseCaseImpl
import com.jar.app.feature_spends_tracker.shared.domain.usecase.impl.FetchSpendsEducationDataUseCaseImpl
import com.jar.app.feature_spends_tracker.shared.domain.usecase.impl.FetchSpendsTransactionDataUseCaseImpl
import com.jar.app.feature_spends_tracker.shared.domain.usecase.impl.ReportTransactionUseCaseImpl

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class SpendsTrackerModule {

    @Provides
    @Singleton
    internal fun provideSpendsTrackerDataSource(@AppHttpClient client: HttpClient): SpendsTrackerDataSource {
        return SpendsTrackerDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideSpendsTrackerRepository(spendsTrackerDataSource: SpendsTrackerDataSource): SpendsTrackerRepository {
        return SpendsTrackerRepositoryImpl(spendsTrackerDataSource)
    }

    @Provides
    @Singleton
    internal fun provideFetchSpendsDataUseCase(spendsTrackerRepository: SpendsTrackerRepository): FetchSpendsDataUseCase {
        return FetchSpendsDataUseCaseImpl(spendsTrackerRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchSpendsTransactionDataUseCase(spendsTrackerRepository: SpendsTrackerRepository): FetchSpendsTransactionDataUseCase {
        return FetchSpendsTransactionDataUseCaseImpl(spendsTrackerRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchSpendsEducationDataUseCase(spendsTrackerRepository: SpendsTrackerRepository): FetchSpendsEducationDataUseCase {
        return FetchSpendsEducationDataUseCaseImpl(spendsTrackerRepository)
    }

    @Provides
    @Singleton
    internal fun provideReportTransactionUseCase(spendsTrackerRepository: SpendsTrackerRepository): ReportTransactionUseCase {
        return ReportTransactionUseCaseImpl(spendsTrackerRepository)
    }
}
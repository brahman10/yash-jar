package com.jar.app.feature_daily_investment_cancellation.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_daily_investment_cancellation.shared.data.repository.DailyInvestmentCancellationRepository
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.FetchDailyInvestmentConfirmActionDataUseCase
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.FetchDailyInvestmentPauseDataUseCase
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.FetchDailyInvestmentPostCancellationDataUseCase
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.FetchDailyInvestmentSettingsDataUseCase
import com.jar.app.feature_daily_investment_cancellation.shared.data.network.DailyInvestmentCancellationDataSource
import com.jar.app.feature_daily_investment_cancellation.shared.domain.repository.DailyInvestmentCancellationRepositoryImpl
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.impl.FetchDailyInvestmentConfirmActionDataUseCaseImpl
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.impl.FetchDailyInvestmentPauseDataUseCaseImpl
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.impl.FetchDailyInvestmentPostCancellationDataUseCaseImpl
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.impl.FetchDailyInvestmentSettingsDataUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DailyInvestmentCancellationModule {

    @Provides
    @Singleton
    internal fun provideDailyInvestmentCancellationDataSource(@AppHttpClient client: HttpClient): DailyInvestmentCancellationDataSource {
        return DailyInvestmentCancellationDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideDailyInvestmentCancellationRepository(dailyInvestmentCancellationDataSource: DailyInvestmentCancellationDataSource): DailyInvestmentCancellationRepository {
        return DailyInvestmentCancellationRepositoryImpl(
            dailyInvestmentCancellationDataSource
        )
    }


    @Provides
    @Singleton
    internal fun provideFetchDailyInvestmentSettingsDataUseCase(dailyInvestmentCancellationRepository: DailyInvestmentCancellationRepository): FetchDailyInvestmentSettingsDataUseCase {
        return FetchDailyInvestmentSettingsDataUseCaseImpl(dailyInvestmentCancellationRepository)
    }


    @Provides
    @Singleton
    internal fun provideFetchDailyInvestmentPostCancellationDataUseCase(dailyInvestmentCancellationRepository: DailyInvestmentCancellationRepository): FetchDailyInvestmentPostCancellationDataUseCase {
        return FetchDailyInvestmentPostCancellationDataUseCaseImpl(dailyInvestmentCancellationRepository)
    }


    @Provides
    @Singleton
    internal fun provideFetchDailyInvestmentPauseDataUseCase(dailyInvestmentCancellationRepository: DailyInvestmentCancellationRepository): FetchDailyInvestmentPauseDataUseCase {
        return FetchDailyInvestmentPauseDataUseCaseImpl(dailyInvestmentCancellationRepository)
    }


    @Provides
    @Singleton
    internal fun provideFetchDailyInvestmentConfirmActionDataUseCase(dailyInvestmentCancellationRepository: DailyInvestmentCancellationRepository): FetchDailyInvestmentConfirmActionDataUseCase {
        return FetchDailyInvestmentConfirmActionDataUseCaseImpl(dailyInvestmentCancellationRepository)
    }
}
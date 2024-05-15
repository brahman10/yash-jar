package com.jar.app.feature_round_off.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_round_off.shared.data.network.RoundOffDataSource
import com.jar.app.feature_round_off.shared.data.repository.RoundOffRepository
import com.jar.app.feature_round_off.shared.domain.repository.RoundOffRepositoryImpl
import com.jar.app.feature_round_off.shared.domain.use_case.FetchInitialRoundOffTransactionBreakupUseCase
import com.jar.app.feature_round_off.shared.domain.use_case.FetchInitialRoundOffUseCase
import com.jar.app.feature_round_off.shared.domain.use_case.FetchRoundOffStepsUseCase
import com.jar.app.feature_round_off.shared.domain.use_case.InitiateDetectedSpendPaymentUseCase
import com.jar.app.feature_round_off.shared.domain.use_case.impl.FetchInitialRoundOffTransactionBreakupUseCaseImpl
import com.jar.app.feature_round_off.shared.domain.use_case.impl.FetchInitialRoundOffUseCaseImpl
import com.jar.app.feature_round_off.shared.domain.use_case.impl.FetchRoundOffStepsUseCaseImpl
import com.jar.app.feature_round_off.shared.domain.use_case.impl.InitiateDetectedSpendPaymentUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class RoundOffModule {

    @Provides
    @Singleton
    internal fun provideRoundOffDataSource(@AppHttpClient client: HttpClient): RoundOffDataSource {
        return RoundOffDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideRoundOffRepository(roundOffDataSource: RoundOffDataSource): RoundOffRepository {
        return RoundOffRepositoryImpl(roundOffDataSource)
    }

    @Provides
    @Singleton
    internal fun provideFetchInitialRoundOffUseCase(roundOffRepository: RoundOffRepository): FetchInitialRoundOffUseCase {
        return FetchInitialRoundOffUseCaseImpl(roundOffRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchInitialRoundOffTransactionBreakupUseCase(roundOffRepository: RoundOffRepository): FetchInitialRoundOffTransactionBreakupUseCase {
        return FetchInitialRoundOffTransactionBreakupUseCaseImpl(roundOffRepository)
    }

    @Provides
    @Singleton
    internal fun provideInitiateDetectedSpendPaymentUseCase(roundOffRepository: RoundOffRepository): InitiateDetectedSpendPaymentUseCase {
        return InitiateDetectedSpendPaymentUseCaseImpl(roundOffRepository)
    }

    @Provides
    @Singleton
    internal fun provideRoundOffStepsUseCase(roundOffRepository: RoundOffRepository): FetchRoundOffStepsUseCase {
        return FetchRoundOffStepsUseCaseImpl(roundOffRepository)
    }

}
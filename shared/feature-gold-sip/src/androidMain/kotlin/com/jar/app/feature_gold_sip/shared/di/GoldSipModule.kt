package com.jar.app.feature_gold_sip.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_gold_sip.shared.data.network.GoldSipDataSource
import com.jar.app.feature_gold_sip.shared.data.repository.GoldSipRepository
import com.jar.app.feature_gold_sip.shared.domain.use_case.DisableGoldSipUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipIntroUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipTypeSetupInfoUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchIsEligibleForGoldSipUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.impl.DisableGoldSipUseCaseImpl
import com.jar.app.feature_gold_sip.shared.domain.use_case.impl.FetchGoldSipIntroUseCaseImpl
import com.jar.app.feature_gold_sip.shared.domain.use_case.impl.FetchGoldSipTypeSetupInfoUseCaseImpl
import com.jar.app.feature_gold_sip.shared.domain.use_case.impl.FetchIsEligibleForGoldSipUseCaseImpl
import com.jar.app.feature_gold_sip.shared.domain.use_case.impl.UpdateGoldSipDetailsUseCaseImpl
import com.jar.app.feature_gold_sip.shared.util.MonthGenerator
import com.jar.app.feature_gold_sip.shared.util.WeekGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class GoldSipModule {

    @Provides
    @Singleton
    internal fun provideGoldSipDataSource(@AppHttpClient client: HttpClient): GoldSipDataSource {
        return GoldSipDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideGoldSipRepository(goldSipDataSource: GoldSipDataSource): GoldSipRepository {
        return com.jar.app.feature_gold_sip.shared.domain.repository.GoldSipRepositoryImpl(
            goldSipDataSource
        )
    }

    @Provides
    @Singleton
    internal fun provideDisableGoldSipUseCase(goldSipRepository: GoldSipRepository): DisableGoldSipUseCase {
        return DisableGoldSipUseCaseImpl(goldSipRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldSipIntroUseCase(goldSipRepository: GoldSipRepository): FetchGoldSipIntroUseCase {
        return FetchGoldSipIntroUseCaseImpl(goldSipRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldSipTypeSetupInfoUseCase(goldSipRepository: GoldSipRepository): FetchGoldSipTypeSetupInfoUseCase {
        return FetchGoldSipTypeSetupInfoUseCaseImpl(goldSipRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchIsEligibleForGoldSipUseCase(goldSipRepository: GoldSipRepository): FetchIsEligibleForGoldSipUseCase {
        return FetchIsEligibleForGoldSipUseCaseImpl(goldSipRepository)
    }

    @Provides
    @Singleton
    internal fun provideUpdateGoldSipDetailsUseCase(goldSipRepository: GoldSipRepository): UpdateGoldSipDetailsUseCase {
        return UpdateGoldSipDetailsUseCaseImpl(goldSipRepository)
    }

    @Provides
    @Singleton
    internal fun provideWeekGenerator() = WeekGenerator()


    @Provides
    @Singleton
    internal fun provideMonthGenerator() = MonthGenerator()
}
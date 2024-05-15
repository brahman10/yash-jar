package com.jar.app.feature_daily_investment.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_daily_investment.shared.data.network.DailyInvestmentDataSource
import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDSAbandonScreenUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDSAmountSelectionUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDSEducationUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDSMandateDataUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentBottomSheetV2UseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentOnboardingFragmentDataUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentOnboardingStoryUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentOptionsUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailySavingsFaqDataUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailySavingsIntroBottomSheetUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchGoldSavingUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchIsSavingPausedUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchUpdateDailyInvestmentStaticDataUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchUpdateDailyInvestmentStaticDataUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal class DailySavingModule {

    @Provides
    @Singleton
    internal fun provideCommonDailySavingModule(@AppHttpClient client: HttpClient): CommonDailySavingModule {
        return CommonDailySavingModule(client)
    }

    @Provides
    @Singleton
    internal fun provideDailyInvestmentDataSource(commonDailySavingModule: CommonDailySavingModule): DailyInvestmentDataSource {
        return commonDailySavingModule.dailyInvestmentDataSource
    }

    @Provides
    @Singleton
    internal fun provideDailyInvestmentRepository(commonDailySavingModule: CommonDailySavingModule): DailyInvestmentRepository {
        return commonDailySavingModule.dailyInvestmentRepository
    }

    @Provides
    @Singleton
    internal fun provideUpdateDailySavingStatusUseCase(commonDailySavingModule: CommonDailySavingModule): UpdateDailyInvestmentStatusUseCase {
        return commonDailySavingModule.provideUpdateDailySavingStatusUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchDailySavingStatusUseCase(commonDailySavingModule: CommonDailySavingModule): FetchDailyInvestmentStatusUseCase {
        return commonDailySavingModule.provideFetchDailySavingStatusUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchDailyInvestmentOptionsUseCase(commonDailySavingModule: CommonDailySavingModule): FetchDailyInvestmentOptionsUseCase {
        return commonDailySavingModule.provideFetchDailyInvestmentOptionsUseCase
    }

    @Provides
    @Singleton
    internal fun provideUpdateSavingPauseDurationUseCase(commonDailySavingModule: CommonDailySavingModule): UpdateSavingPauseDurationUseCase {
        return commonDailySavingModule.provideUpdateSavingPauseDurationUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchIsSavingPausedUseCase(commonDailySavingModule: CommonDailySavingModule): FetchIsSavingPausedUseCase {
        return commonDailySavingModule.provideFetchIsSavingPausedUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchDSAbandonScreenUseCase(commonDailySavingModule: CommonDailySavingModule): FetchDSAbandonScreenUseCase {
        return commonDailySavingModule.provideFetchDSAbandonScreenUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchDSAmountSelectionUseCaseImpl(commonDailySavingModule: CommonDailySavingModule): FetchDSAmountSelectionUseCase {
        return commonDailySavingModule.provideFetchDSAmountSelectionUseCaseImpl
    }

    @Provides
    @Singleton
    internal fun provideFetchDailySavingsFaqDataUseCaseImpl(commonDailySavingModule: CommonDailySavingModule): FetchDailySavingsFaqDataUseCase {
        return commonDailySavingModule.provideFetchDailySavingsFaqDataUseCaseImpl
    }

    @Provides
    @Singleton
    internal fun provideFetchDSEducationUseCase(commonDailySavingModule: CommonDailySavingModule): FetchDSEducationUseCase {
        return commonDailySavingModule.provideFetchDSEducationUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldSavingUserCase(commonDailySavingModule: CommonDailySavingModule): FetchGoldSavingUseCase {
        return commonDailySavingModule.provideFetchGoldSavingUserCase
    }

    @Provides
    @Singleton
    internal fun provideFetchDailySavingsIntroBottomSheetUserCase(commonDailySavingModule: CommonDailySavingModule): FetchDailySavingsIntroBottomSheetUseCase {
        return commonDailySavingModule.provideFetchDailySavingsIntroBottomSheetUserCase
    }

    @Provides
    @Singleton
    internal fun provideFetchDSMandateDataUseCase(commonDailySavingModule: CommonDailySavingModule): FetchDSMandateDataUseCase {
        return commonDailySavingModule.provideFetchDSMandateDataUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchDailyInvestmentOnboardingStoryUseCase(commonDailySavingModule: CommonDailySavingModule): FetchDailyInvestmentOnboardingStoryUseCase {
        return commonDailySavingModule.provideFetchDailyInvestmentOnboardingStoryUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchDailyInvestmentOnboardingFragmentDataUseCase(commonDailySavingModule: CommonDailySavingModule): FetchDailyInvestmentOnboardingFragmentDataUseCase {
        return commonDailySavingModule.provideFetchDailyInvestmentOnboardingFragmentDataUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchDailyInvestmentBottomSheetV2UseCase(commonDailySavingModule: CommonDailySavingModule): FetchDailyInvestmentBottomSheetV2UseCase {
        return commonDailySavingModule.provideFetchDailyInvestmentBottomSheetV2UseCase
    }


    @Provides
    @Singleton
    internal fun provideFetchUpdateDailyInvestmentStaticDataUseCaseImpl(dailyInvestmentRepository: DailyInvestmentRepository): FetchUpdateDailyInvestmentStaticDataUseCase {
        return FetchUpdateDailyInvestmentStaticDataUseCaseImpl(dailyInvestmentRepository)
    }


}
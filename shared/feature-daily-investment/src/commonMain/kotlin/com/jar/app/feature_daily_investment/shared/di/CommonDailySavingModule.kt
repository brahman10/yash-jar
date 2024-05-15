package com.jar.app.feature_daily_investment.shared.di

import com.jar.app.feature_daily_investment.shared.data.network.DailyInvestmentDataSource
import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.repository.DailyInvestmentRepositoryImpl
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
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchDSAbandonScreenUseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchDSAmountSelectionUseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchDSEducationUseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchDSMandateDataUseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchDailyInvestmentBottomSheetV2UseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchDailyInvestmentOnboardingFragmentDataUseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchDailyInvestmentOnboardingStoryUseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchDailyInvestmentOptionsUseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchDailyInvestmentStatusUseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchDailySavingsFaqDataUseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchDailySavingsIntroBottomSheetUseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchGoldSavingUseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.FetchIsSavingPausedUseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.UpdateDailyInvestmentStatusUseCaseImpl
import com.jar.app.feature_daily_investment.shared.domain.use_case.impl.UpdateSavingPauseDurationUseCaseImpl
import io.ktor.client.HttpClient

class CommonDailySavingModule(client: HttpClient) {

    val dailyInvestmentDataSource: DailyInvestmentDataSource by lazy {
        DailyInvestmentDataSource(client)
    }

    val dailyInvestmentRepository: DailyInvestmentRepository by lazy {
        DailyInvestmentRepositoryImpl(dailyInvestmentDataSource)
    }

    val provideUpdateDailySavingStatusUseCase: UpdateDailyInvestmentStatusUseCase by lazy {
        UpdateDailyInvestmentStatusUseCaseImpl(dailyInvestmentRepository)
    }

    val provideFetchDailySavingStatusUseCase: FetchDailyInvestmentStatusUseCase by lazy {
        FetchDailyInvestmentStatusUseCaseImpl(dailyInvestmentRepository)
    }

    val provideFetchDailyInvestmentOptionsUseCase: FetchDailyInvestmentOptionsUseCase by lazy {
        FetchDailyInvestmentOptionsUseCaseImpl(dailyInvestmentRepository)
    }

    val provideUpdateSavingPauseDurationUseCase: UpdateSavingPauseDurationUseCase by lazy {
        UpdateSavingPauseDurationUseCaseImpl(dailyInvestmentRepository)
    }

    val provideFetchIsSavingPausedUseCase: FetchIsSavingPausedUseCase by lazy {
        FetchIsSavingPausedUseCaseImpl(dailyInvestmentRepository)
    }

    val provideFetchDSAbandonScreenUseCase: FetchDSAbandonScreenUseCase by lazy {
        FetchDSAbandonScreenUseCaseImpl(dailyInvestmentRepository)
    }

    val provideFetchDSAmountSelectionUseCaseImpl: FetchDSAmountSelectionUseCase by lazy {
        FetchDSAmountSelectionUseCaseImpl(dailyInvestmentRepository)
    }

    val provideFetchDailySavingsFaqDataUseCaseImpl: FetchDailySavingsFaqDataUseCase by lazy {
        FetchDailySavingsFaqDataUseCaseImpl(dailyInvestmentRepository)
    }

    val provideFetchDSEducationUseCase: FetchDSEducationUseCase by lazy {
        FetchDSEducationUseCaseImpl(dailyInvestmentRepository)
    }

    val provideFetchGoldSavingUserCase: FetchGoldSavingUseCase by lazy {
        FetchGoldSavingUseCaseImpl(dailyInvestmentRepository)
    }

    val provideFetchDailySavingsIntroBottomSheetUserCase: FetchDailySavingsIntroBottomSheetUseCase by lazy {
        FetchDailySavingsIntroBottomSheetUseCaseImpl(dailyInvestmentRepository)
    }

    val provideFetchDSMandateDataUseCase: FetchDSMandateDataUseCase by lazy {
        FetchDSMandateDataUseCaseImpl(dailyInvestmentRepository)
    }

    val provideFetchDailyInvestmentOnboardingStoryUseCase: FetchDailyInvestmentOnboardingStoryUseCase by lazy {
        FetchDailyInvestmentOnboardingStoryUseCaseImpl(dailyInvestmentRepository)
    }

    val provideFetchDailyInvestmentOnboardingFragmentDataUseCase: FetchDailyInvestmentOnboardingFragmentDataUseCase by lazy {
        FetchDailyInvestmentOnboardingFragmentDataUseCaseImpl(dailyInvestmentRepository)
    }

    val provideFetchDailyInvestmentBottomSheetV2UseCase: FetchDailyInvestmentBottomSheetV2UseCase by lazy {
        FetchDailyInvestmentBottomSheetV2UseCaseImpl(dailyInvestmentRepository)
    }
}
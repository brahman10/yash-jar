package com.jar.app.feature_goal_based_saving.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.jar.app.feature_goal_based_saving.shared.data.network.GoalBasedSavingDataSource
import com.jar.app.feature_goal_based_saving.shared.data.repository.GoalBasedSavingRepository
import com.jar.app.feature_goal_based_saving.shared.domain.repository.GoalBasedSavingRepositoryImpl
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.*
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.impl.*
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class GoalBasedSavingModule {

    @Provides
    @Singleton
    internal fun provideGoalBasedSavingDataSource(@AppHttpClient client: HttpClient): GoalBasedSavingDataSource {
        return GoalBasedSavingDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideGoalBasedSavingRepository(goalBasedSavingDataSource: GoalBasedSavingDataSource): GoalBasedSavingRepository {
        return GoalBasedSavingRepositoryImpl(goalBasedSavingDataSource)
    }

    @Provides
    @Singleton
    internal fun provideFetchIntroDetailsUseCase(goalBasedSavingRepository: GoalBasedSavingRepository): FetchIntroDetailsUseCase {
        return FetchIntroDetailsUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchGoalNameScreenDetailsUseCase(goalBasedSavingRepository: GoalBasedSavingRepository): FetchGoalNameScreenDetails {
        return FetchGoalNameScreenDetailsImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchGoalAmountScreenDetailsUseCase(goalBasedSavingRepository: GoalBasedSavingRepository): FetchGoalAmountScreenDetailsUseCase {
        return FetchGoalAmountScreenDetailsUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchGoalDurationScreenDetailsUseCase(goalBasedSavingRepository: GoalBasedSavingRepository): FetchGoalDurationUseCase {
        return FetchGoalDurationUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchDailyAmountUseCase(goalBasedSavingRepository: GoalBasedSavingRepository): FetchDailyAmountUseCase {
        return FetchDailyAmountUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchMergeGoalScreenUseCase(goalBasedSavingRepository: GoalBasedSavingRepository): FetchMergeGoalScreenUseCase {
        return FetchMergeGoalScreenUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchMandateInfoUseCase(goalBasedSavingRepository: GoalBasedSavingRepository): FetchMandateInfoUseCase {
        return FetchMandateInfoUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchAbandonScreenResponseUseCase(goalBasedSavingRepository: GoalBasedSavingRepository): FetchAbandonScreenResponseUseCase {
        return FetchAbandonScreenResponseUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideCreateGoalUseCase(goalBasedSavingRepository: GoalBasedSavingRepository): CreateGoalUseCase {
        return CreateGoalUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchQnaUseCaseUseCase(goalBasedSavingRepository: GoalBasedSavingRepository): FetchQnaUseCase {
        return FetchQnaUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideEndGoalUseCase(goalBasedSavingRepository: GoalBasedSavingRepository): EndGoalUseCase {
        return EndGoalUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideManageGoalUseCase(goalBasedSavingRepository: GoalBasedSavingRepository):  FetchMangeGoalUseCase{
        return FetchMangeGoalUseCaseImpl(goalBasedSavingRepository)
    }
    @Provides
    @Singleton
    internal fun provideFetchEndGoalScreenResponseUseCase(goalBasedSavingRepository: GoalBasedSavingRepository):  FetchEndGoalScreenResponseUseCase{
        return FetchEndGoalScreenResponseUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchHomeFeedResponseUseCase(goalBasedSavingRepository: GoalBasedSavingRepository):  FetchHomeFeedResponseUseCase{
        return FetchHomeFeedResponseUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchGBSSettingsReponse(goalBasedSavingRepository: GoalBasedSavingRepository):  FetchGBSSettingsReponse {
        return FetchGBSSettingsReponseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideEndScreenViewedUseCase(goalBasedSavingRepository: GoalBasedSavingRepository):  EndScreenViewedUseCase {
        return EndScreenViewedUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideGetGoalTransactionScreenResponseUseCase(goalBasedSavingRepository: GoalBasedSavingRepository): GetGoalTransactionScreenResponseUseCase {
        return GetGoalTransactionScreenResponseUseCaseImpl(goalBasedSavingRepository)
    }

    @Provides
    @Singleton
    internal fun provideUpdateGoalDailyInvestmentStatusUseCase(goalBasedSavingRepository: GoalBasedSavingRepository): UpdateGoalDailyRecurringAmountUseCase {
        return UpdateGoalDailyInvestmentStatusUseCaseImpl(goalBasedSavingRepository)
    }



}
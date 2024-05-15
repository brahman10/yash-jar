package com.jar.app.feature_quests.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.feature_quests.shared.data.network.datasource.QuestsDataSource
import com.jar.feature_quests.shared.data.repository.QuestsRepository
import com.jar.feature_quests.shared.domain.repository.QuestsRepositoryImpl
import com.jar.feature_quests.shared.domain.use_case.*
import com.jar.feature_quests.shared.domain.use_case.impl.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
internal class QuestsApiModule {

    @Provides
    @Singleton
    internal fun providesQuestsModule(@AppHttpClient client: HttpClient): QuestsDataSource {
        return QuestsDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideQuestsRepository(dataSource: QuestsDataSource): QuestsRepository {
        return QuestsRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun provideFetchWelcomeRewardUseCase(repository: QuestsRepository): FetchWelcomeRewardUseCase {
        return FetchWelcomeRewardUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    fun provideFetchQuestRewardsUseCase(repository: QuestsRepository): FetchQuestRewardsUseCase {
        return FetchQuestRewardsUseCaseImpl(repository)
    }
    @Provides
    @Singleton
    fun provideUnlockWelcomeRewardUseCase(repository: QuestsRepository): UnlockWelcomeRewardUseCase {
        return UnlockWelcomeRewardUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    fun provideFetchHomePageUseCase(repository: QuestsRepository): FetchHomePageUseCase {
        return FetchHomePageUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    fun provideGetQuizGameQuestionUseCase(repository: QuestsRepository): GetQuizGameQuestionUseCase {
        return GetQuizGameQuestionUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    fun provideMarkAnswerUseCase(repository: QuestsRepository): MarkAnswerUseCase {
        return MarkAnswerUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    fun provideMarkGameInProgressUseCase(repository: QuestsRepository): MarkGameInProgressUseCase {
        return MarkGameInProgressUseCaseImpl(repository)
    }
}
package com.jar.feature_quests.shared.domain.repository

import com.jar.feature_quests.shared.data.network.datasource.QuestsDataSource
import com.jar.feature_quests.shared.data.repository.QuestsRepository
import com.jar.feature_quests.shared.domain.model.request.SubmitAnswerRequestData

internal class QuestsRepositoryImpl constructor(private val dataSource: QuestsDataSource) :
    QuestsRepository {
    override suspend fun fetchWelcomeReward() = getFlowResult { dataSource.fetchWelcomeReward() }
    override suspend fun unlockWelcomeReward() = getFlowResult { dataSource.unlockWelcomeReward() }
    override suspend fun fetchHomePage() = getFlowResult { dataSource.fetchHomePage() }
    override suspend fun getQuizGameQuestion() = getFlowResult { dataSource.getQuizGameQuestion() }
    override suspend fun markAnswer(data: SubmitAnswerRequestData) = getFlowResult { dataSource.markAnswer(data) }
    override suspend fun fetchQuestRewards() = getFlowResult { dataSource.fetchQuestRewards() }

    override suspend fun markGameInProgress(gameType: String) = getFlowResult { dataSource.markGameInProgress(gameType) }
}
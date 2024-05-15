package com.jar.feature_quests.shared.domain.use_case.impl

import com.jar.feature_quests.shared.data.repository.QuestsRepository
import com.jar.feature_quests.shared.domain.use_case.FetchQuestRewardsUseCase

class FetchQuestRewardsUseCaseImpl(private val repository: QuestsRepository) :
    FetchQuestRewardsUseCase {
    override suspend fun fetchQuestRewards() = repository.fetchQuestRewards()
}
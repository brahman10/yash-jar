package com.jar.feature_quests.shared.domain.use_case.impl

import com.jar.feature_quests.shared.data.repository.QuestsRepository
import com.jar.feature_quests.shared.domain.use_case.FetchWelcomeRewardUseCase

class FetchWelcomeRewardUseCaseImpl(private val repository: QuestsRepository) :
    FetchWelcomeRewardUseCase {
    override suspend fun fetchWelcomeReward() = repository.fetchWelcomeReward()
}



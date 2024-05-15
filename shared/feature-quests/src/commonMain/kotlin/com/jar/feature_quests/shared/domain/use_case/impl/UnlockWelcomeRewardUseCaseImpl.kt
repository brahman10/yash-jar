package com.jar.feature_quests.shared.domain.use_case.impl

import com.jar.feature_quests.shared.data.repository.QuestsRepository
import com.jar.feature_quests.shared.domain.use_case.UnlockWelcomeRewardUseCase

class UnlockWelcomeRewardUseCaseImpl(private val repository: QuestsRepository) :
    UnlockWelcomeRewardUseCase {
    override suspend fun unlockWelcomeReward() = repository.unlockWelcomeReward()
}
package com.jar.feature_quests.shared.domain.use_case.impl

import com.jar.feature_quests.shared.data.repository.QuestsRepository
import com.jar.feature_quests.shared.domain.use_case.MarkGameInProgressUseCase

class MarkGameInProgressUseCaseImpl(private val repository: QuestsRepository): MarkGameInProgressUseCase {

    override suspend fun markGameInProgress(gameType: String) = repository.markGameInProgress(gameType)

}
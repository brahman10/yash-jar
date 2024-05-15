package com.jar.feature_quests.shared.domain.use_case.impl

import com.jar.feature_quests.shared.data.repository.QuestsRepository
import com.jar.feature_quests.shared.domain.use_case.GetQuizGameQuestionUseCase

class GetQuizGameQuestionUseCaseImpl(private val repository: QuestsRepository) :
    GetQuizGameQuestionUseCase {
    override suspend fun getQuizGameQuestion() = repository.getQuizGameQuestion()
}
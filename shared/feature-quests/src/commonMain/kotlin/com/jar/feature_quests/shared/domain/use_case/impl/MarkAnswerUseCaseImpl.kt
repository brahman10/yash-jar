package com.jar.feature_quests.shared.domain.use_case.impl

import com.jar.feature_quests.shared.domain.model.request.SubmitAnswerRequestData
import com.jar.feature_quests.shared.data.repository.QuestsRepository
import com.jar.feature_quests.shared.domain.use_case.MarkAnswerUseCase

class MarkAnswerUseCaseImpl(private val repository: QuestsRepository) : MarkAnswerUseCase {
    override suspend fun markAnswer(body: SubmitAnswerRequestData) = repository.markAnswer(body)
}
package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.AcknowledgeOneTimeCardUseCase

internal class AcknowledgeOneTimeCardUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : AcknowledgeOneTimeCardUseCase {
    override suspend fun acknowledgeOneTimeCard(cardType: String) =
        lendingRepository.acknowledgeOneTimeCard(cardType)
}
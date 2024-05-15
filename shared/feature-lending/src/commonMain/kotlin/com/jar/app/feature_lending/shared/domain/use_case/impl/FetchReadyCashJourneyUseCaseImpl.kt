package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchReadyCashJourneyUseCase

internal class FetchReadyCashJourneyUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : FetchReadyCashJourneyUseCase {
    override suspend fun getReadyCashJourney() = lendingRepository.fetchReadyCashJourney()

}
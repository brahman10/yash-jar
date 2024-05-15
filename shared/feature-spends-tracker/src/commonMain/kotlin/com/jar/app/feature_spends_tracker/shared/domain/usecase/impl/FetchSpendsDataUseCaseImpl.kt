package com.jar.app.feature_spends_tracker.shared.domain.usecase.impl

import com.jar.app.feature_spends_tracker.shared.data.repository.SpendsTrackerRepository
import com.jar.app.feature_spends_tracker.shared.domain.usecase.FetchSpendsDataUseCase

internal class FetchSpendsDataUseCaseImpl  constructor(
    private val spendsTrackerRepository: SpendsTrackerRepository
) : FetchSpendsDataUseCase {
    override suspend fun fetchSpendsData() = spendsTrackerRepository.fetchSpendsData()
}
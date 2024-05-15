package com.jar.app.feature_spends_tracker.shared.domain.usecase.impl

import com.jar.app.feature_spends_tracker.shared.data.repository.SpendsTrackerRepository
import com.jar.app.feature_spends_tracker.shared.domain.usecase.FetchSpendsTransactionDataUseCase

internal class FetchSpendsTransactionDataUseCaseImpl  constructor(
    private val spendsTrackerRepository: SpendsTrackerRepository
) : FetchSpendsTransactionDataUseCase {

    override suspend fun fetchSpendsTransactionData(page: Int, size: Int) =
        spendsTrackerRepository.fetchSpendsTransactionData(page, size)
}
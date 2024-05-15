package com.jar.app.feature_spends_tracker.shared.domain.usecase.impl

import com.jar.app.feature_spends_tracker.shared.data.repository.SpendsTrackerRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_spends_tracker.shared.domain.model.spends_education.SpendsEducationData
import com.jar.app.feature_spends_tracker.shared.domain.usecase.FetchSpendsEducationDataUseCase
import kotlinx.coroutines.flow.Flow

internal class FetchSpendsEducationDataUseCaseImpl constructor(private val spendsTrackerRepository: SpendsTrackerRepository) :
    FetchSpendsEducationDataUseCase {
    override suspend fun fetchSpendsEducationData(): Flow<RestClientResult<ApiResponseWrapper<SpendsEducationData?>>> =
        spendsTrackerRepository.fetchSpendsEducationData()
}
package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.FetchCamsDataStatusUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchCamsDataStatusUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : FetchCamsDataStatusUseCase {
    override suspend fun fetchCamsDataStatus(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>> =
        lendingRepository.fetchCamsDataStatus()

}
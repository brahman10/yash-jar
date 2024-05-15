package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.model.camps_flow.RealtimeBankData
import com.jar.app.feature_lending.shared.domain.use_case.FetchCamsBanksUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchCamsBanksUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : FetchCamsBanksUseCase {
    override suspend fun fetchCamsBanks(): Flow<RestClientResult<ApiResponseWrapper<List<RealtimeBankData>?>>> =
        lendingRepository.fetchCamsBanks()
}
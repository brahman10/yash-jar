package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.model.camps_flow.CamsSdkRedirectionData
import com.jar.app.feature_lending.shared.domain.use_case.FetchCamsSdkRedirectDataUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchCamsSdkRedirectDataUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : FetchCamsSdkRedirectDataUseCase {
    override suspend fun fetchCamsSdkRedirectData(fipId: String): Flow<RestClientResult<ApiResponseWrapper<CamsSdkRedirectionData?>>> =
        lendingRepository.fetchCamsSdkRedirectData(fipId)
}
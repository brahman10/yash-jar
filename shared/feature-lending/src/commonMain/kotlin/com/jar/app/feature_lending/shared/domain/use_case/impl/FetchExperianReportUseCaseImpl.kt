package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.FetchExperianReportRequest
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.SuccessApiResponse
import com.jar.app.feature_lending.shared.domain.use_case.FetchExperianReportUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchExperianReportUseCaseImpl(
    private val lendingRepository: LendingRepository
) : FetchExperianReportUseCase {

    override suspend fun fetchExperianReportUseCase(
        fetchExperianReportRequest: FetchExperianReportRequest
    ): Flow<RestClientResult<ApiResponseWrapper<SuccessApiResponse?>>> =
        lendingRepository.fetchExperianReport(fetchExperianReportRequest)


}
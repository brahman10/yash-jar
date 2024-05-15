package com.jar.app.feature_lending_kyc.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_lending_kyc.shared.domain.model.FaqDetails
import kotlinx.coroutines.flow.Flow

interface FetchLendingKycFaqListUseCase {

    suspend fun fetchKycFaqList(): Flow<RestClientResult<ApiResponseWrapper<FaqDetails?>>>
}
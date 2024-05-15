package com.jar.app.feature_gold_lease.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseTermsAndConditions
import kotlinx.coroutines.flow.Flow

interface FetchGoldLeaseTermsAndConditionsUseCase {

    suspend fun fetchGoldLeaseTermsAndConditions(): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseTermsAndConditions?>>>

}
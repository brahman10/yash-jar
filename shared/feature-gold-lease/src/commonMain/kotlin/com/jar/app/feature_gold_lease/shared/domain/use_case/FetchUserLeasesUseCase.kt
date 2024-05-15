package com.jar.app.feature_gold_lease.shared.domain.use_case

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2UserLeases
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

interface FetchUserLeasesUseCase {

    suspend fun fetchUserLeases(page: Int, size: Int, userLeasesFilter: String): RestClientResult<ApiResponseWrapper<GoldLeaseV2UserLeases?>>

}
package com.jar.app.feature_gold_lease.shared.domain.use_case

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2JewellerListing
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchGoldLeaseJewellerListingsUseCase {

    suspend fun fetchGoldLeaseJewellerListings(): Flow<RestClientResult<ApiResponseWrapper<GoldLeaseV2JewellerListing?>>>

}
package com.jar.app.feature_sell_gold.shared.domain.use_cases

import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.feature_sell_gold.shared.domain.models.SellGoldStaticData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface IFetchSellGoldStaticContentUseCase {
    suspend fun fetchDashboardStaticContent(staticContentType: StaticContentType):
            Flow<RestClientResult<ApiResponseWrapper<SellGoldStaticData?>>>
}
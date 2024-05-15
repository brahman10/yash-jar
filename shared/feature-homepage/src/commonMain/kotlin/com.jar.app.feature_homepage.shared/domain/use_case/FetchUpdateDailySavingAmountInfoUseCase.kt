package com.jar.app.feature_homepage.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_homepage.shared.domain.model.update_daily_saving.UpdateDailySavingInfo
import kotlinx.coroutines.flow.Flow

interface FetchUpdateDailySavingAmountInfoUseCase {
    suspend fun fetchUpdateDailySavingAmountInfo(includeView: Boolean = true): Flow<RestClientResult<ApiResponseWrapper<UpdateDailySavingInfo>>>
}
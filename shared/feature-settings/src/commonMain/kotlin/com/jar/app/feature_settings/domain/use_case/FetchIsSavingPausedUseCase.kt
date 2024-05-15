package com.jar.app.feature_settings.domain.use_case

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import kotlinx.coroutines.flow.Flow

interface FetchIsSavingPausedUseCase {

    suspend fun fetchIsSavingPaused(pauseType: SavingsType): Flow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
}
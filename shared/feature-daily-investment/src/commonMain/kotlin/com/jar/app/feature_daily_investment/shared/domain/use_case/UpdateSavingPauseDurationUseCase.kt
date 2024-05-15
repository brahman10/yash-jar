package com.jar.app.feature_daily_investment.shared.domain.use_case

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UpdateSavingPauseDurationUseCase {

    suspend fun updateSavingPauseDuration(
        pause: Boolean,
        pauseDuration: String?,
        savingType: SavingsType,
        customDuration: Long? = null
    ): Flow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>

}
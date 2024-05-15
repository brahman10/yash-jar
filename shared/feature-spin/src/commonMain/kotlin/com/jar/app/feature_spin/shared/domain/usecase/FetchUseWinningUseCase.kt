package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_spin.shared.domain.model.UseWinningPopupCta
import kotlinx.coroutines.flow.Flow

interface FetchUseWinningUseCase {
    suspend fun fetchUseWinning(): Flow<RestClientResult<ApiResponseWrapper<UseWinningPopupCta>>>

}
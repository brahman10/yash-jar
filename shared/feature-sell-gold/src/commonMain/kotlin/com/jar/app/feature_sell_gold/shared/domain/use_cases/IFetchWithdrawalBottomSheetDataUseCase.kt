package com.jar.app.feature_sell_gold.shared.domain.use_cases

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_sell_gold.shared.domain.models.WithdrawHelpData
import kotlinx.coroutines.flow.Flow

interface IFetchWithdrawalBottomSheetDataUseCase {
    suspend fun fetchWithdrawBottomSheetData() : Flow<RestClientResult<ApiResponseWrapper<WithdrawHelpData?>>>
}
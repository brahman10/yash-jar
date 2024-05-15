package com.jar.app.feature_mandate_payments_common.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PreferredBankPageItem
import kotlinx.coroutines.flow.Flow

interface FetchPreferredBankUseCase {
    suspend fun fetchPreferredBank(): Flow<RestClientResult<ApiResponseWrapper<PreferredBankPageItem?>>>
}
package com.jar.app.feature_savings_common.shared.domain.use_case

import com.jar.app.feature_savings_common.shared.domain.model.DSSavingsState
import com.jar.app.feature_savings_common.shared.domain.model.SavingSetupInfo
import com.jar.app.feature_savings_common.shared.domain.model.SavingsSubscriptionType
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchSavingsSetupInfoUseCase {

    suspend fun fetchSavingSetupInfo(
        savingsSubscriptionType: SavingsSubscriptionType,
        savingsType: SavingsType,
        savingStateContext: String = DSSavingsState.DS_SETUP.name
    ): Flow<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>>
}
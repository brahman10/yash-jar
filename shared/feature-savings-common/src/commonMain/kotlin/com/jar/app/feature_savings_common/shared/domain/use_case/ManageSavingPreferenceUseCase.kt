package com.jar.app.feature_savings_common.shared.domain.use_case

import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface ManageSavingPreferenceUseCase {
    suspend fun manageSavingsPreference(
        savingsType: SavingsType,
        enableAutoSave: Boolean
    ): Flow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
}
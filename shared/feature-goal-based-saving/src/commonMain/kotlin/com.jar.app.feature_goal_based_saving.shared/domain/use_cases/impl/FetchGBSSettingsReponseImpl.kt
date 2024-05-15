package com.jar.app.feature_goal_based_saving.shared.domain.use_cases.impl

import com.jar.app.feature_goal_based_saving.shared.data.model.GBSSettingResponse
import com.jar.app.feature_goal_based_saving.shared.data.repository.GoalBasedSavingRepository
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchGBSSettingsReponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchGBSSettingsReponseImpl constructor(
    private val goalBasedSavingRepository: GoalBasedSavingRepository
): FetchGBSSettingsReponse {
    override suspend fun execute(): Flow<RestClientResult<ApiResponseWrapper<GBSSettingResponse>>> {
        return goalBasedSavingRepository.fetchGBSSettings()
    }
}
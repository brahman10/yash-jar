package com.jar.app.feature_gold_sip.shared.data.repository

import com.jar.app.feature_gold_sip.shared.domain.model.EligibleForGoldSipData
import com.jar.app.feature_gold_sip.shared.domain.model.GoldSipIntroData
import com.jar.app.feature_gold_sip.shared.domain.model.GoldSipSetupInfo
import com.jar.app.feature_gold_sip.shared.domain.model.UpdateSipDetails
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface GoldSipRepository : BaseRepository {

    suspend fun fetchGoldSipIntro(): Flow<RestClientResult<ApiResponseWrapper<GoldSipIntroData>>>

    suspend fun fetchIsEligibleForGoldSip(): Flow<RestClientResult<ApiResponseWrapper<EligibleForGoldSipData>>>

    suspend fun fetchGoldSipDetails(): Flow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>

    suspend fun updateGoldSipDetails(updateSipDetails: UpdateSipDetails): Flow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>

    suspend fun disableGoldSip(): Flow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>

    suspend fun fetchGoldSipTypeSetupInfo(subscriptionType: String): Flow<RestClientResult<ApiResponseWrapper<GoldSipSetupInfo>>>

}
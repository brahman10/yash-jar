package com.jar.app.feature_gold_sip.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_gold_sip.shared.domain.model.GoldSipIntroData
import kotlinx.coroutines.flow.Flow

 interface FetchGoldSipIntroUseCase {

    suspend fun fetchGoldSipIntro(): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_gold_sip.shared.domain.model.GoldSipIntroData>>>

}
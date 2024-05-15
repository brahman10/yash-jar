package com.jar.app.feature_refer_earn_v2.shared.domain.use_case

import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferIntroScreenData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchReferralIntroStaticDataUseCase {
    suspend fun fetchReferralIntroStaticData(): Flow<RestClientResult<ApiResponseWrapper<ReferIntroScreenData?>>>
}
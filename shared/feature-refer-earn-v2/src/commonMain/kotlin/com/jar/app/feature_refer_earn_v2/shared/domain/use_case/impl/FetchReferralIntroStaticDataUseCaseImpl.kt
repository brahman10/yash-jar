package com.jar.app.feature_refer_earn_v2.shared.domain.use_case.impl

import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferIntroScreenData
import com.jar.app.feature_refer_earn_v2.shared.domain.repository.ReferEarnV2Repository
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralIntroStaticDataUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchReferralIntroStaticDataUseCaseImpl constructor(
    private val referEarnV2Repository: ReferEarnV2Repository
) : FetchReferralIntroStaticDataUseCase {

    override suspend fun fetchReferralIntroStaticData(): Flow<RestClientResult<ApiResponseWrapper<ReferIntroScreenData?>>> =
        referEarnV2Repository.fetchReferralIntroStaticData()

}
package com.jar.app.feature_refer_earn_v2.shared.domain.use_case.impl

import com.jar.app.feature_refer_earn_v2.shared.domain.model.PostReferralAttributionData
import com.jar.app.feature_refer_earn_v2.shared.domain.repository.ReferEarnV2Repository
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralsUseCase
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.PostReferralAttributionDataUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class PostReferralAttributionDataUseCaseImpl constructor(
    private val referEarnV2Repository: ReferEarnV2Repository
) : PostReferralAttributionDataUseCase {

    override suspend fun postReferralAttribution(
        data: PostReferralAttributionData?
    ) = referEarnV2Repository.postReferralAttribution(data)
}
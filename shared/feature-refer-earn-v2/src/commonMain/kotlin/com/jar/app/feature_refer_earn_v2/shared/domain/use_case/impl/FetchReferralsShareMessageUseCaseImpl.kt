package com.jar.app.feature_refer_earn_v2.shared.domain.use_case.impl

import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferralShareMessageData
import com.jar.app.feature_refer_earn_v2.shared.domain.repository.ReferEarnV2Repository
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralsShareMessageUseCase
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralsUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchReferralsShareMessageUseCaseImpl constructor(
    private val referEarnV2Repository: ReferEarnV2Repository
) : FetchReferralsShareMessageUseCase {
    override suspend fun fetchReferralShareMessage(referralLink: String): Flow<RestClientResult<ApiResponseWrapper<ReferralShareMessageData?>>> {
        return referEarnV2Repository.fetchReferralShareMessage(referralLink)
    }

}
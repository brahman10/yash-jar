package com.jar.app.feature_refer_earn_v2.shared.domain.repository

import com.jar.app.feature_refer_earn_v2.shared.data.network.ReferEarnV2DataSource
import com.jar.app.feature_refer_earn_v2.shared.domain.model.PostReferralAttributionData
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferIntroScreenData
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferralShareMessageData
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferralUserData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow


internal class ReferEarnV2RepositoryImpl constructor(private val referEarnV2DataSource: ReferEarnV2DataSource) :
    ReferEarnV2Repository {
    override suspend fun fetchReferralIntroStaticData() = getFlowResult { referEarnV2DataSource.fetchReferralIntroStaticData() }
    override suspend fun fetchReferrals(
        page: Int,
        size: Int
    ) = referEarnV2DataSource.fetchReferrals(page, size)

    override suspend fun postReferralAttribution(
        data: PostReferralAttributionData?
    ) = getFlowResult { referEarnV2DataSource.postReferralAttribution(data) }

    override suspend fun fetchReferralShareMessage(referralLink: String) = getFlowResult { referEarnV2DataSource.fetchReferralShareMessage(referralLink) }

}
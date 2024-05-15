package com.jar.app.feature_refer_earn_v2.shared.domain.repository

import com.jar.app.feature_refer_earn_v2.shared.domain.model.PostReferralAttributionData
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferIntroScreenData
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferralShareMessageData
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferralUserData
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface ReferEarnV2Repository: BaseRepository {
    suspend fun fetchReferralIntroStaticData(): Flow<RestClientResult<ApiResponseWrapper<ReferIntroScreenData?>>>
    suspend fun fetchReferrals(
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<ReferralUserData?>>

    suspend fun postReferralAttribution(
        data: PostReferralAttributionData?
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchReferralShareMessage(
        referralLink: String
    ): Flow<RestClientResult<ApiResponseWrapper<ReferralShareMessageData?>>>
}
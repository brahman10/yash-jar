package com.jar.app.feature_refer_earn_v2.shared.data.network

import com.jar.app.feature_refer_earn_v2.shared.domain.model.PostReferralAttributionData
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferIntroScreenData
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferralShareMessageData
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferralUserData
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

internal class ReferEarnV2DataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchReferralIntroStaticData() =
        getResult<ApiResponseWrapper<ReferIntroScreenData?>> {
            client.get {
                url(ReferEarnV2Constants.Endpoints.FETCH_REFERRAL_INTRO)
            }
        }
    suspend fun fetchReferrals(page: Int, size: Int) =
        getResult<ApiResponseWrapper<ReferralUserData?>> {
            client.get {
                url(ReferEarnV2Constants.Endpoints.FETCH_REFERRALS)
                parameter("page", page)
                parameter("size", size)
            }
        }
    suspend fun postReferralAttribution(data: PostReferralAttributionData?) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(ReferEarnV2Constants.Endpoints.POST_REFERRAL_ATTRIBUTION)
                setBody(data)
            }
        }
    suspend fun fetchReferralShareMessage(
        referralLink: String
    ) =
        getResult<ApiResponseWrapper<ReferralShareMessageData?>> {
            client.get {
                url(ReferEarnV2Constants.Endpoints.FETCH_REFERRAL_SHARE_MESSAGES)
                parameter("referralLink", referralLink)
            }
        }
}
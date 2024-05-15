package com.jar.app.feature_refer_earn_v2.shared.domain.use_case

import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferralUserData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult


interface FetchReferralsUseCase {

    suspend fun fetchReferrals(
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<ReferralUserData?>>

}
package com.jar.app.feature_refer_earn_v2.shared.ui

import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferIntroScreenData
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferralShareMessageData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

data class ReferEarnIntroStateData(
    val introScreenData: RestClientResult<ApiResponseWrapper<ReferIntroScreenData?>> = RestClientResult.none(),
    val shareMessageDetails: RestClientResult<ApiResponseWrapper<ReferralShareMessageData?>> = RestClientResult.none(),
)
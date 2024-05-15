package com.jar.app.feature_buy_gold_v2.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousDateList

interface FetchAuspiciousDatesUseCase {
    suspend fun fetchAuspiciousDates(
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<AuspiciousDateList>>

}
package com.jar.app.feature.home.domain.usecase

import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousDateList
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

interface FetchAuspiciousDatesUseCase {

    suspend fun fetchAuspiciousDates(
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<AuspiciousDateList>>

}
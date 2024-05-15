package com.jar.app.feature_homepage.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_homepage.shared.domain.model.partner_banner.BannerList
import kotlinx.coroutines.flow.Flow

interface FetchPartnerBannerUseCase {

    suspend fun fetchPartnerBanners(includeView: Boolean = false): Flow<RestClientResult<ApiResponseWrapper<BannerList>>>

}
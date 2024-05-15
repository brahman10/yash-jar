package com.jar.app.feature_contact_sync_common.shared.domain.usecases

import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteListResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

interface FetchSentInviteListUseCase {
    suspend fun fetchSentInviteList(
        page: Int,
        size: Int,
        featureType: ContactListFeatureType
    ): RestClientResult<ApiResponseWrapper<SentInviteListResponse?>>
}
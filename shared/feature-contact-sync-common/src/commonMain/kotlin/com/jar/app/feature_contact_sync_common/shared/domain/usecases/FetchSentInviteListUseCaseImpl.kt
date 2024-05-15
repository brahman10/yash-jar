package com.jar.app.feature_contact_sync_common.shared.domain.usecases

import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.data.repository.IContactsSyncRepository
import com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteListResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

internal class FetchSentInviteListUseCaseImpl constructor(private val repostiory: IContactsSyncRepository) :
    FetchSentInviteListUseCase {
    override suspend fun fetchSentInviteList(
        page: Int,
        size: Int,
        featureType: ContactListFeatureType
    ): RestClientResult<ApiResponseWrapper<SentInviteListResponse?>> =
        repostiory.fetchSentInviteListList(page, size, featureType)
}
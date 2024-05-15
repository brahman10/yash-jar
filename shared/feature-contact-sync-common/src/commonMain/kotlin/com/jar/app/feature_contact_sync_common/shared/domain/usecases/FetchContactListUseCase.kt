package com.jar.app.feature_contact_sync_common.shared.domain.usecases

import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListResponse
import kotlinx.coroutines.flow.Flow

interface FetchContactListUseCase {

    suspend fun fetchContactListFlow(
        page: Int, size: Int,
        featureType: ContactListFeatureType,
        searchText: String?
    ): Flow<RestClientResult<ApiResponseWrapper<ContactListResponse?>>>

    suspend fun fetchContactList(
        page: Int,
        size: Int,
        featureType: ContactListFeatureType,
        searchText: String?
    ): RestClientResult<ApiResponseWrapper<ContactListResponse?>>

}
package com.jar.app.feature_contact_sync_common.shared.domain.usecases

import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListStaticDataResponse
import kotlinx.coroutines.flow.Flow

interface FetchContactListStaticDataUseCase {

    suspend fun fetchContactListStaticData(
        featureType: ContactListFeatureType,
    ): Flow<RestClientResult<ApiResponseWrapper<ContactListStaticDataResponse?>>>

}
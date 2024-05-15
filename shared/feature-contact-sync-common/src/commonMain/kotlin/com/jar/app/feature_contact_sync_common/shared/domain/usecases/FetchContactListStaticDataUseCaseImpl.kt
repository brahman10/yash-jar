package com.jar.app.feature_contact_sync_common.shared.domain.usecases

import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.data.repository.IContactsSyncRepository
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListStaticDataResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchContactListStaticDataUseCaseImpl constructor(
    private val iContactsSyncRepository: IContactsSyncRepository
) : FetchContactListStaticDataUseCase {
    override suspend fun fetchContactListStaticData(featureType: ContactListFeatureType): Flow<RestClientResult<ApiResponseWrapper<ContactListStaticDataResponse?>>> =
        iContactsSyncRepository.fetchContactListStaticData(featureType)

}
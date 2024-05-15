package com.jar.app.feature_contact_sync_common.shared.domain.usecases

import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.data.repository.IContactsSyncRepository

internal class FetchContactListUseCaseImpl constructor(
    private val iContactsSyncRepository: IContactsSyncRepository
) : FetchContactListUseCase {
    override suspend fun fetchContactListFlow(
        page: Int,
        size: Int,
        featureType: ContactListFeatureType,
        searchText: String?
    ) = iContactsSyncRepository.fetchContactListFlow(page, size, featureType, searchText)

    override suspend fun fetchContactList(
        page: Int,
        size: Int,
        featureType: ContactListFeatureType,
        searchText: String?
    ) = iContactsSyncRepository.fetchContactList(page, size, featureType, searchText)

}

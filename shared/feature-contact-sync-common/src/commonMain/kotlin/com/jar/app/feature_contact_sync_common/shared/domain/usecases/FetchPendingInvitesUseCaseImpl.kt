package com.jar.app.feature_contact_sync_common.shared.domain.usecases

import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.data.repository.IContactsSyncRepository

internal class FetchPendingInvitesUseCaseImpl constructor(
    private val repostiory: IContactsSyncRepository
) : FetchPendingInvitesUseCase {
    override suspend fun fetchPendingInvites(
        featureType: ContactListFeatureType,
    ) = repostiory.fetchPendingInvites(featureType)
}
package com.jar.app.feature_contact_sync_common.shared.domain.usecases

import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.data.repository.IContactsSyncRepository
import com.jar.app.feature_contact_sync_common.shared.domain.model.MultipleInviteRequest

internal class SendInviteUseCaseImpl constructor(private val repository: IContactsSyncRepository) :
    SendInviteUseCase {
    override suspend fun sendInvite(inviteePhoneNumber: String, featureType: ContactListFeatureType, referralLink: String) =
        repository.sendInvite(inviteePhoneNumber, featureType, referralLink)


    override suspend fun sendInviteReminder(inviteePhoneNumber: String, referralLink: String, featureType: ContactListFeatureType) =
        repository.sendInviteReminder(inviteePhoneNumber, referralLink, featureType)

    override suspend fun sendMultipleInvite(multipleInviteRequest: MultipleInviteRequest) =
        repository.sendMultipleInvite(multipleInviteRequest)
}
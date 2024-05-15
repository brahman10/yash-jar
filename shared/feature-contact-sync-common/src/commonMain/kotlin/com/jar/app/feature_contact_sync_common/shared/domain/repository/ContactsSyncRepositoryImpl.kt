package com.jar.app.feature_contact_sync_common.shared.domain.repository

import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.data.network.ContactsSyncDataSource
import com.jar.app.feature_contact_sync_common.shared.data.repository.IContactsSyncRepository
import com.jar.app.feature_contact_sync_common.shared.domain.model.AddContactRequest
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListStaticDataResponse
import com.jar.app.feature_contact_sync_common.shared.domain.model.MultipleInviteRequest
import com.jar.app.feature_contact_sync_common.shared.domain.model.ProcessInviteRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class ContactsSyncRepositoryImpl(
    private val dataSource: ContactsSyncDataSource
) : IContactsSyncRepository {
    override suspend fun fetchContactListFlow(
        page: Int,
        size: Int,
        featureType: ContactListFeatureType,
        searchText: String?
    ) = getFlowResult { dataSource.fetchContactList(page, size, featureType, searchText) }

    override suspend fun fetchContactList(
        page: Int,
        size: Int,
        featureType: ContactListFeatureType,
        searchText: String?
    ) = dataSource.fetchContactList(page, size, featureType, searchText)

    override suspend fun sendInvite(
        inviteePhoneNumber: String,
        featureType: ContactListFeatureType,
        referralLink: String
    ) =
        getFlowResult {
            dataSource.sendInvite(inviteePhoneNumber, featureType, referralLink)
        }

    override suspend fun fetchContactListStaticData(featureType: ContactListFeatureType): Flow<RestClientResult<ApiResponseWrapper<ContactListStaticDataResponse?>>> =
        getFlowResult {
            dataSource.fetchContactListStaticData(featureType)
        }

    override suspend fun sendInviteReminder(inviteePhoneNumber: String, referralLink: String, featureType: ContactListFeatureType) =
        getFlowResult {
            dataSource.sendInviteReminder(
                inviteePhoneNumber = inviteePhoneNumber,
                referralLink = referralLink,
                featureType = featureType
            )
        }

    override suspend fun sendMultipleInvite(multipleInviteRequest: MultipleInviteRequest) =
        getFlowResult {
            dataSource.sendMultipleInvite(multipleInviteRequest)
        }


    override suspend fun fetchContactProcessingStatus() = getFlowResult {
        dataSource.fetchContactProcessingStatus()
    }

    override suspend fun addContacts(addContactRequest: AddContactRequest) = getFlowResult {
        dataSource.addContacts(addContactRequest)
    }

    override suspend fun fetchSentInviteListList(
        page: Int,
        size: Int,
        featureType: ContactListFeatureType
    ) = dataSource.fetchSentInvites(page, size, featureType)

    override suspend fun processInvite(processInviteRequest: ProcessInviteRequest) = getFlowResult {
        dataSource.processInvite(processInviteRequest)
    }


    override suspend fun fetchPendingInvites(featureType: ContactListFeatureType,) = getFlowResult {
        dataSource.fetchPendingInvites(featureType)
    }
}
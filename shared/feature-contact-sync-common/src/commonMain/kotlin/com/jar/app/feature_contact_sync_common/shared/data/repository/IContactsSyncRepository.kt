package com.jar.app.feature_contact_sync_common.shared.data.repository

import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListResponse
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.domain.model.AddContactRequest
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListStaticDataResponse
import com.jar.app.feature_contact_sync_common.shared.domain.model.MultipleInviteRequest
import com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteResponse
import com.jar.app.feature_contact_sync_common.shared.domain.model.ProcessInviteRequest
import com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteListResponse
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface IContactsSyncRepository : BaseRepository {
    suspend fun fetchContactListFlow(
        page: Int,
        size: Int,
        featureType: ContactListFeatureType,
        searchText: String?
    ): Flow<RestClientResult<ApiResponseWrapper<ContactListResponse?>>>

    suspend fun fetchContactList(
        page: Int,
        size: Int,
        featureType: ContactListFeatureType,
        searchText: String?
    ): RestClientResult<ApiResponseWrapper<ContactListResponse?>>


    suspend fun sendInvite(inviteePhoneNumber: String, featureType: ContactListFeatureType, referralLink: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
    suspend fun fetchContactListStaticData(featureType: ContactListFeatureType): Flow<RestClientResult<ApiResponseWrapper<ContactListStaticDataResponse?>>>

    suspend fun sendInviteReminder(inviteePhoneNumber: String, referralLink: String, featureType: ContactListFeatureType): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun sendMultipleInvite(multipleInviteRequest: MultipleInviteRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>


    suspend fun fetchContactProcessingStatus(): Flow<RestClientResult<ApiResponseWrapper<String>>>

    suspend fun addContacts(addContactRequest: AddContactRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchSentInviteListList(
        page: Int,
        size: Int,
        featureType: ContactListFeatureType
    ): RestClientResult<ApiResponseWrapper<SentInviteListResponse?>>

    suspend fun processInvite(processInviteRequest: ProcessInviteRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchPendingInvites(featureType: ContactListFeatureType,): Flow<RestClientResult<ApiResponseWrapper<PendingInviteResponse>>>
}
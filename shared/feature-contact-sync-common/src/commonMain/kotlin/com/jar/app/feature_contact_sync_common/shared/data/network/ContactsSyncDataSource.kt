package com.jar.app.feature_contact_sync_common.shared.data.network

import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.domain.model.AddContactRequest
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListResponse
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListStaticDataResponse
import com.jar.app.feature_contact_sync_common.shared.domain.model.MultipleInviteRequest
import com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteResponse
import com.jar.app.feature_contact_sync_common.shared.domain.model.ProcessInviteRequest
import com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteListResponse
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.Endpoints
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class ContactsSyncDataSource constructor(
    private val client: HttpClient,
) : BaseDataSource() {

    suspend fun fetchContactProcessingStatus() = getResult<ApiResponseWrapper<String>> {
        client.get {
            url(Endpoints.FETCH_CONTACT_PROCESSING_STATUS)
        }
    }

    suspend fun processInvite(
        processInviteRequest: ProcessInviteRequest
    ) = getResult<ApiResponseWrapper<Unit?>> {
        client.post {
            url(Endpoints.PROCESS_INVITE)
            setBody(processInviteRequest)
        }
    }

    suspend fun fetchPendingInvites(featureType: ContactListFeatureType) =
        getResult<ApiResponseWrapper<PendingInviteResponse>> {
            client.get {
                parameter("featureType", featureType)
                parameter("page", 0)
                parameter("size", 20)
                url(Endpoints.FETCH_PENDING_INVITES)
            }
        }

    suspend fun fetchSentInvites(pageNo: Int, pageSize: Int, featureType: ContactListFeatureType) =
        getResult<ApiResponseWrapper<SentInviteListResponse?>> {
            client.get {
                url(Endpoints.FETCH_SENT_INVITES)
                parameter("page", pageNo)
                parameter("size", pageSize)
                parameter("featureType", featureType)
            }
        }

    suspend fun fetchContactList(
        page: Int,
        size: Int,
        featureType: ContactListFeatureType,
        searchText: String?
    ) = getResult<ApiResponseWrapper<ContactListResponse?>> {
        client.get {
            url(Endpoints.FETCH_CONTACT_LIST)
            parameter("page", page)
            parameter("size", size)
            parameter("featureType", featureType)
            if (searchText.isNullOrBlank().not())
                parameter("searchText", searchText)
        }
    }

    suspend fun fetchContactListStaticData(
        featureType: ContactListFeatureType,
    ) = getResult<ApiResponseWrapper<ContactListStaticDataResponse?>> {
        client.get {
            url(Endpoints.FETCH_CONTACT_LIST_STATIC_DATA)
            parameter("featureType", featureType)
        }
    }

    suspend fun addContacts(addContactRequest: AddContactRequest) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.ADD_CONTACTS)
                setBody(addContactRequest)
            }
        }

    suspend fun sendInvite(
        inviteePhoneNumber: String,
        featureType: ContactListFeatureType,
        referralLink: String
    ) = getResult<ApiResponseWrapper<Unit?>> {
        client.put {
            url(Endpoints.SEND_DUO_INVITE)
            parameter("inviteePhoneNumber", inviteePhoneNumber)
            parameter("featureType", featureType)
            parameter("referralLink", referralLink)
        }
    }

    suspend fun sendInviteReminder(inviteePhoneNumber: String, referralLink: String, featureType: ContactListFeatureType, ) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.put {
                url(Endpoints.SEND_DUO_REMINDER)
                parameter("inviteePhoneNumber", inviteePhoneNumber)
                parameter("referralLink", referralLink)
                parameter("featureType", featureType)
            }
        }

    suspend fun sendMultipleInvite(multipleInviteRequest: MultipleInviteRequest) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.SEND_MULTIPLE_INVITES)
                setBody(multipleInviteRequest)
            }
        }
}
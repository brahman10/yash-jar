package com.jar.app.feature_contact_sync_common.shared.domain.usecases

import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.domain.model.MultipleInviteRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface SendInviteUseCase {
    suspend fun sendInvite(
        inviteePhoneNumber: String,
        featureType: ContactListFeatureType,
        referralLink: String
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun sendMultipleInvite(multipleInviteRequest: MultipleInviteRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
    suspend fun sendInviteReminder(
        inviteePhoneNumber: String,
        referralLink: String,
        featureType: ContactListFeatureType
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

}
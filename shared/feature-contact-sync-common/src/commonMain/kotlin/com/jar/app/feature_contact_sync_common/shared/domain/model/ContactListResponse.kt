package com.jar.app.feature_contact_sync_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ContactListResponse(
    @SerialName("duoContactsListRespList")
    val duoContactsListRespList: List<ServerContact>,
    @SerialName("totalContacts")
    val totalContacts: Int? = null,
    @SerialName("totalContactsOnJar")
    val totalContactsOnJar: Int? = null,
    @SerialName("isContactSynced")
    val isContactSynced: Boolean? = null
) : Parcelable
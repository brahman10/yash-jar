package com.jar.app.feature_contact_sync_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ContactListStaticDataResponse(
    @SerialName("contactListText")
    val contactListText: ContactListText? = null,
    @SerialName("isContactSynced")
    val isContactSynced: Boolean? = null,
    @SerialName("showListHeaders")
    val showListHeaders: Boolean? = null,
    @SerialName("lottieBanner")
    val lottieBanner: String? = null,
    @SerialName("bannerBg")
    val bannerBg: String? = null,
    @SerialName("bannerRightImage")
    val bannerRightImage: String? = null,
    @SerialName("buttonTint")
    val buttonTint: String? = null,
    @SerialName("totalContacts")
    val totalContacts: Int? = null,
    @SerialName("totalContactsOnJar")
    val totalContactsOnJar: Int? = null,
) : Parcelable
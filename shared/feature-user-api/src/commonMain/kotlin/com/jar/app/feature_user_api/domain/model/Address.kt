package com.jar.app.feature_user_api.domain.model

import com.jar.app.feature_user_api.shared.MR
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Address(
    @SerialName("name")
    var name: String? = null,

    @SerialName("phoneNumber")
    var phoneNumber: String? = null,

    @SerialName("pinCode")
    val pinCode: String,

    @SerialName("address1")
    val address1: String,

    @SerialName("address2")
    val address2: String? = null,

    @SerialName("city")
    val city: String,

    @SerialName("state")
    val state: String,

    @SerialName("address")
    val address: String? = null,

    @SerialName("addressId")
    val addressId: String? = null,

    @SerialName("addressCategory")
    var addressCategory: String? = null,

    @SerialName("latitude")
    val latitude: String? = null,

    @SerialName("longitude")
    val longitude: String? = null,

    @SerialName("landmark")
    val landmark: String? = null,

    @SerialName("tags")
    val tags: AddressTags? = null,

    //For UI purpose
    var isSelected: Boolean = false,

    var isEditable: Boolean = false,

    val status: String? = null
) : Parcelable {
    fun getAddressCategory(): AddressType {
        return AddressType.values().find { it.name == addressCategory }
            ?: AddressType.DEFAULT
    }
}

@Parcelize
@Serializable
data class AddressTags(
    @SerialName("LOAN")
    val LOAN: List<String>? = null
) : Parcelable

enum class AddressType(val addressCategory: StringResource) {
    RECENTLY_SAVED(MR.strings.feature_lending_recently_saved),
    PERMANENT(MR.strings.feature_user_api_linked_to_you_aadhar_card),
    DEFAULT(MR.strings.feature_user_api_saved_address)
}
package com.jar.app.feature_kyc.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class KycDocListResponse(
    @SerialName("kycAlternateDocs")
    val kycAlternateDocs: KycAlternateDocs,
)

@kotlinx.serialization.Serializable
data class KycAlternateDocs(
    @SerialName("title")
    val title: String? = null,

    @SerialName("kycDoc")
    val kycDoc: List<KycDoc>
)

@Parcelize
@kotlinx.serialization.Serializable
data class KycDoc(
    @SerialName("title")
    val title: String,

    @SerialName("icon")
    val icon: String,

    @SerialName("documentType")
    val documentType: String,

    @SerialName("disable")
    val disable: Boolean
) : Parcelable {
    fun getDocType(): DocType {
        return DocType.values().find { it.name == documentType } ?: DocType.DEFAULT
    }
}
enum class DocType(
    val uploadTitle: StringResource,
    val uploadDescription: StringResource,
) {
    PAN(
        uploadTitle = com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_pan_card,
        uploadDescription = com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_pan_details_sub_heading
    ),
    AADHAAR(
        uploadTitle = com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_aadhar_card,
        uploadDescription = com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_aadhar_card_des
    ),
    LICENSE(
        uploadTitle = com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_driving_license,
        uploadDescription = com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_driving_license_des
    ),
    VOTER_ID(
        uploadTitle = com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_voter_id,
        uploadDescription = com.jar.app.feature_kyc.shared.MR.strings.feature_kuc_upload_voter_id_des
    ),
    PASSPORT(
        uploadTitle = com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_passport,
        uploadDescription = com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_passport_des
    ),
    DEFAULT(
        uploadTitle = com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_document,
        uploadDescription = com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_upload_document
    )
}
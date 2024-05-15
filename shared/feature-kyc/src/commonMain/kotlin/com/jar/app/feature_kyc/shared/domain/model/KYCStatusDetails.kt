package com.jar.app.feature_kyc.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class KYCStatusDetails(
    @SerialName("verificationStatus")
    val verificationStatus: String? = null,

    @SerialName("failureReason")
    val failureReason: String? = null,

    @SerialName("verifiedOn")
    val verifiedOn: String? = null,

    @SerialName("panData")
    val panData: KYCStatusInfo? = null,

    @SerialName("kycDetails")
    val kycDetails: KycDetails? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("contactShareMessage")
    val shareMsg: String? = null,

    @SerialName("errorMessage")
    val errorMessage: String? = null,

    @SerialName("allRetryExhausted")
    val allRetryExhausted: Boolean = false,

    @SerialName("remainingRetryCount")
    val remainingRetryCount: Int? = null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class KYCStatusInfo(
    @SerialName("panNumber")
    val panNumber: String,

    @SerialName("dob")
    val dob: String,

    @SerialName("name")
    val name: String,

    @SerialName("docType")
    val docType: String?=null,

    @SerialName("docId")
    val docId: String?=null
) : Parcelable {
    fun getKycDocType(): KYCDocType {
        return KYCDocType.values().find { it.name == docType } ?: KYCDocType.DEFAULT
    }
    fun getHiddenDocId(): String {
        var hiddenDocId = ""
        if (docId==null)return hiddenDocId
        for (index in docId.indices){
            if(index != 0 && index != docId.length-1){
                hiddenDocId += "*"
            }else{
                hiddenDocId += docId[index]
            }
        }
        return hiddenDocId
    }
}

@Parcelize
@kotlinx.serialization.Serializable
data class KycDetails(
    @SerialName("dob")
    val dob: String,

    @SerialName("name")
    val name: String,

    @SerialName("docType")
    val docType: String,

    @SerialName("docId")
    val docId: String
): Parcelable

enum class KYCDocType(
    val docType: StringResource,
    val showMsg: StringResource,
    val hideMsg: StringResource,
    val docName: StringResource
) {
    PAN(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_pan_number, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_show_pan_number, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_hide_pan_number, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_pan_header),
    LICENSE(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_driving_license, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_show_driving_license, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_hide_driving_license, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_dl_header),
    AADHAAR(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_aadhaar_number, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_show_aadhaar_number, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_hide_aadhaar_number, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_aadhar_header),
    VOTER_ID(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_voter_id, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_show_voter_id, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_hide_voter_id, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_voter_id_header),
    PASSPORT(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_passport_number, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_show_passport_number, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_hide_passport_number, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_passport_header),
    DEFAULT(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_empty, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_empty, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_empty, com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_empty)
}

enum class KycVerificationStatus {
    PENDING,
    VERIFIED,
    FAILED,
    RETRY
}


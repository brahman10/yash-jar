package com.jar.app.feature_kyc.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class KycOcrResponse(
    @SerialName("docId")
    val docId: String? = null,

    @SerialName("documentId")
    val documentId: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("dob")
    val dob: String? = null,

    @SerialName("yob")
    val yob: String? = null,

    @SerialName("docType")
    val docType: String,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("errorImage")
    val errorImage: String? = null,

    @SerialName("errorMsg")
    val errorMsg: String? = null
) : Parcelable {

    fun getDocNumber() = docId ?: documentId ?: ""

    fun getDocType(): DocType {
        return DocType.values().find { it.name == docType } ?: DocType.DEFAULT
    }
}
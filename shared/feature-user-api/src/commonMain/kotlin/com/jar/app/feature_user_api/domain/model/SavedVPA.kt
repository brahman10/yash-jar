package com.jar.app.feature_user_api.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SavedVPA(
    @SerialName("id")
    val id: String,
    @SerialName("vpaHandle")
    val vpaHandle: String,
    @SerialName("isDeleted")
    val isDeleted: Boolean? = null,
    @SerialName("isDefault")
    val isDefault: Boolean? = null,
    @SerialName("isVerified")
    val isVerified: Boolean? = null,
    @SerialName("autopay")
    val autopay: Boolean? = null,
    @SerialName("isPrimaryUpi")
    val isPrimaryUpi: Boolean? = null,
    //Added for UI purpose
    @SerialName("isSelected")
    var isSelected: Boolean? = null
)

@kotlinx.serialization.Serializable
data class SavedVpaResponse(
    @SerialName("payoutSavedVpas")
    val payoutSavedVpas: List<SavedVPA>
)
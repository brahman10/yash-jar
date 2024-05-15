package com.jar.app.feature_lending.shared.domain.model.temp

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class MandateData(
    @SerialName("status")
    val status: String? = null,

    @SerialName("mandateLink")
    val mandateLink: String? = null,

    @SerialName("mandateStatus")
    val mandateStatus: String? = null,

    @SerialName("provider")
    val provider: String? = null
) : Parcelable {

    fun getMandateStatus(): MandateStatus {
        return try {
            MandateStatus.valueOf(status.orEmpty())
        } catch (e: Exception) {
            MandateStatus.FAILED
        }
    }
}

enum class MandateStatus {
    PENDING,
    IN_PROGRESS,
    VERIFIED,
    FAILED
}
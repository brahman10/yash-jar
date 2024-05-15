package com.jar.app.feature_lending.shared.domain.model.temp

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class LendingEligibility(
    @SerialName("charges")
    val charges: List<LendingCharge>? = null,

    @SerialName("limits")
    val limits: LoanLimits,

    @SerialName("interestRate")
    val interestRate: Float,

    @SerialName("interestType")
    val interestType: String,

    @SerialName("maxEmiAllowed")
    val maxEmiAllowed: Float,

    @SerialName("maxTenure")
    val maxTenure: Int,

    @SerialName("minTenure")
    val minTenure: Int,

    @SerialName("status")
    val status: String,

    @SerialName("maxDrawdown")
    val maxDrawdown: Float,

    @SerialName("minDrawdown")
    val minDrawdown: Float,

    @SerialName("creditScore")
    val creditScore: String? = null,

    @SerialName("currentTimeStamp")
    val currentTimeStamp: Long? = null
) : Parcelable {
    fun getLendingStatus(): LendingStatus {
        return LendingStatus.values().find { it.name == status } ?: LendingStatus.Default
    }
}

@Parcelize
@kotlinx.serialization.Serializable
data class LendingCharge(
    @SerialName("chargeCalculationType")
    val chargeCalculationType: String? = null,

    @SerialName("chargeType")
    val chargeType: String? = null,

    @SerialName("chargeValue")
    val chargeValue: Float? = null
) : Parcelable

enum class LendingStatus {
    Approved,
    Pending,
    Created,
    Rejected,
    Default //Status Not Found
}
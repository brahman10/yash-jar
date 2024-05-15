package com.jar.app.feature_one_time_payments_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Voucher(
    @SerialName("amount")
    val amount: Int?,
    @SerialName("code")
    val code: String?,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("voucherName")
    val voucherName: String?
) : Parcelable
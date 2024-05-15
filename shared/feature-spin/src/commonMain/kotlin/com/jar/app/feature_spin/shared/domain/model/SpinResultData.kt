package com.jar.app.feature_spin.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
@Parcelize
data class SpinResultData(
    @SerialName("outCome")
    val outCome: Int? = null,

    @SerialName("outcomeType")
    val outcomeType: String? = null,

    @SerialName("shareMsg")
    val shareMsg: String? = null,

    @SerialName("spinCouponMetadata")
    val spinCouponMetadata: SpinCouponMetadata? = null
) : Parcelable

@kotlinx.serialization.Serializable
@Parcelize
data class SpinCouponMetadata(
    @SerialName("message")
    val message: String? = null,

    @SerialName("couponName")
    val couponName: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("validity")
    val validity: Long? = null,
) : Parcelable
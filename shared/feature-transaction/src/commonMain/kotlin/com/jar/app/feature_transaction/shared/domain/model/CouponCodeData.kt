package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CouponCodeData(
    @SerialName("couponCode")
    val couponCode: String? = null,
    @SerialName("couponCodeDescription")
    val couponCodeDescription: String? = null,

    override val uniqueKey: String = couponCode?.plus(couponCodeDescription).orEmpty()
) : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.COUPON_CODE
    }

    override fun equals(other: Any?): Boolean {
        return other is CouponCodeData
    }
}
package com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page

import com.jar.app.feature_coupon_api.domain.model.CouponCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponCodeResponseForMandateScreenItem(
    @SerialName("couponCodes")
    val couponCode: CouponCode
) : BasePaymentPageItem {
    override val uniqueId: String
        get() = couponCode.couponCodeId.orEmpty()

}

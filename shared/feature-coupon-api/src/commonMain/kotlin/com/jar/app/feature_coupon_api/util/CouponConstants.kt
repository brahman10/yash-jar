package com.jar.app.feature_coupon_api.util

object CouponConstants {
    //Do not change the value unless changed from BE!!!
    const val MAX_REWARD_TEMPLATE_FOR_COUPON_DESCRIPTION = "rewardsApplicable"

    internal object Endpoints {
        const val FETCH_COUPON_CODES = "v1/api/couponCodes"
        const val APPLY_COUPON_CODE = "v1/api/couponCodes/apply"
        const val FETCH_JAR_COUPONS = "v1/api/offers/jarCoupons/fetch"
        const val FETCH_BRAND_COUPONS = "v1/api/offers/otherBrands/fetch"
        const val FETCH_COUPON_DETAILS = "v1/api/offers/otherBrands/couponDetails"
    }

    enum class MandatePaymentContext {
        SAVE_DAILY
    }
}
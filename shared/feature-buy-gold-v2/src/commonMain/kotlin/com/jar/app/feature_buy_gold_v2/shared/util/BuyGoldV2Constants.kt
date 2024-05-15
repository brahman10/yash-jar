package com.jar.app.feature_buy_gold_v2.shared.util

object BuyGoldV2Constants {
    const val BUY_GOLD_CROSS_PROMOTION = "BUY_GOLD_CROSS_PROMOTION"
    const val INITIATE_BUY_GOLD_DATA = "INITIATE_BUY_GOLD_DATA"
    const val NO_CODE = "NO_CODE"
    const val UNIT_GM = "gm"
    const val UNIT_RS = "rs"

    const val DEFAULT_MAX_PAYMENT_METHODS_COUNT = 5
    const val EXIT_BUY_GOLD_FLOW = "EXIT_BUY_GOLD_FLOW"
    //Do not change the value unless changed from BE!!!
    const val MAX_REWARD_TEMPLATE_FOR_COUPON_DESCRIPTION = "rewardsApplicable"

    object ApiTypeForAnalytics {
        const val FETCH_COUPON_CODES = "FETCH_COUPON_CODES"
        const val FETCH_SUGGESTED_AMOUNT = "FETCH_SUGGESTED_AMOUNT"
        const val FETCH_BANNER_CONTEXT = "FETCH_BANNER_CONTEXT"
    }

    internal object Endpoints {
        const val FETCH_BUY_GOLD_STATIC_INFO = "v2/api/dashboard/static"
        const val FETCH_COUPON_CODES = "v1/api/couponCodes"
        const val APPLY_COUPON_CODE = "v1/api/couponCodes/apply"
        const val FETCH_AUSPICIOUS_DATES = "v2/api/gold/auspiciousDates"
        const val FETCH_IS_AUSPICIOUS_TIME = "v2/api/gold/isAuspicious"
        const val BUY_GOLD_MANUAL = "v3/api/gold/buy/manual"
        const val FETCH_JAR_COUPONS = "v1/api/offers/jarCoupons/fetch"
        const val FETCH_BRAND_COUPONS = "v1/api/offers/otherBrands/fetch"
        const val FETCH_COUPON_DETAILS = "v1/api/offers/otherBrands/couponDetails"
        const val FETCH_CONTEXT_BANNER = "/v1/api/buyGold/getContextBanner"
    }
}
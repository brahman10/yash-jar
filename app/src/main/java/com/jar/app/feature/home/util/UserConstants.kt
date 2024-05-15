package com.jar.app.feature.home.util

internal object UserConstants {

    internal object Endpoints {
        const val APPLY_PROMO_CODE = "v1/api/promoCode/apply"
        const val UPDATE_USER_DEVICE_DETAIL = "v2/api/user/device"
        const val VERIFY_PHONE_NUMBER = "v2/api/user/update/phone/verify"
        const val UPDATE_FCM_TOKEN = "v1/api/user/fcmToken"
        const val FETCH_VPA_CHIPS = "v2/api/payouts/vpa/chips"
        const val FETCH_USER_SAVED_VPA = "v2/api/user/vpa/all"
        const val ADD_NEW_VPA = "v2/api/user/vpa/add/vpa"
        const val SUBMIT_USER_REVIEW = "v1/api/user/review"
        const val GET_USER_RATING = "v1/api/user/review"
    }
}
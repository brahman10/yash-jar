package com.jar.app.feature_profile.util

object Constants {
    const val PROFILE = "Profile"
    const val DEFAULT_COUNTRY_CODE = "91"

    internal object Endpoints {
        const val REQUEST_OTP = "v2/api/auth/requestOTP"
        const val REQUEST_OTP_VIA_CALL = "v2/api/auth/sendOTP/call"
        const val FETCH_DASHBOARD_STATIC_CONTENT = "v2/api/dashboard/static"
    }
}
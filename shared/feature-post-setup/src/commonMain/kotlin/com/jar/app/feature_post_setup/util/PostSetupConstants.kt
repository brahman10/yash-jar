package com.jar.app.feature_post_setup.util

object PostSetupConstants {

    const val SUCCESSFUL_TRANSACTION_CALLBACK = "SUCCESSFUL_TRANSACTION_CALLBACK"

    internal object Endpoints {
        const val FETCH_POST_SETUP_USER_DATA = "v1/api/postSetup"
        const val FETCH_POST_SETUP_CALENDAR_DATA = "v1/api/postSetup/info"
        const val FETCH_POST_SETUP_QUICK_ACTIONS = "v1/api/postSetup/staticContent"
        const val FETCH_POST_SETUP_SAVING_OPERATION = "v1/api/postSetup/staticContent"
        const val FETCH_POST_SETUP_FAQ = "v1/api/postSetup/staticContent"
        const val INITIATE_PAYMENT_FOR_FAILED_TRANSACTIONS = "v2/api/payments/initiate"
        const val FETCH_POST_SETUP_FAILURE_INFO = "v1/api/postSetup/failureInfo"
    }
}
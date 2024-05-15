package com.jar.app.feature_vasooli.impl.util

internal object VasooliConstants {

    const val SELF = "SELF"

    const val JAR = "JAR"

    const val MAX_AMOUNT = 1000000

    internal object Endpoints {
        const val FETCH_VASOOLI_OVERVIEW = "v1/api/vasooli/overview"
        const val FETCH_VASOOLI_LENT_LIST = "v1/api/vasooli/handloans/lent"
        const val POST_VASOOLI_REQUEST = "v1/api/vasooli/request"
        const val FETCH_VASOOLI_REPAYMENT_HISTORY = "v1/api/vasooli/repayments"
        const val POST_REPAYMENT_ENTRY_REQUEST = "v1/api/vasooli/add/repayment"
        const val UPDATE_VASOOLI_STATUS = "v1/api/vasooli/update/status"
        const val DELETE_VASOOLI_ENTRY = "v1/api/vasooli/delete"
        const val FETCH_LOAD_DETAILS = "v1/api/vasooli/handloans/lent/individual"
        const val UPDATE_VASOOLI_ENTRY = "v1/api/vasooli/update/details"
        const val FETCH_VASOOLI_REMINDER = "v1/api/vasooli/reminder"
        const val FETCH_VASOOLI_REMINDER_IMAGE = "v1/api/vasooli/reminder/changeimage"
        const val SEND_VASOOLI_REMINDER = "v1/api/vasooli/send/reminder"
    }


}
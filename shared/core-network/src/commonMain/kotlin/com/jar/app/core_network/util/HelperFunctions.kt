package com.jar.app.core_network.util

import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jar_core_network.api.util.Constants

private val INTERNAL_SERVER_ERROR = 500..510

fun checkErrorCode(errorCode: String?): Boolean {
    return errorCode in setOf<String>(
        Constants.NetworkErrorCodes.INTERNET_NOT_WORKING.toString(),
        Constants.NetworkErrorCodes.NETWORK_CALL_CANCELLED.toString(),
    ) || (INTERNAL_SERVER_ERROR.contains(errorCode?.toIntOrNull().orZero()))
}
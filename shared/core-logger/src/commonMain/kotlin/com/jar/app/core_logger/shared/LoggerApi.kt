package com.jar.app.core_logger.shared

interface LoggerApi {

    fun logD(message: String)

    fun logDWithTag(tag: String, message: String)

    fun logE(message: String, e: Exception)

    fun logEWithTag(tag: String, message: String, e: Exception)
}
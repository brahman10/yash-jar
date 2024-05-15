package com.jar.app.core_logger.shared.di

import com.jar.app.core_logger.shared.LoggerApi
import com.jar.app.core_logger.shared.LoggerApiImpl

class LoggerModule(
    shouldEnableLogs: Boolean
) {

    val loggerApi: LoggerApi by lazy {
        LoggerApiImpl(shouldEnableLogs = shouldEnableLogs)
    }
}
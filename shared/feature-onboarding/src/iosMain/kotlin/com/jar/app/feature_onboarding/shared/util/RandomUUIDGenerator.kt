package com.jar.app.feature_onboarding.shared.util

import platform.Foundation.NSUUID

actual class RandomUUIDGenerator {
    actual companion object{
        actual fun randomUUID(): String = NSUUID().UUIDString()
    }
}
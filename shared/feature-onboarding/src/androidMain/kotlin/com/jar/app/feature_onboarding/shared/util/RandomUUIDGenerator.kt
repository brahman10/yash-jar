package com.jar.app.feature_onboarding.shared.util

import java.util.UUID

actual class RandomUUIDGenerator {
    actual companion object{
        actual fun randomUUID() = UUID.randomUUID().toString()
    }
}
package com.jar.app.feature_onboarding.shared.util

expect class RandomUUIDGenerator{
    companion object{
        fun randomUUID(): String
    }
}

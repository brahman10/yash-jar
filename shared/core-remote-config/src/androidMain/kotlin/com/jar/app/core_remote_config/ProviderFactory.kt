package com.jar.app.core_remote_config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig

internal object ProviderFactory {

    private val firebaseRemoteConfig by lazy {
        Firebase.remoteConfig
    }

    internal fun getRemoteConfig(): FirebaseRemoteConfig = firebaseRemoteConfig
}
package com.jar.app.core_remote_config

import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.jar.internal.library.jar_core_remote_config.impl.initializer.data.InitializerApi
import com.jar.internal.library.jar_core_remote_config.impl.initializer.model.ConfigSettings

class InitializerApiImpl : InitializerApi {

    private val firebaseRemoteConfig = ProviderFactory.getRemoteConfig()

    override fun initialize(
        configSettings: ConfigSettings,
        onSuccess: () -> Unit,
        onError: (exception: Exception) -> Unit
    ) {
        firebaseRemoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(configSettings.minimumFetchIntervalInSeconds)
                .build()
        )
        val task = firebaseRemoteConfig.fetchAndActivate()
        task.addOnSuccessListener {
            onSuccess.invoke()
        }
        task.addOnFailureListener {
            onError.invoke(it)
        }
    }

    override fun setDefaults(
        defaults: List<Pair<String, Any?>>?,
        onSuccess: () -> Unit,
        onError: (exception: Exception) -> Unit
    ) {
        val task = firebaseRemoteConfig.setDefaultsAsync(defaults?.toMap().orEmpty())
        task.addOnSuccessListener {
            onSuccess.invoke()
        }
        task.addOnFailureListener {
            onError.invoke(it)
        }
    }

}
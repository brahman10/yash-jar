package com.jar.app.worker

import com.jar.internal.library.jar_core_remote_config.api.ConfigApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ConfigInitializer @Inject constructor(
    private val appScope: CoroutineScope,
    private val configApi: ConfigApi
) {

    fun initialize(defaults: List<Pair<String, String>>) {
        appScope.launch {
            configApi.setDefaults(defaults, {}, {})
        }
    }

    fun fetchConfig(
        onSuccess: () -> Unit = {},
        onError: (e: Exception) -> Unit = {}
    ) {
        appScope.launch {
            configApi.fetchAndActivate(onSuccess, onError)
        }
    }

}
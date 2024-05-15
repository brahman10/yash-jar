package com.jar.app.core_remote_config

import com.jar.internal.library.jar_core_remote_config.api.ConfigApi
import com.jar.internal.library.jar_core_remote_config.impl.fetcher.data.FetcherApi
import com.jar.internal.library.jar_core_remote_config.impl.initializer.data.InitializerApi

class RemoteConfigModule(
    initializerApi: InitializerApi,
    fetcherApi: FetcherApi
) {

    val internalConfigApi: ConfigApi by lazy {
        ConfigApi
            .Builder()
            .setInitializerApiImpl(initializerApi)
            .setFetcherApiImpl(fetcherApi)
            .build()
    }
    
    val remoteConfigApi: RemoteConfigApi by lazy {
        RemoteConfigApiImpl(internalConfigApi)
    }
}
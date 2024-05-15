package com.jar.app.core_remote_config

import com.jar.internal.library.jar_core_remote_config.api.ConfigApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class RemoteConfigModule {

    @Provides
    @Singleton
    internal fun provideConfigApi(): ConfigApi {
        return ConfigApi
            .Builder()
            .setInitializerApiImpl(InitializerApiImpl())
            .setFetcherApiImpl(FetcherApiImpl())
            .build()
    }

    @Provides
    @Singleton
    internal fun provideRemoteConfigApi(configApi: ConfigApi): RemoteConfigApi {
        return RemoteConfigApiImpl(configApi)
    }

}
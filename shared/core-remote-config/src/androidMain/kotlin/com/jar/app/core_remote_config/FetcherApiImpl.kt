package com.jar.app.core_remote_config

import com.jar.internal.library.jar_core_remote_config.impl.fetcher.data.FetcherApi

class FetcherApiImpl : FetcherApi {

    private val firebaseRemoteConfig = ProviderFactory.getRemoteConfig()

    override fun getString(configKey: String) = firebaseRemoteConfig.getString(configKey)

    override fun getInt(configKey: String) = firebaseRemoteConfig.getLong(configKey).toInt()

    override fun getLong(configKey: String) = firebaseRemoteConfig.getLong(configKey)

    override fun getFloat(configKey: String) = firebaseRemoteConfig.getDouble(configKey).toFloat()

    override fun getDouble(configKey: String) = firebaseRemoteConfig.getDouble(configKey)

    override fun getBoolean(configKey: String) = firebaseRemoteConfig.getBoolean(configKey)

}
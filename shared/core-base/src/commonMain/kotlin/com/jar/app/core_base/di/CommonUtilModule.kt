package com.jar.app.core_base.di

import com.jar.app.core_base.util.RemoteConfigDefaultsHelper
import kotlinx.serialization.json.Json

class CommonUtilModule constructor(
    private val json: Json
) {

    val remoteConfigDefaultsHelper by lazy {
        RemoteConfigDefaultsHelper(json)
    }
}
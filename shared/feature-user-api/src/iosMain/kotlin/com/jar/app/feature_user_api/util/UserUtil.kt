package com.jar.app.feature_user_api.util

import com.jar.app.core_base.domain.model.User
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.internal.library.jar_core_network.api.util.Serializer

class UserUtil(
    private val prefsApi: PrefsApi,
    private val serializer: Serializer
) {

    fun fetchUser(): User? {
        val userString = prefsApi.getUserStringSync()
        if (userString.isNullOrBlank().not()) {
            return serializer.decodeFromString(userString!!)
        }
        return null
    }
}
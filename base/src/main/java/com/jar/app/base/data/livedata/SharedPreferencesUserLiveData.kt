package com.jar.app.base.data.livedata

import android.content.SharedPreferences
import com.jar.app.core_base.domain.model.User
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_preferences.api.PrefsApi
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class SharedPreferencesUserLiveData @Inject constructor(
    preferences: SharedPreferences,
    private val prefs: PrefsApi,
    private val serializer: Serializer
) : SharedPreferenceLiveData<User>(preferences, USER_KEY) {

    companion object {
        private const val USER_KEY = "USER_KEY"
    }

    override fun getValueFromPreferences(key: String): User? {
        val userString = prefs.getUserStringSync()
        return if (userString.isNullOrBlank().not()) {
            serializer.decodeFromString(userString!!)
        } else {
            null
        }
    }
}
package com.jar.app.core_preferences.di

import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_preferences.api.RetainedPrefsApi
import com.jar.app.core_preferences.impl.PrefsApiImpl
import com.jar.app.core_preferences.impl.RetainedPrefsApiImpl
import com.jar.internal.library.jar_core_preferences.api.PreferenceApi

class CommonPreferencesModule {

    val internalPrefsApi by lazy {
        PreferenceApi.getInstance()
    }

    val prefApi: PrefsApi by lazy {
        PrefsApiImpl(internalPrefsApi)
    }


    val internalRetainedPrefsApi by lazy {
        PreferenceApi.getInstance()
    }

    val retainedPrefApi: RetainedPrefsApi by lazy {
        RetainedPrefsApiImpl(internalRetainedPrefsApi)
    }
}
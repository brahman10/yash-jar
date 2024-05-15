package com.jar.app.base.di

import com.jar.app.core_preferences.api.PrefsApi
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PreferenceEntryPoint {
    fun getPrefs(): PrefsApi
}
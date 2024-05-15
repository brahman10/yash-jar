package com.jar.app.core_preferences.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.jar.app.core_preferences.impl.PrefsApiImpl
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_preferences.api.RetainedPrefsApi
import com.jar.app.core_preferences.di.qualifiers.RetainedPreferenceApi
import com.jar.app.core_preferences.di.qualifiers.SimplePreferenceApi
import com.jar.app.core_preferences.impl.RetainedPrefsApiImpl
import com.jar.internal.library.jar_core_preferences.api.PreferenceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class PreferencesModule {

    @Provides
    @Singleton
    internal fun provideCommonPreferencesModule(): CommonPreferencesModule {
        return CommonPreferencesModule()
    }

    @Provides
    @Singleton
    internal fun providePrefApi(
        commonPreferencesModule: CommonPreferencesModule
    ): PrefsApi {
        return PrefsApiImpl(commonPreferencesModule.internalPrefsApi)
    }

    @Provides
    @Singleton
    internal fun provideRetainedPrefApi(
        commonPreferencesModule: CommonPreferencesModule
    ): RetainedPrefsApi {
        return RetainedPrefsApiImpl(commonPreferencesModule.internalRetainedPrefsApi)
    }

    @Provides
    @Singleton
    internal fun preferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}
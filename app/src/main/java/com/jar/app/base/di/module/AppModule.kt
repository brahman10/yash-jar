package com.jar.app.base.di.module

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jar.app.JarApp
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideCrashlytics(): FirebaseCrashlytics {
        return FirebaseCrashlytics.getInstance()
    }

    @Provides
    @Singleton
    fun providePhoneNumberUtil(@ApplicationContext context: Context): PhoneNumberUtil {
        return PhoneNumberUtil.createInstance(context)
    }

    @Provides
    @Singleton
    fun provideAppScope(@ApplicationContext context: Context): CoroutineScope {
        return (context as JarApp).appScope
    }
}
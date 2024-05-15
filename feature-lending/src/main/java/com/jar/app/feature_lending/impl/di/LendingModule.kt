package com.jar.app.feature_lending.impl.di

import android.content.Context
import com.jar.app.feature_lending.impl.util.FileSelectorUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.lang.ref.WeakReference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class LendingModule {

    @Provides
    @Singleton
    internal fun provideFileSelectorUtil(@ApplicationContext context: Context): FileSelectorUtil {
        return FileSelectorUtil(WeakReference(context))
    }
}
package com.jar.app.core_base.di

import android.content.Context
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_base.util.EncryptionUtil
import com.jar.app.core_base.util.RemoteConfigDefaultsHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class UtilsModule {

    @Provides
    @Singleton
    internal fun provideCommonUtilModule(json: Json): CommonUtilModule {
        return CommonUtilModule(json)
    }

    @Provides
    @Singleton
    internal fun provideDeviceUtils(@ApplicationContext context: Context): DeviceUtils {
        return DeviceUtils(context)
    }

    @Provides
    @Singleton
    internal fun provideEncryptionUtils(): EncryptionUtil {
        return EncryptionUtil()
    }

    @Provides
    @Singleton
    internal fun provideRemoteConfigDefaultsHelper(commonUtilModule: CommonUtilModule): RemoteConfigDefaultsHelper {
        return commonUtilModule.remoteConfigDefaultsHelper
    }
}
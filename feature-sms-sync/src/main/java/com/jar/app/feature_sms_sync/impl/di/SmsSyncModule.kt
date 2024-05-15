package com.jar.app.feature_sms_sync.impl.di

import android.content.Context
import android.content.SharedPreferences
import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_sms_sync.impl.data.network.SmsSyncDataSource
import com.jar.app.feature_sms_sync.impl.data.repository.ISmsSyncRepository
import com.jar.app.feature_sms_sync.impl.domain.repository.SmsSyncRepositoryImpl
import com.jar.app.feature_sms_sync.impl.domain.usecases.ISendSmsToServerUseCase
import com.jar.app.feature_sms_sync.impl.domain.usecases.SendSmsToServerUseCaseImpl
import com.jar.app.feature_sms_sync.impl.utils.SmsSyncConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class SmsSyncModule {
    @Provides
    @Singleton
    @SmsSyncPreferences
    internal fun providePrefs(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SmsSyncConstants.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    internal fun provideApiService(
        @AppHttpClient client: HttpClient
    ): SmsSyncDataSource {
        return SmsSyncDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideSmsSyncRepository(smsSyncDataSource: SmsSyncDataSource): ISmsSyncRepository {
        return SmsSyncRepositoryImpl(smsSyncDataSource)
    }

    @Provides
    @Singleton
    internal fun provideSmsSyncUseCase(smsSyncRepository: ISmsSyncRepository): ISendSmsToServerUseCase {
        return SendSmsToServerUseCaseImpl(smsSyncRepository)
    }
}
package com.jar.app.feature_settings.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_settings.data.network.SettingsDataSource
import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.use_case.*
import com.jar.app.feature_settings.domain.use_case.impl.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class SettingsDataSourceModule {

    @Provides
    @Singleton
    internal fun provideCommonSettingsModule(@AppHttpClient client: HttpClient): CommonSettingsModule {
        return CommonSettingsModule(client)
    }
    
    @Provides
    @Singleton
    internal fun provideSettingsDataSource(commonSettingsModule: CommonSettingsModule): SettingsDataSource {
        return commonSettingsModule.settingsDataSource
    }

    @Provides
    @Singleton
    internal fun provideSettingsRepository(commonSettingsModule: CommonSettingsModule): SettingsRepository {
        return commonSettingsModule.settingsRepository
    }

    @Provides
    @Singleton
    internal fun provideFetchSupportedAppLanguagesUseCase(commonSettingsModule: CommonSettingsModule): FetchSupportedAppLanguagesUseCase {
        return commonSettingsModule.provideFetchSupportedAppLanguagesUseCase
    }


    @Provides
    @Singleton
    internal fun provideFetchIsSavingPausedUseCase(commonSettingsModule: CommonSettingsModule): FetchIsSavingPausedUseCase {
        return commonSettingsModule.provideFetchIsSavingPausedUseCase
    }

    @Provides
    @Singleton
    internal fun provideUpdateSavingPauseDurationUseCase(commonSettingsModule: CommonSettingsModule): UpdateSavingPauseDurationUseCase {
        return commonSettingsModule.provideUpdateSavingPauseDurationUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchVpaChipUseCase(commonSettingsModule: CommonSettingsModule): FetchVpaChipUseCase {
        return commonSettingsModule.provideFetchVpaChipUseCase
    }

    @Provides
    @Singleton
    internal fun provideVerifyUpiUseCase(commonSettingsModule: CommonSettingsModule): VerifyUpiUseCase {
        return commonSettingsModule.provideVerifyUpiUseCase
    }

    @Provides
    @Singleton
    internal fun provideAddNewUpiIdUseCase(commonSettingsModule: CommonSettingsModule): AddNewUpiIdUseCase {
        return commonSettingsModule.provideAddNewUpiIdUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchUserSavedCardsUseCase(commonSettingsModule: CommonSettingsModule): FetchUserSavedCardsUseCase {
        return commonSettingsModule.provideFetchUserSavedCardsUseCase
    }

    @Provides
    @Singleton
    internal fun provideAddNewCardUseCase(commonSettingsModule: CommonSettingsModule): AddNewCardUseCase {
        return commonSettingsModule.provideAddNewCardUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchCardBinInfoUseCase(commonSettingsModule: CommonSettingsModule): FetchCardBinInfoUseCase {
        return commonSettingsModule.provideFetchCardBinInfoUseCase
    }

    @Provides
    @Singleton
    internal fun provideDeleteSavedCardUseCase(commonSettingsModule: CommonSettingsModule): DeleteSavedCardUseCase {
        return commonSettingsModule.provideDeleteSavedCardUseCase
    }

    @Provides
    @Singleton
    internal fun provideDailyInvestmentCancellationV2UseCase(commonSettingsModule: CommonSettingsModule): DailyInvestmentCancellationV2RedirectionDetailsUseCase {
        return commonSettingsModule.provideDailyInvestmentCancellationV2UseCase
    }
}
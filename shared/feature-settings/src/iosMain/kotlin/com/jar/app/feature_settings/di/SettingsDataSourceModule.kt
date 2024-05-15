package com.jar.app.feature_settings.di

import com.jar.app.feature_settings.data.network.SettingsDataSource
import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.repository.SettingsRepositoryImpl
import com.jar.app.feature_settings.domain.use_case.AddNewCardUseCase
import com.jar.app.feature_settings.domain.use_case.AddNewUpiIdUseCase
import com.jar.app.feature_settings.domain.use_case.DeleteSavedCardUseCase
import com.jar.app.feature_settings.domain.use_case.FetchCardBinInfoUseCase
import com.jar.app.feature_settings.domain.use_case.FetchIsSavingPausedUseCase
import com.jar.app.feature_settings.domain.use_case.FetchSupportedAppLanguagesUseCase
import com.jar.app.feature_settings.domain.use_case.FetchUserSavedCardsUseCase
import com.jar.app.feature_settings.domain.use_case.FetchVpaChipUseCase
import com.jar.app.feature_settings.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_settings.domain.use_case.VerifyUpiUseCase
import com.jar.app.feature_settings.domain.use_case.impl.AddNewCardUseCaseImpl
import com.jar.app.feature_settings.domain.use_case.impl.AddNewUpiIdUseCaseImpl
import com.jar.app.feature_settings.domain.use_case.impl.DeleteSavedCardUseCaseImpl
import com.jar.app.feature_settings.domain.use_case.impl.FetchCardBinInfoUseCaseImpl
import com.jar.app.feature_settings.domain.use_case.impl.FetchIsSavingPausedUseCaseImpl
import com.jar.app.feature_settings.domain.use_case.impl.FetchSupportedAppLanguagesUseCaseImpl
import com.jar.app.feature_settings.domain.use_case.impl.FetchUserSavedCardsUseCaseImpl
import com.jar.app.feature_settings.domain.use_case.impl.FetchVpaChipUseCaseImpl
import com.jar.app.feature_settings.domain.use_case.impl.UpdateSavingPauseDurationUseCaseImpl
import com.jar.app.feature_settings.domain.use_case.impl.VerifyUpiUseCaseImpl
import io.ktor.client.HttpClient

class SettingsDataSourceModule(
    client: HttpClient
) {

    private val settingsDataSource: SettingsDataSource by lazy {
        SettingsDataSource(client)
    }


    private val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(settingsDataSource)
    }


    val fetchSupportedAppLanguagesUseCase: FetchSupportedAppLanguagesUseCase by lazy {
        FetchSupportedAppLanguagesUseCaseImpl(settingsRepository)
    }


    val fetchIsSavingPausedUseCase: FetchIsSavingPausedUseCase by lazy {
        FetchIsSavingPausedUseCaseImpl(settingsRepository)
    }


    val updateSavingPauseDurationUseCase: UpdateSavingPauseDurationUseCase by lazy {
        UpdateSavingPauseDurationUseCaseImpl(settingsRepository)
    }


    val fetchVpaChipUseCase: FetchVpaChipUseCase by lazy {
        FetchVpaChipUseCaseImpl(settingsRepository)
    }


    val verifyUpiUseCase: VerifyUpiUseCase by lazy {
        VerifyUpiUseCaseImpl(settingsRepository)
    }


    val addNewUpiIdUseCase: AddNewUpiIdUseCase by lazy {
        AddNewUpiIdUseCaseImpl(settingsRepository)
    }


    val fetchUserSavedCardsUseCase: FetchUserSavedCardsUseCase by lazy {
        FetchUserSavedCardsUseCaseImpl(settingsRepository)
    }


    val addNewCardUseCase: AddNewCardUseCase by lazy {
        AddNewCardUseCaseImpl(settingsRepository)
    }


    val fetchCardBinInfoUseCase: FetchCardBinInfoUseCase by lazy {
        FetchCardBinInfoUseCaseImpl(settingsRepository)
    }


    val deleteSavedCardUseCase: DeleteSavedCardUseCase by lazy {
        DeleteSavedCardUseCaseImpl(settingsRepository)
    }
}
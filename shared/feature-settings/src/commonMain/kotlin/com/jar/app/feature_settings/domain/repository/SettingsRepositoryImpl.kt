package com.jar.app.feature_settings.domain.repository

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_settings.data.network.SettingsDataSource
import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.model.CardDetail
import com.jar.app.feature_settings.domain.model.DailyInvestmentCancellationV2RedirectionDetails
import com.jar.app.feature_settings.domain.model.DeleteCard
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class SettingsRepositoryImpl constructor(
    private val settingsDataSource: SettingsDataSource
) : SettingsRepository {

    override suspend fun fetchSupportedLanguages() =
        getFlowResult { settingsDataSource.fetchSupportedLanguages() }

    override suspend fun fetchIsSavingPaused(pauseType: SavingsType) = getFlowResult {
        settingsDataSource.areSavingPaused(pauseType)
    }

    override suspend fun updateSavingPauseDuration(
        pause: Boolean,
        pauseDuration: String?,
        savingType: SavingsType
    ) = getFlowResult {
        settingsDataSource.updateSavingPauseDuration(
            pause,
            pauseDuration,
            savingType
        )
    }

    override suspend fun fetchVpaChips() = getFlowResult { settingsDataSource.fetchVpaChips() }

    override suspend fun verifyUpiAddress(upiAddress: String) = getFlowResult {
        settingsDataSource.verifyUpiAddress(upiAddress)
    }

    override suspend fun addNewUpiId(upiId: String)= getFlowResult {
        settingsDataSource.addNewUpiId(upiId)
    }

    override suspend fun fetchSavedCards() = getFlowResult {
        settingsDataSource.fetchUserSavedCards()
    }

    override suspend fun deleteSavedCard(deleteCard: DeleteCard) = getFlowResult {
        settingsDataSource.deleteSavedCard(deleteCard)
    }

    override suspend fun fetchDailySavingRedirectionDetails() = getFlowResult {
        settingsDataSource.dailyInvestmentCancellationV2RedirectionDetails()
    }

    override suspend fun addNewCard(cardDetail: CardDetail) = getFlowResult {
        settingsDataSource.addNewCard(cardDetail)
    }

    override suspend fun fetchCardBinInfo(cardBin: String) = getFlowResult {
        settingsDataSource.fetchCardBinInfo(cardBin)
    }
}
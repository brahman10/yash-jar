package com.jar.app.feature_settings.data.repository

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_settings.domain.model.*
import com.jar.app.feature_settings.domain.model.VerifyUpiResponse
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.app.feature_user_api.domain.model.SavedVPA
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface SettingsRepository : BaseRepository {

    suspend fun fetchSupportedLanguages(): Flow<RestClientResult<ApiResponseWrapper<LanguageList>>>

    suspend fun fetchIsSavingPaused(pauseType: SavingsType): Flow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>

    suspend fun updateSavingPauseDuration(
        pause: Boolean,
        pauseDuration: String?,
        savingType: SavingsType
    ): Flow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>

    suspend fun fetchVpaChips(): Flow<RestClientResult<ApiResponseWrapper<VpaChips>>>

    suspend fun verifyUpiAddress(upiAddress: String): Flow<RestClientResult<ApiResponseWrapper<VerifyUpiResponse>>>

    suspend fun addNewUpiId(upiId: String): Flow<RestClientResult<ApiResponseWrapper<SavedVPA?>>>

    suspend fun fetchSavedCards(): Flow<RestClientResult<ApiResponseWrapper<List<SavedCard>>>>

    suspend fun addNewCard(cardDetail: CardDetail): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchCardBinInfo(cardBin: String): Flow<RestClientResult<ApiResponseWrapper<CardBinInfo>>>

    suspend fun deleteSavedCard(deleteCard: DeleteCard): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchDailySavingRedirectionDetails(): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentCancellationV2RedirectionDetails>>>

}
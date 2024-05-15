package com.jar.app.feature_settings.data.network

import com.jar.app.feature_settings.domain.model.*
import com.jar.app.feature_settings.domain.model.VerifyUpiResponse
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_settings.util.SettingsConstants.Endpoints
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.app.feature_user_api.domain.model.SavedVPA
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class SettingsDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchSupportedLanguages() =
        getResult<ApiResponseWrapper<LanguageList>> {
            client.get {
                url(Endpoints.FETCH_SUPPORTED_LANGUAGES)
            }
        }

    suspend fun areSavingPaused(pauseType: SavingsType) =
        getResult<ApiResponseWrapper<PauseSavingResponse>> {
            client.get {
                url(Endpoints.FETCH_ARE_SAVINGS_PAUSED)
                parameter("pauseType", pauseType.name)
                parameter("includeView", false)
            }
        }

    suspend fun updateSavingPauseDuration(
        pause: Boolean,
        pauseDuration: String?,
        savingType: SavingsType
    ) = getResult<ApiResponseWrapper<PauseSavingResponse>> {
        client.get {
            url(Endpoints.UPDATE_USER_SAVING_DURATION)
            parameter("pause", pause)
            parameter("pauseDuration", savingType)
            if (pauseDuration != null)
                parameter("pauseType", pauseDuration)
        }
    }

    suspend fun fetchVpaChips() =
        getResult<ApiResponseWrapper<VpaChips>> {
            client.get {
                url(Endpoints.FETCH_VPA_CHIPS)
            }
        }

    suspend fun verifyUpiAddress(upiAddress: String) =
        getResult<ApiResponseWrapper<VerifyUpiResponse>> {
            client.post {
                url(Endpoints.VERIFY_VPA)
                parameter("vpa", upiAddress)
            }
        }

    suspend fun addNewUpiId(upiId: String) =
        getResult<ApiResponseWrapper<SavedVPA?>> {
            client.post {
                url(Endpoints.ADD_NEW_VPA)
                setBody(JsonObject(mapOf(Pair("vpa", JsonPrimitive(upiId)))))
            }
        }

    suspend fun fetchCardBinInfo(cardBin: String) =
        getResult<ApiResponseWrapper<CardBinInfo>> {
            client.get {
                url(Endpoints.FETCH_CARD_BIN_INFO)
                parameter("cardBin", cardBin)
            }
        }

    suspend fun fetchUserSavedCards() =
        getResult<ApiResponseWrapper<List<SavedCard>>> {
            client.get {
                url(Endpoints.FETCH_USER_SAVED_CARDS)
            }
        }

    suspend fun addNewCard(cardDetail: CardDetail) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.ADD_NEW_CARD)
                setBody(cardDetail)
            }
        }

    suspend fun deleteSavedCard(deleteCard: DeleteCard) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.DELETE_SAVED_CARD)
                setBody(deleteCard)
            }
        }

    suspend fun dailyInvestmentCancellationV2RedirectionDetails() =
        getResult<ApiResponseWrapper<DailyInvestmentCancellationV2RedirectionDetails>> {
            client.get {
                url(Endpoints.DAILY_SAVING_REDIRECTION)
                parameter("setting", "DAILY_SAVINGS")
            }

        }
}
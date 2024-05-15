package com.jar.app.feature_spin.shared.data.network

import com.jar.app.core_base.domain.model.JackPotResponseV2
import com.jar.app.feature_spin.shared.domain.model.FlatOutcome
import com.jar.app.feature_spin.shared.domain.model.GameModelRequest
import com.jar.app.feature_spin.shared.domain.model.GameResult
import com.jar.app.feature_spin.shared.domain.model.IntroPageModel
import com.jar.app.feature_spin.shared.domain.model.SpinToWinResponse
import com.jar.app.feature_spin.shared.domain.model.SpinsMetaData
import com.jar.app.feature_spin.shared.domain.model.UseWinningPopupCta
import com.jar.app.feature_spin.shared.util.Constants.Endpoints
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import com.jar.app.feature_spin.shared.util.Constants
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonObject

class SpinDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchSpinsMetaData(includeView: Boolean) =
        getResult<ApiResponseWrapper<SpinsMetaData>> {
            client.get {
                url(Endpoints.FETCH_SPIN_META_DATA)
                parameter("includeView", includeView)
            }
        }

    suspend fun fetchSpinIntro() =
        getResult<ApiResponseWrapper<IntroPageModel>> {
            client.get {
                url(Endpoints.FETCH_INTRO_PAGE)
            }
        }

    suspend fun fetchSpinData(flowTypeContext: SpinsContextFlowType) =
        getResult<ApiResponseWrapper<SpinToWinResponse>> {
            client.get {
                when (flowTypeContext) {
                    SpinsContextFlowType.SPINS -> url(Endpoints.FETCH_SPIN_GANE)
                    SpinsContextFlowType.QUESTS -> url(Constants.QuestsEndpointsV2.FETCH_SPIN_GANE)
                }
            }
        }

    suspend fun fetchSpinResult(gameId: GameModelRequest?, flowTypeContext: SpinsContextFlowType) =
        getResult<ApiResponseWrapper<GameResult>> {
            when (flowTypeContext) {
                SpinsContextFlowType.SPINS -> {
                    client.post {
                        url(Endpoints.FETCH_GAME_RESULT)
                        setBody(gameId)
                    }
                }
                SpinsContextFlowType.QUESTS -> {
                    client.get {
                        url(Constants.QuestsEndpointsV2.FETCH_GAME_RESULT)
                    }
                }
            }
        }

    suspend fun fetchFlatOutComeResult(
        jsonObject: JsonObject,
        flowTypeContext: SpinsContextFlowType
    ) =
        getResult<ApiResponseWrapper<FlatOutcome?>> {
            client.post {
                when (flowTypeContext) {
                    SpinsContextFlowType.SPINS -> url(Endpoints.FETCH_FLAT_OUTCOME)
                    SpinsContextFlowType.QUESTS -> url(Constants.QuestsEndpointsV2.FETCH_FLAT_OUTCOME)
                }
                setBody(jsonObject)
            }
        }

    suspend fun fetchJackpotOutComeResult(
        jsonObject: JsonObject,
        flowTypeContext: SpinsContextFlowType
    ) =
        getResult<ApiResponseWrapper<JackPotResponseV2>> {
            client.post {
                when (flowTypeContext) {
                    SpinsContextFlowType.SPINS -> url(Endpoints.FETCH_JACKPOT_OUTCOME)
                    SpinsContextFlowType.QUESTS -> url(Constants.QuestsEndpointsV2.FETCH_JACKPOT_OUTCOME)
                }
                setBody(jsonObject)
            }
        }

    suspend fun fetchUseWinning() =
        getResult<ApiResponseWrapper<UseWinningPopupCta>> {
            client.get {
                url(Endpoints.FETCH_WINNINGS_POPUP_DATA)
            }
        }

    suspend fun resetSpin(spinId: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.put {
                url(Endpoints.RESET_SPIN)
                parameter("spinId", spinId)
            }
        }

}
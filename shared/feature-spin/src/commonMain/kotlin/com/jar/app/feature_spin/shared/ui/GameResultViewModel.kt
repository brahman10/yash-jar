package com.jar.app.feature_spin.shared.ui

import com.jar.app.core_base.domain.model.JackPotResponseV2
import com.jar.app.core_base.util.toJsonElement
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import com.jar.app.feature_spin.shared.domain.model.FlatOutcome
import com.jar.app.feature_spin.shared.domain.model.GameModelRequest
import com.jar.app.feature_spin.shared.domain.model.GameResult
import com.jar.app.feature_spin.shared.domain.model.IntroPageModel
import com.jar.app.feature_spin.shared.domain.model.SpinToWinResponse
import com.jar.app.feature_spin.shared.domain.model.UseWinningPopupCta
import com.jar.app.feature_spin.shared.domain.usecase.FetchJackpotOutComeDataUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinDataUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinIntroUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinsResultDataUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchUseWinningUseCase
import com.jar.app.feature_spin.shared.domain.usecase.SpinFlatOutcomeUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class GameResultViewModel constructor(
    private val fetchSpinDataUseCase: FetchSpinDataUseCase,
    private val fetchSpinsResultDataUseCase: FetchSpinsResultDataUseCase,
    private val fetchSpinIntroUseCase: FetchSpinIntroUseCase,
    private val fetchFlatOutComeUseCase: SpinFlatOutcomeUseCase,
    private val fetchJackpotOutComeUseCase: FetchJackpotOutComeDataUseCase,
    private val fetchUseWinningUseCase: FetchUseWinningUseCase,
    private val prefs: PrefsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private var context: SpinsContextFlowType = SpinsContextFlowType.SPINS

    var spinToWinResponseData: SpinToWinResponse? = null
    var gameResultData: GameResult? = null
    var shouldShowJackpot = true
    var shouldShowWinningPopUp = true
    var isSpinResponseAlreadyFetched = false

    private val nullGameId = "123"

    val winningCloseLiveData: MutableStateFlow<Boolean?> = MutableStateFlow(true)

    val spinRotationCompleteLiveData: MutableStateFlow<Boolean?> = MutableStateFlow(false)

    private val _gameResult =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GameResult>>?>(null)
    val gameResult: CFlow<RestClientResult<ApiResponseWrapper<GameResult>>?>
        get() = _gameResult.toCommonFlow()

    private val _flatWinningResponse =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<FlatOutcome?>>?>(null)
    val flatWinningResponse: CStateFlow<RestClientResult<ApiResponseWrapper<FlatOutcome?>>?>
        get() = _flatWinningResponse.toCommonStateFlow()

    private val _jackpotResponse =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<JackPotResponseV2>>?>(null)
    val jackpotResponse: CStateFlow<RestClientResult<ApiResponseWrapper<JackPotResponseV2>>?>
        get() = _jackpotResponse.toCommonStateFlow()

    private val _spinToWinResponse =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<SpinToWinResponse>>>(RestClientResult.none())
    val spinToWinResponse: CStateFlow<RestClientResult<ApiResponseWrapper<SpinToWinResponse>>>
        get() = _spinToWinResponse.toCommonStateFlow()

    private val _useWinningData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UseWinningPopupCta>>>()
    val useWinningData: CFlow<RestClientResult<ApiResponseWrapper<UseWinningPopupCta>>>
        get() = _useWinningData.toCommonFlow()

    private val _showIntroPage =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<IntroPageModel>>>()
    val showIntroPage: CFlow<RestClientResult<ApiResponseWrapper<IntroPageModel>>>
        get() = _showIntroPage.toCommonFlow()

    private val _openUseWinnings = MutableSharedFlow<String>()
    val openUseWinnings: CFlow<String>
        get() = _openUseWinnings.toCommonFlow()

    // this is for spin response
    private val _spinResponseAndWinningCloseLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<GameResult>>>()
    val spinResponseAndWinningCloseLiveData: CFlow<RestClientResult<ApiResponseWrapper<GameResult>>>
        get() = _spinResponseAndWinningCloseLiveData.toCommonFlow()

    private val _spinCompleteAndExecuteFlatResponseLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<FlatOutcome?>>>()
    val spinCompleteAndExecuteFlatResponseLiveData: CFlow<RestClientResult<ApiResponseWrapper<FlatOutcome?>>>
        get() = _spinCompleteAndExecuteFlatResponseLiveData.toCommonFlow()

    private val _spinCompleteAndExecuteJackpotResponseLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<JackPotResponseV2>>>()

    val spinCompleteAndExecuteJackpotResponseLiveData: CFlow<RestClientResult<ApiResponseWrapper<JackPotResponseV2>>>
        get() = _spinCompleteAndExecuteJackpotResponseLiveData.toCommonFlow()

    init {
        viewModelScope.launch {
            _gameResult.combine(winningCloseLiveData) { gameResult, winningClosed ->
                if (winningClosed == true && gameResult != null) {
                    return@combine gameResult
                }
                return@combine RestClientResult.none()
            }.collectLatest {
                _spinResponseAndWinningCloseLiveData.emit(it)
            }
        }

        viewModelScope.launch {
            _jackpotResponse.combine(spinRotationCompleteLiveData) { jackpot, spinRotationCompleted ->
                if (spinRotationCompleted == true && jackpot != null) {
                    return@combine jackpot
                }
                return@combine RestClientResult.none()
            }.collectLatest {
                _spinCompleteAndExecuteJackpotResponseLiveData.emit(it)
            }
        }

        viewModelScope.launch {
            _flatWinningResponse.combine(spinRotationCompleteLiveData) { flatWinning, spinRotationCompleted ->
                if (spinRotationCompleted == true && flatWinning != null) {
                    return@combine flatWinning
                }
                return@combine RestClientResult.none()
            }.collectLatest {
                _spinCompleteAndExecuteFlatResponseLiveData.emit(it)
            }
        }
    }

    fun fetchSpinToWinResponse() {
        viewModelScope.launch {
            fetchSpinDataUseCase.fetchSpinsData(context).collect {
                _spinToWinResponse.emit(it)
            }
        }
    }

    fun fetchGameResult(gameId: String?) {
        viewModelScope.launch {
            val gameIdIfNull = gameId ?: nullGameId
            val jsonObject = GameModelRequest(gameIdIfNull)
            fetchSpinsResultDataUseCase.fetchSpinsResultData(
                jsonObject,
                context
            ).collect {
                _gameResult.emit(it)
            }
        }
    }

    fun onSetup() {
        fetchSpinToWinResponse()
        getSpinIntro()
    }

    fun getFlatWinningDetail(spinId: String?) {
        viewModelScope.launch {
            val jsonObject = JsonObject(
                mutableMapOf<String, JsonElement>().apply {
                    if (spinId.isNullOrBlank().not())
                        put("spinId", spinId!!.toJsonElement())
                }
            )
            fetchFlatOutComeUseCase.fetchSpinFlatOutCome(jsonObject, context).collect {
                _flatWinningResponse.emit(it)
            }
        }
    }

    fun getJackpotDetail(spinId: String?) {
        viewModelScope.launch {
            val jsonObject = JsonObject(
                mutableMapOf<String, JsonElement>().apply {
                    if (spinId.isNullOrBlank().not())
                        put("spinId", spinId!!.toJsonElement())
                }
            )
            fetchJackpotOutComeUseCase.fetchJackpotOutComeData(jsonObject, context).collect {
                _jackpotResponse.emit(it)
            }
        }
    }

    fun getUseThisWinningData(deeplink: String) {
        viewModelScope.launch {
            val isHowToUswWinningsShown = prefs.getIsShownHowToUseWinnings()
            if (
                spinToWinResponseData?.showTodayWinnings == true
                && spinToWinResponseData?.useWinningsCta?.text == "USE WINNINGS"
                && isHowToUswWinningsShown.not()
            ) {
                prefs.setIsShownHowToUseWinnings(true)
                fetchUseWinningUseCase.fetchUseWinning().collect {
                    _useWinningData.emit(it)
                }
            } else {
                _openUseWinnings.emit(deeplink)
            }
        }
    }

      fun getSpinIntro() {
        viewModelScope.launch {
            fetchSpinIntroUseCase.fetchSpinIntro().collect {
                _showIntroPage.emit(it)
            }
        }
    }

    suspend fun showAlertNudge(): Boolean {
        return viewModelScope.async {
            val isAlertNudgeShown = prefs.getIsAlertNudgeShow()
            if (isAlertNudgeShown.not()) {
                prefs.setIsAlertShown(true)
                return@async true
            }
            return@async false
        }.await()
    }

    fun setFlowContext(context: SpinsContextFlowType) {
        this.context = context
    }
}

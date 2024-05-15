package com.jar.app.feature_settings.ui

import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_preferences.api.RetainedPrefsApi
import com.jar.app.feature_savings_common.shared.domain.model.GoalBasedSavingDetails
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchGoalBasedSavingSettingUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_settings.domain.model.DailyInvestmentCancellationV2RedirectionDetails
import com.jar.app.feature_settings.domain.model.Settings
import com.jar.app.feature_settings.domain.use_case.DailyInvestmentCancellationV2RedirectionDetailsUseCase
import com.jar.app.feature_settings.util.SettingsV2ListGenerator
import com.jar.app.feature_user_api.domain.mappers.toUserGoldSipDetails
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsV2ViewModel constructor(
    private val fetchGoldSipDetailsUseCase: FetchGoldSipDetailsUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val fetchGoalBasedSavingSettingUseCase: FetchGoalBasedSavingSettingUseCase,
    private val dailyInvestmentCancellationV2RedirectionDetailsUseCase: DailyInvestmentCancellationV2RedirectionDetailsUseCase,
    prefsApi: PrefsApi,
    retainedPrefsApi: RetainedPrefsApi,
    deviceUtils: DeviceUtils,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val settingsV2ListGenerator = SettingsV2ListGenerator(
        prefsApi,
        retainedPrefsApi,
        deviceUtils
    )

    private var job: Job? = null

    private val _dailySavingDetailsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>(
            RestClientResult.none()
        )
    val dailySavingDetailsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _dailySavingDetailsLiveData.toCommonStateFlow()

    private val _roundOffDetailsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>(
            RestClientResult.none()
        )
    val roundOffDetailsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _roundOffDetailsLiveData.toCommonStateFlow()

    private val _goldSipDetailsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails?>>>(
            RestClientResult.none()
        )
    val goldSipDetailsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails?>>>
        get() = _goldSipDetailsLiveData.toCommonStateFlow()

    private val _dailyInvestmentCancellationV2DetailsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentCancellationV2RedirectionDetails>>>(
            RestClientResult.none()
        )
    val dailyInvestmentCancellationV2DetailsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentCancellationV2RedirectionDetails>>>
        get() = _dailyInvestmentCancellationV2DetailsLiveData.toCommonStateFlow()

    private val _settingListLiveData = MutableStateFlow<List<Settings>>(emptyList())
    val settingListLiveData: CStateFlow<List<Settings>>
        get() = _settingListLiveData.toCommonStateFlow()

    private val _goalBasedSavingSetting =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoalBasedSavingDetails>>>(
            RestClientResult.none()
        )
    val goalBasedSavingSetting: CStateFlow<RestClientResult<ApiResponseWrapper<GoalBasedSavingDetails>>>
        get() = _goalBasedSavingSetting.toCommonStateFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow: CFlow<String>
        get() = _errorFlow.toCommonFlow()

    var userDailySavingDetails: UserSavingsDetails? = null
    var userGoalBasedSavingDetails: GoalBasedSavingDetails? = null

    fun getData() {
        fetchDailySavingDetails()
        fetchRoundOffDetails()
        fetchGoldSipDetails()
        fetchDailyInvestmentCancellationV2RedirectionDetails()
        fetchGbSSetting()
    }

    fun observeFlows(
        convertToString: (
            stringRes: StringResource,
            args: Array<Any>
        ) -> String?
    ) {
        viewModelScope.launch {
            dailySavingDetailsLiveData.collect(
                onSuccess = {
                    userDailySavingDetails = it
                    mergeApiResponse(
                        convertToString = convertToString,
                        userDailySavingDetails = it
                    )
                },
                onError = { errorMessage, _ ->
                    _errorFlow.emit(errorMessage)
                }
            )
        }

        viewModelScope.launch {
            goldSipDetailsLiveData.collect(
                onSuccess = {
                    mergeApiResponse(
                        convertToString = convertToString,
                        userGoldSipDetails = it
                    )
                },
                onError = { errorMessage, _ ->
                    _errorFlow.emit(errorMessage)
                }
            )
        }

        viewModelScope.launch {
            roundOffDetailsLiveData.collect(
                onSuccess = {
                    mergeApiResponse(
                        convertToString = convertToString,
                        userRoundOffDetails = it
                    )
                },
                onError = { errorMessage, _ ->
                    _errorFlow.emit(errorMessage)
                }
            )
        }

        viewModelScope.launch {
            goalBasedSavingSetting.collect(
                onSuccess = {
                    userGoalBasedSavingDetails = it
                    mergeApiResponse(
                        convertToString = convertToString,
                        goalBasedSavingDetails = it
                    )
                },
                onError = { errorMessage, _ ->
                    _errorFlow.emit(errorMessage)
                }
            )
        }

        viewModelScope.launch {
            dailyInvestmentCancellationV2DetailsLiveData.collect(
                onSuccess = {
                    mergeApiResponse(
                        convertToString = convertToString,
                        dailyInvestmentCancellationV2RedirectionDetails = it
                    )
                },
                onError = { errorMessage, _ ->
                    _errorFlow.emit(errorMessage)
                }
            )
        }

    }

    fun fetchGbSSetting() {
        viewModelScope.launch {
            fetchGoalBasedSavingSettingUseCase.fetchGoalBasedSavingSettingScreenData().collect {
                _goalBasedSavingSetting.emit(it)
            }
        }
    }

    fun fetchDailySavingDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.DAILY_SAVINGS).collect {
                _dailySavingDetailsLiveData.emit(it)
            }
        }
    }

    fun fetchGoldSipDetails() {
        viewModelScope.launch {
            fetchGoldSipDetailsUseCase.fetchGoldSipDetails()
                .mapToDTO {
                    it?.toUserGoldSipDetails()
                }
                .collectLatest {
                    _goldSipDetailsLiveData.emit(it)
                }
        }
    }

    fun fetchRoundOffDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.ROUND_OFFS).collect {
                _roundOffDetailsLiveData.emit(it)
            }
        }
    }

    fun fetchDailyInvestmentCancellationV2RedirectionDetails() {
        viewModelScope.launch {
            dailyInvestmentCancellationV2RedirectionDetailsUseCase.fetchDailySavingRedirectionDetails()
                .collect {
                    _dailyInvestmentCancellationV2DetailsLiveData.emit(it)
                }
        }
    }

    private fun mergeApiResponse(
        convertToString: (
            stringRes: StringResource,
            args: Array<Any>
        ) -> String?,
        userDailySavingDetails: UserSavingsDetails? = dailySavingDetailsLiveData.value.data?.data,
        userGoldSipDetails: UserGoldSipDetails? = goldSipDetailsLiveData.value.data?.data,
        userRoundOffDetails: UserSavingsDetails? = roundOffDetailsLiveData.value.data?.data,
        goalBasedSavingDetails: GoalBasedSavingDetails? = goalBasedSavingSetting.value.data?.data,
        dailyInvestmentCancellationV2RedirectionDetails: DailyInvestmentCancellationV2RedirectionDetails? = dailyInvestmentCancellationV2DetailsLiveData.value.data?.data
    ) {
        job?.cancel()
        job = viewModelScope.launch {
            val settings = settingsV2ListGenerator.getSettingList(
                convertToString = convertToString,
                userDailySavingDetails = userDailySavingDetails,
                userRoundOffDetails = userRoundOffDetails,
                userGoldSipDetails = userGoldSipDetails,
                goalBasedSavingDetails = goalBasedSavingDetails,
                dailyInvestmentCancellationV2RedirectionDetails = dailyInvestmentCancellationV2RedirectionDetails
            )
            _settingListLiveData.emit(settings)
        }
    }
}
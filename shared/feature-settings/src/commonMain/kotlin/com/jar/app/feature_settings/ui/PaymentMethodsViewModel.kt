package com.jar.app.feature_settings.ui

import com.jar.app.feature_settings.domain.model.PaymentMethod
import com.jar.app.feature_settings.domain.model.SavedCard
import com.jar.app.feature_settings.domain.model.SavedCardPaymentMethod
import com.jar.app.feature_settings.domain.model.SavedUpiIdsPaymentMethod
import com.jar.app.feature_settings.domain.use_case.FetchUserSavedCardsUseCase
import com.jar.app.feature_settings.util.SettingsConstants
import com.jar.app.feature_user_api.domain.model.SavedVPA
import com.jar.app.feature_user_api.domain.model.SavedVpaResponse
import com.jar.app.feature_user_api.domain.use_case.FetchUserVpaUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PaymentMethodsViewModel constructor(
    private val fetchUserVpaUseCase: FetchUserVpaUseCase,
    private val fetchUserSavedCardsUseCase: FetchUserSavedCardsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _userSavedCardsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<List<SavedCard>>>>(RestClientResult.none())
    val userSavedCardsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<List<SavedCard>>>>
        get() = _userSavedCardsLiveData.toCommonStateFlow()

    private val _userVPAsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<SavedVpaResponse>>>(
            RestClientResult.none()
        )
    val userVPAsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<SavedVpaResponse>>>
        get() = _userVPAsLiveData.toCommonStateFlow()

    private val _errorMessageFlow = MutableSharedFlow<String>()
    val errorMessageFlow: CFlow<String>
        get() = _errorMessageFlow.toCommonFlow()

    private val _paymentMethodLiveData =
        MutableStateFlow<List<PaymentMethod>>(emptyList())
    val paymentMethodLiveData: CStateFlow<List<PaymentMethod>>
        get() = _paymentMethodLiveData.toCommonStateFlow()

    private var job: Job? = null

    fun getData() {
        fetchUserSavedVPAs()
        fetchUserSavedCards()
        observeFlows()
    }

    private fun observeFlows() {
        viewModelScope.launch {
            userVPAsLiveData.collect(
                onSuccess = {
                    mergeApiResponse(savedVpaList = it.payoutSavedVpas)
                },
                onError = { errorMessage, _ ->
                    _errorMessageFlow.emit(errorMessage)
                }
            )
        }

        viewModelScope.launch {
            userSavedCardsLiveData.collect(
                onSuccess = {
                    mergeApiResponse(saveCardList = it)
                },
                onError = { errorMessage, _ ->
                    _errorMessageFlow.emit(errorMessage)
                }
            )
        }
    }

    fun fetchUserSavedVPAs() {
        viewModelScope.launch {
            fetchUserVpaUseCase.fetchUserSavedVPAs().collect {
                _userVPAsLiveData.emit(it)
            }
        }
    }

    fun fetchUserSavedCards() {
        viewModelScope.launch {
            fetchUserSavedCardsUseCase.fetchSavedCards().collect {
                _userSavedCardsLiveData.emit(it)
            }
        }
    }

    private fun mergeApiResponse(
        saveCardList: List<SavedCard>? = userSavedCardsLiveData.value.data?.data,
        savedVpaList: List<SavedVPA>? = userVPAsLiveData.value.data?.data?.payoutSavedVpas
    ) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.Default) {
            delay(100)
            val list = ArrayList<PaymentMethod>()
            savedVpaList?.let {
                list.add(SavedUpiIdsPaymentMethod(it, SettingsConstants.PaymentMethodsPosition.UPI))
            }
            saveCardList?.let {
                list.add(SavedCardPaymentMethod(it, SettingsConstants.PaymentMethodsPosition.CARDS))
            }
            _paymentMethodLiveData.emit(list)
        }

    }
}
package com.jar.app.feature_one_time_payments.shared.ui

import com.jar.app.core_base.util.orFalse
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.app.feature_one_time_payments.shared.domain.model.InitiateUpiCollectRequest
import com.jar.app.feature_one_time_payments.shared.domain.model.InitiateUpiCollectResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.SavedUpiIdsResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import com.jar.app.feature_one_time_payments.shared.domain.model.VerifyUpiAddressResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.SavedCard
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.PaymentMethod
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.PaymentMethodCard
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.PaymentMethodNB
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.OneTimePaymentMethodType
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.PaymentMethodUpiCollect
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.PaymentMethodUpiIntent
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.AddCardPaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.OrderSummarySection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.PaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.RecentlyUsedPaymentMethodSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.SavedCardPaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.SavedUpiIdSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.SecurePaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.UpiCollectPaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_section.UpiIntentAppsPaymentSection
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchEnabledPaymentMethodUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchRecentlyUsedPaymentMethodsUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchSavedUpiIdUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.InitiateUpiCollectUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.VerifyUpiAddressUseCase
import com.jar.app.feature_one_time_payments.shared.util.OneTimePaymentEventKey
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray

class PaymentOptionPageFragmentViewModel constructor(
    private val fetchSavedUpiIdUseCase: FetchSavedUpiIdUseCase,
    private val verifyUpiAddressUseCase: VerifyUpiAddressUseCase,
    private val initiateUpiCollectUseCase: InitiateUpiCollectUseCase,
    private val fetchEnabledPaymentMethodUseCase: FetchEnabledPaymentMethodUseCase,
    private val fetchRecentlyUsedPaymentMethodsUseCase: FetchRecentlyUsedPaymentMethodsUseCase,
    private val remoteConfigApi: RemoteConfigApi,
    private val serializer: Serializer,
    private val analyticsApi: AnalyticsApi,
    private val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _verifyUpiFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<VerifyUpiAddressResponse?>>>()
    val verifyUpiFlow: CFlow<RestClientResult<ApiResponseWrapper<VerifyUpiAddressResponse?>>>
        get() = _verifyUpiFlow.toCommonFlow()

    private val _initiateUpiCollectFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<InitiateUpiCollectResponse>>>()
    val initiateUpiCollectFlow: CFlow<RestClientResult<ApiResponseWrapper<InitiateUpiCollectResponse>>>
        get() = _initiateUpiCollectFlow.toCommonFlow()

    private val _recentlyUsedPaymentMethodFlow =
        MutableStateFlow<RestClientResult<List<PaymentMethod>>>(RestClientResult.none())
    val recentlyUsedPaymentMethodFlow: CStateFlow<RestClientResult<List<PaymentMethod>>>
        get() = _recentlyUsedPaymentMethodFlow.toCommonStateFlow()

    private val _savedUpiAddressFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<SavedUpiIdsResponse>>>(RestClientResult.none())
    val savedUpiAddressFlow: CStateFlow<RestClientResult<ApiResponseWrapper<SavedUpiIdsResponse>>>
        get() = _savedUpiAddressFlow.toCommonStateFlow()

    private val _enabledPaymentMethodsFlow =
        MutableStateFlow<RestClientResult<List<OneTimePaymentMethodType>>>(RestClientResult.none())
    val enabledPaymentMethodsFlow: CStateFlow<RestClientResult<List<OneTimePaymentMethodType>>>
        get() = _enabledPaymentMethodsFlow.toCommonStateFlow()

    private val _listFlow = MutableSharedFlow<List<PaymentSection>>()
    val listFlow: CFlow<List<PaymentSection>>
        get() = _listFlow.toCommonFlow()

    private var job: Job? = null

    private var upiApps: List<UpiApp>? = null

    var amount: Float? = null

    var cards: List<SavedCard>? = null

    private val _exitSurveyResponse = MutableSharedFlow<Boolean?>()
    val exitSurveyResponse: SharedFlow<Boolean?>
        get() = _exitSurveyResponse

    fun verifyUpiAddress(upiAddress: String) {
        viewModelScope.launch {
            verifyUpiAddressUseCase.verifyUpiAddress(upiAddress, isEligibleForMandate = null)
                .collect {
                    _verifyUpiFlow.emit(it)
                }
        }
    }

    fun initiateUpiCollectRequest(initiateUpiCollectRequest: InitiateUpiCollectRequest) {
        viewModelScope.launch {
            initiateUpiCollectUseCase.initiateUpiCollect(initiateUpiCollectRequest).collect {
                _initiateUpiCollectFlow.emit(it)
            }
        }
    }

    fun fetchEnabledPaymentMethod(transactionType: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchEnabledPaymentMethodUseCase.fetchEnabledPaymentMethods(transactionType).collect(
                onLoading = {
                    _enabledPaymentMethodsFlow.emit(RestClientResult.loading())
                },
                onSuccess = {
                    val list = mutableListOf<OneTimePaymentMethodType>()
                    it.paymentMethods.forEach { type ->
                        val paymentMethod = OneTimePaymentMethodType.values().find { it.name == type }
                        if (paymentMethod != null) {
                            list.add(paymentMethod)
                        }
                    }
                    _enabledPaymentMethodsFlow.emit(RestClientResult.success(list))
                },
                onError = { errorMessage, errorCode ->
                    _enabledPaymentMethodsFlow.emit(RestClientResult.error(errorMessage))
                }
            )
        }
    }

    fun fetchRecentlyUsedPaymentMethods(isPackageInstalled: (packageName: String) -> Boolean, flowContext: String? = null) {
        viewModelScope.launch {
            fetchRecentlyUsedPaymentMethodsUseCase.fetchRecentlyUsedPaymentMethods(
                isPackageInstalled = isPackageInstalled,
                flowContext = flowContext
            ).collect {
                _recentlyUsedPaymentMethodFlow.emit(it)
            }
        }
    }

    fun fetchSavedUpiIds() {
        viewModelScope.launch {
            fetchSavedUpiIdUseCase.fetchSavedUpiIds().collect {
                _savedUpiAddressFlow.emit(it)
            }
        }
    }

    fun setUpiApps(jsonArray: JsonArray) {
        viewModelScope.launch(Dispatchers.Default) {
            val list =
                serializer.decodeFromString<List<UpiApp>>(jsonArray.toString())
            this@PaymentOptionPageFragmentViewModel.upiApps = list
            analyticsApi.postEvent(
                OneTimePaymentEventKey.Shown_AvailableUpiApps, mapOf(
                    OneTimePaymentEventKey.PaymentMethod to OneTimePaymentEventKey.OneTimePayment,
                    OneTimePaymentEventKey.UpiAppsShown to if (list.isNotEmpty()) list.joinToString(
                        separator = ","
                    ) else "NA",
                )
            )
            mergePaymentData(upiAppList = list)
        }
    }

    fun setUpiApps(map: Map<String, String>) {
        viewModelScope.launch(Dispatchers.Default) {
            val list = map.toList().map {
                UpiApp(
                    packageName = it.first,
                    appName = it.second,
                    isSelected = false
                )
            }
            this@PaymentOptionPageFragmentViewModel.upiApps = list
            mergePaymentData(upiAppList = list)
        }
    }

    fun setSelectedApp(upiApp: UpiApp) {
        viewModelScope.launch(Dispatchers.Default) {
            val list = this@PaymentOptionPageFragmentViewModel.upiApps?.map {
                it.copy(
                    isSelected = it.packageName == upiApp.packageName
                )
            }
            this@PaymentOptionPageFragmentViewModel.upiApps = list
            mergePaymentData(upiAppList = list)
        }
    }

    fun getExitSurveyData() {
        viewModelScope.launch {
            fetchExitSurveyQuestionsUseCase.fetchExitSurveyQuestions(ExitSurveyRequestEnum.MANUAL_BUY_TRANSACTION_SCREEN.toString()).collect(
                onLoading = {},
                onSuccessWithNullData = {
                    _exitSurveyResponse.emit(false)
                },
                onSuccess = {
                    _exitSurveyResponse.emit(true)
                },
                onError = {_, _ ->
                }
            )
        }
    }

    fun mergePaymentData(
        upiAppList: List<UpiApp>? = upiApps,
        recentlyUsedPaymentMethods: List<PaymentMethod>? = recentlyUsedPaymentMethodFlow.value.data.orEmpty(),
        savedUpiIdsResponse: SavedUpiIdsResponse? = savedUpiAddressFlow.value.data?.data,
        amount: Float? = this.amount,
        upiCollectErrorMessage: String? = null,
        cards: List<SavedCard>? = this.cards
    ) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.Default) {

            val enabledPaymentMethods = enabledPaymentMethodsFlow.value.data

            delay(500)
            val sections = mutableListOf<PaymentSection>()

            //Order Summary Section
            amount?.let {
                sections.add(OrderSummarySection(amount))
            }

            //Recently Used Section
            if (!recentlyUsedPaymentMethods.isNullOrEmpty()) {
                val finalList = recentlyUsedPaymentMethods.filter {
                    when (it) {
                        is PaymentMethodCard -> {
                            if (enabledPaymentMethods?.contains(OneTimePaymentMethodType.CARD).orFalse()) {
                                val savedCard =
                                    cards?.find { card -> card.cardFingerprint == it.cardFingerprint }
                                if (savedCard != null) {
                                    it.savedCard = savedCard
                                    true
                                } else false
                            } else false
                        }

                        is PaymentMethodUpiIntent -> {
                            enabledPaymentMethods?.contains(OneTimePaymentMethodType.UPI_INTENT).orFalse()
                        }

                        is PaymentMethodUpiCollect -> {
                            enabledPaymentMethods?.contains(OneTimePaymentMethodType.UPI_COLLECT).orFalse()
                        }

                        is PaymentMethodNB -> {
                            false
                        }
                    }
                }
                sections.add(RecentlyUsedPaymentMethodSection(finalList))
            }

            //Saved Upi Ids Section
            if (savedUpiIdsResponse != null
                && !savedUpiIdsResponse.vpaAddresses.isNullOrEmpty()
                && enabledPaymentMethods?.contains(OneTimePaymentMethodType.UPI_COLLECT).orFalse()
            ) {
                val vpaAddresses = savedUpiIdsResponse.vpaAddresses?.filterNotNull().orEmpty()
                sections.add(SavedUpiIdSection(vpaAddresses))
            }

            //Upi Intent Apps Section
            if (!upiAppList.isNullOrEmpty()
//                && enabledPaymentMethods?.contains(PaymentMethodType.UPI_INTENT).orFalse()
            ) {
                val sortedList = upiAppList.sortedWith(CustomAppListSorter())
                sections.add(UpiIntentAppsPaymentSection(sortedList))
            }

            //Upi Collect Section
            if (enabledPaymentMethods?.contains(OneTimePaymentMethodType.UPI_COLLECT).orFalse()) {
                sections.add(
                    UpiCollectPaymentSection(
                        remoteConfigApi.getUpiAppsLogoUrl(),
                        upiCollectErrorMessage
                    )
                )
            }

            //Saved Card Section
            if (!cards.isNullOrEmpty() &&
                enabledPaymentMethods?.contains(OneTimePaymentMethodType.CARD).orFalse()
            ) {
                sections.add(SavedCardPaymentSection(cards))
            }

            //Add Card Section
            if (enabledPaymentMethods?.contains(OneTimePaymentMethodType.CARD).orFalse()) {
                sections.add(AddCardPaymentSection(remoteConfigApi.getBankAppsLogoUrl()))
            }

            //Secure Payment Section
            sections.add(SecurePaymentSection())

            _listFlow.emit(sections)
        }
    }

    inner class CustomAppListSorter : Comparator<UpiApp> {
        private val orderPreference = listOf(
            "com.phonepe.app",
            "com.google.android.apps.nbu.paisa.user",
            "net.one97.paytm",
            "in.org.npci.upiapp"
        )

        override fun compare(o1: UpiApp, o2: UpiApp): Int {
            if (orderPreference.contains(o1.packageName) && orderPreference.contains(o2.packageName)) {
                // Both objects are in our ordered list. Compare them by their position in the list
                return orderPreference.indexOf(o1.packageName) - orderPreference.indexOf(o2.packageName)
            }

            if (orderPreference.contains(o1.packageName)) {
                // o1 is in the ordered list, but o2 isn't. o1 is smaller (i.e. first)
                return -1
            }

            if (orderPreference.contains(o2.packageName)) {
                // o2 is in the ordered list, but o1 isn't. o2 is smaller (i.e. first)
                return 1
            }

            return o1.toString().compareTo(o2.toString())
        }
    }
}
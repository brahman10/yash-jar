package com.jar.app.feature_buy_gold_v2.shared.ui

import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentSectionHeaderType
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldUpiApp
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2Constants
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey
import com.jar.app.feature_one_time_payments.shared.domain.model.EnabledPaymentMethodResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.*
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchEnabledPaymentMethodUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchRecentlyUsedPaymentMethodsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BuyGoldPaymentOptionsViewModel constructor(
    private val fetchRecentlyUsedPaymentMethodsUseCase: FetchRecentlyUsedPaymentMethodsUseCase,
    private val fetchEnabledPaymentMethodUseCase: FetchEnabledPaymentMethodUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private var job: Job? = null

    private val _listFlow = MutableSharedFlow<List<BuyGoldUpiApp>>()
    val listFlow: CFlow<List<BuyGoldUpiApp>>
        get() = _listFlow.toCommonFlow()

    private val _recentlyUsedPaymentMethodFlow =
        MutableStateFlow<RestClientResult<List<PaymentMethod>>>(RestClientResult.none())
    val recentlyUsedPaymentMethodFlow: CStateFlow<RestClientResult<List<PaymentMethod>>>
        get() = _recentlyUsedPaymentMethodFlow.toCommonStateFlow()

    private val _enabledPaymentMethodsFlow =
        MutableStateFlow<RestClientResult<EnabledPaymentMethodResponse>>(RestClientResult.none())
    val enabledPaymentMethodsFlow: CStateFlow<RestClientResult<EnabledPaymentMethodResponse>>
        get() = _enabledPaymentMethodsFlow.toCommonStateFlow()

    private var upiApps: List<BuyGoldUpiApp>? = null

    var maxPaymentMethodsCount = BuyGoldV2Constants.DEFAULT_MAX_PAYMENT_METHODS_COUNT

    var paymentMethodsList: MutableList<BuyGoldUpiApp> = ArrayList()
    var selectedPaymentMethod: BuyGoldUpiApp? = null

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

    fun fetchEnabledPaymentMethod(transactionType: String?) {
        viewModelScope.launch {
            fetchEnabledPaymentMethodUseCase.fetchEnabledPaymentMethods(transactionType).collect(
                onLoading = {
                    _enabledPaymentMethodsFlow.emit(RestClientResult.loading())
                },
                onSuccess = {
                    _enabledPaymentMethodsFlow.emit(RestClientResult.success(it))
                },
                onError = { errorMessage, errorCode ->
                    _enabledPaymentMethodsFlow.emit(RestClientResult.error(errorMessage))
                }
            )
        }
    }

    fun setUpiAppsList(upiAppsList: List<BuyGoldUpiApp>) {
        this.upiApps = upiAppsList
        mergePaymentData(installedUpiAppsList = upiAppsList)
    }

    fun updateSelectedPaymentMethod(buyGoldUpiApp: BuyGoldUpiApp) {
        viewModelScope.launch {
            selectedPaymentMethod = buyGoldUpiApp
            val updatedList = paymentMethodsList.map { upiApp ->
                upiApp.copy(
                    isSelected =
                    if (upiApp.payerApp == buyGoldUpiApp.payerApp)
                        upiApp.isSelected.not()
                    else
                        false
                )
            }
            _listFlow.emit(updatedList)
        }
    }

    fun mergePaymentData(
        recentlyUsedPaymentMethods: List<PaymentMethod>? = recentlyUsedPaymentMethodFlow.value.data.orEmpty(),
        installedUpiAppsList: List<BuyGoldUpiApp>? = upiApps
    ) {
        job?.cancel()
        job = viewModelScope.launch {

            val enabledPaymentMethods = mutableListOf<OneTimePaymentMethodType>()
            enabledPaymentMethodsFlow.value.data?.paymentMethods?.forEach { type ->
                val paymentMethod = OneTimePaymentMethodType.values().find { it.name == type }
                if (paymentMethod != null) {
                    enabledPaymentMethods.add(paymentMethod)
                }
            }
            val whiteListedApps = enabledPaymentMethodsFlow.value.data?.whitelistedUpiApps.orEmpty()
            
            //This list is to keep a track of all the already added upi apps in the list to avoid
            //duplicate apps in the final list
            val previousUpiIntentAppsPackageIdList: ArrayList<String> = ArrayList()

            paymentMethodsList.clear()

            delay(500)

            /**
             * Recommended Section - 1st upi intent app from recently used list
             * **/
            val recommendedAppsList: MutableList<BuyGoldUpiApp> = ArrayList()
            if (!recentlyUsedPaymentMethods.isNullOrEmpty()) {
                val paymentMethodUpiIntent =
                    recentlyUsedPaymentMethods.filterIsInstance<PaymentMethodUpiIntent>().firstOrNull()
                if (paymentMethodUpiIntent != null) {
                    recommendedAppsList.add(
                        BuyGoldUpiApp(
                            payerApp = paymentMethodUpiIntent.payerApp,
                            headerType = BuyGoldPaymentSectionHeaderType.RECOMMENDED
                        )
                    )
                }
            }

            /**
             * UPI Apps Section - Remaining apps of recently used list,
             * upi apps installed on phone, saved upi ids
             * **/
            val finalUpiAppsList: MutableList<BuyGoldUpiApp> = ArrayList()
            recommendedAppsList.forEach { upiApp ->
                previousUpiIntentAppsPackageIdList.add(upiApp.payerApp)
            }
            //After adding recommended app, we need to add the remaining recently used apps first
            if (!recentlyUsedPaymentMethods.isNullOrEmpty()) {
                recentlyUsedPaymentMethods.forEach {
                    when (it) {
                        is PaymentMethodUpiIntent -> {
                            if (whiteListedApps.contains(it.payerApp)
                                && !previousUpiIntentAppsPackageIdList.contains(it.payerApp)) {
                                finalUpiAppsList.add(
                                    BuyGoldUpiApp(
                                        payerApp = it.payerApp,
                                        headerType = BuyGoldPaymentSectionHeaderType.UPI_APPS
                                    )
                                )
                            }
                        }
                        else -> {
                            //Payment Method not supported
                        }
                    }
                }
            }

            //After adding remaining recently used apps we need to add upi apps installed on phone
            finalUpiAppsList.forEach { upiApp ->
                previousUpiIntentAppsPackageIdList.add(upiApp.payerApp)
            }
            if (!installedUpiAppsList.isNullOrEmpty()) {
                installedUpiAppsList.forEach {
                    if (whiteListedApps.contains(it.payerApp)
                        && !previousUpiIntentAppsPackageIdList.contains(it.payerApp)) {
                        finalUpiAppsList.add(it)
                    }
                }
            }

            paymentMethodsList.addAll(recommendedAppsList)
            paymentMethodsList.addAll(finalUpiAppsList)

            paymentMethodsList =
                if (paymentMethodsList.size > maxPaymentMethodsCount)
                    paymentMethodsList.dropLast(paymentMethodsList.size - maxPaymentMethodsCount).toMutableList()
                else
                    paymentMethodsList

            _listFlow.emit(paymentMethodsList)
        }
    }

    fun fireShownEvent(analyticsData: MutableMap<String,String>) {
        analyticsApi.postEvent(
            BuyGoldV2EventKey.BuyGold_AutoPayMethod_BSShown,
            analyticsData
        )
    }

    fun fireClickEvent(analyticsData: MutableMap<String,String>) {
        analyticsApi.postEvent(
            BuyGoldV2EventKey.BuyGold_AutoPayMethod_BSClick,
            analyticsData
        )
    }
}
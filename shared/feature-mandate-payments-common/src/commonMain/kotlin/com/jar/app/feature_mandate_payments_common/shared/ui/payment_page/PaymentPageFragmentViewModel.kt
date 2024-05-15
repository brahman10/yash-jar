package com.jar.app.feature_mandate_payments_common.shared.ui.payment_page

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_coupon_api.domain.model.ApplyCouponCodeResponse
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_coupon_api.domain.use_case.ApplyCouponUseCase
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_mandate_payments_common.shared.MR
import com.jar.app.feature_mandate_payments_common.shared.MandatePaymentBuildKonfig
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.mandate_help.MandateEducationResp
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method.EnabledPaymentMethodResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.BasePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.CouponCodeResponseForMandateScreenItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.DescriptionPaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.MandateEducationPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PreferredBankPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.SeparatorPaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.SpacePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.TitlePaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.UpiAppPaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.UpiCollectPaymentPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_upi.VerifyUpiAddressResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchEnabledPaymentMethodsUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandateEducationUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchPreferredBankUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.InitiateMandatePaymentUseCase
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.VerifyUpiAddressUseCase
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentGateway
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentPageFragmentViewModel constructor(
    private val verifyUpiAddressUseCase: VerifyUpiAddressUseCase,
    private val fetchPreferredBankUseCase: FetchPreferredBankUseCase,
    private val fetchMandateEducationUseCase: FetchMandateEducationUseCase,
    private val initiateMandatePaymentUseCase: InitiateMandatePaymentUseCase,
    private val fetchEnabledPaymentMethodsUseCase: FetchEnabledPaymentMethodsUseCase,
    private val applyCouponUseCase: ApplyCouponUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val fetchCouponCodeUseCase: FetchCouponCodeUseCase,
    private val fetchInstalledUpiApps: () -> List<String>,
    private val fetchAppNameFromPackageName: (packageName: String) -> String?,
    private val remoteConfigApi: RemoteConfigApi,
    private val analyticsApi: AnalyticsApi,
    private val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase,
    coroutineScope: CoroutineScope?
) {

    companion object{
        const val DAILY_SAVINGS = "DAILY_SAVINGS"
    }
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    var paymentPageHeaderDetail: PaymentPageHeaderDetail? = null
    var initiateMandatePaymentRequest: InitiateMandatePaymentRequest? = null

    private var position: Int = 0

    private val _paymentPageLiveData =
        MutableSharedFlow<List<BasePaymentPageItem>>()
    val paymentPageLiveData: CFlow<List<BasePaymentPageItem>>
        get() = _paymentPageLiveData.toCommonFlow()

    private val _mandatePaymentEducationLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<MandateEducationResp>>>()
    val mandatePaymentEducationLiveData: CFlow<RestClientResult<ApiResponseWrapper<MandateEducationResp>>>
        get() = _mandatePaymentEducationLiveData.toCommonFlow()

    private val _preferredBankLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<PreferredBankPageItem?>>>()
    val preferredBankLiveData: CFlow<RestClientResult<ApiResponseWrapper<PreferredBankPageItem?>>>
        get() = _preferredBankLiveData.toCommonFlow()

    private val _verifyUpiAddressLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<VerifyUpiAddressResponse>>>()
    val verifyUpiAddressLiveData: CFlow<RestClientResult<ApiResponseWrapper<VerifyUpiAddressResponse>>>
        get() = _verifyUpiAddressLiveData.toCommonFlow()

    private val _initiateMandatePaymentDataLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<InitiateMandatePaymentApiResponse?>>>()
    val initiateMandatePaymentDataLiveData: CFlow<RestClientResult<ApiResponseWrapper<InitiateMandatePaymentApiResponse?>>>
        get() = _initiateMandatePaymentDataLiveData.toCommonFlow()

    private val _fetchEnabledPaymentMethodsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<EnabledPaymentMethodResponse?>>>()
    val fetchEnabledPaymentMethodsFlow: CFlow<RestClientResult<ApiResponseWrapper<EnabledPaymentMethodResponse?>>>
        get() = _fetchEnabledPaymentMethodsFlow.toCommonFlow()

    private val _couponCodesFlow =
        MutableStateFlow<RestClientResult<List<CouponCode>>>(RestClientResult.none())
    val couponCodesFlow: CFlow<RestClientResult<List<CouponCode>>>
        get() = _couponCodesFlow.toCommonFlow()

    private val _currentGoldBuyPriceFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>()
    val currentGoldBuyPriceFlow: CFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>
        get() = _currentGoldBuyPriceFlow.toCommonFlow()

    private val _applyCouponCodeFlow =
        MutableSharedFlow<RestClientResult<ApplyCouponCodeResponse?>>()
    val applyCouponCodeFlow: CFlow<RestClientResult<ApplyCouponCodeResponse?>>
        get() = _applyCouponCodeFlow.toCommonFlow()

    private val _exitSurveyResponse = MutableSharedFlow<Boolean?>()
    val exitSurveyResponse: SharedFlow<Boolean?>
        get() = _exitSurveyResponse

    private var job: Job? = null
    var couponCodeList: List<CouponCode>? = null

    private var mandatePaymentEducationResp: MandateEducationResp? = null
    var preferredBankPageItem: PreferredBankPageItem? = null
    private var enabledPaymentMethodResponse: EnabledPaymentMethodResponse? = null
    private var couponCodeResponseForMandateScreenItem: CouponCodeResponseForMandateScreenItem? =
        null
    private var currentPaymentPageItemList: List<BasePaymentPageItem>? = null

    init {
        viewModelScope.launch {
            mandatePaymentEducationLiveData.collectLatest {
                mandatePaymentEducationResp = it.data?.data
            }
        }

        viewModelScope.launch {
            preferredBankLiveData.collectLatest {
                preferredBankPageItem = it.data?.data
            }
        }

        viewModelScope.launch {
            fetchEnabledPaymentMethodsFlow.collectLatest {
                enabledPaymentMethodResponse = it.data?.data
            }
        }

        viewModelScope.launch {
            paymentPageLiveData.collectLatest {
                currentPaymentPageItemList = it
            }
        }

        viewModelScope.launch {
            couponCodesFlow.collectLatest {
                couponCodeResponseForMandateScreenItem = it.data?.getOrNull(0)
                    ?.let { it1 -> CouponCodeResponseForMandateScreenItem(couponCode = it1) }
            }
        }
    }

    fun getData() {
        fetchEnabledPaymentMethods(paymentPageHeaderDetail?.featureFlow)
        paymentPageHeaderDetail?.mandateSavingsType?.let {
            fetchMandateEducation(it)
        }
        initiateMandatePaymentRequest?.subscriptionType?.let {
            if (it == DAILY_SAVINGS && paymentPageHeaderDetail?.featureFlow == MandatePaymentEventKey.FeatureFlows.SetupDailySaving)
                fetchCouponCodes(MandatePaymentCommonConstants.SAVE_DAILY)
        }
        fetchPreferredBank()
    }

    private fun fetchMandateEducation(mandateStaticContentType: MandatePaymentCommonConstants.MandateStaticContentType) {
        viewModelScope.launch {
            fetchMandateEducationUseCase.fetchMandateEducation(mandateStaticContentType).collect {
                _mandatePaymentEducationLiveData.emit(it)
            }
        }
    }

    private fun fetchEnabledPaymentMethods(flowType: String?) {
        viewModelScope.launch {
            fetchEnabledPaymentMethodsUseCase.fetchEnabledPaymentMethods(flowType).collectLatest {
                _fetchEnabledPaymentMethodsFlow.emit(it)
            }
        }
    }

    private fun fetchCouponCodes(context: String) {
        viewModelScope.launch {
            fetchCouponCodeUseCase.fetchCouponCodes(context = context).collectUnwrapped(
                onLoading = {
                    _couponCodesFlow.emit(RestClientResult.loading())
                },
                onSuccess = {
                    couponCodeList = it.data?.couponCodes
                    fetchCurrentGoldBuyPrice()
                    analyticsApi.postEvent(
                        MandatePaymentEventKey.Clicked_UPIApp_MandatePaymentScreen_Shown,
                        mapOf(
                            MandatePaymentEventKey.Coupon to couponCodeList?.isNotEmpty().orFalse(),
                        )
                    )
                    _couponCodesFlow.emit(RestClientResult.success(couponCodeList.orEmpty()))
                },
                onError = { errorMessage, _ ->
                    _couponCodesFlow.emit(RestClientResult.error(errorMessage))
                }
            )
        }
    }

    private suspend fun getHeaderSection(
        paymentPageHeaderDetail: PaymentPageHeaderDetail,
        couponDetails: CouponCodeResponseForMandateScreenItem? = null,
        mandateEducationResp: MandateEducationResp?
    ): List<BasePaymentPageItem> =
        withContext(Dispatchers.Default) {
            val headerList = mutableListOf<BasePaymentPageItem>()
            headerList.add(
                SpacePaymentPageItem(
                    16,
                    com.jar.app.core_base.shared.CoreBaseMR.colors.color_2E2942,
                    position = position++
                )
            )
            headerList.add(
                TitlePaymentPageItem(
                    titleString = paymentPageHeaderDetail.title,
                    bgColor = com.jar.app.core_base.shared.CoreBaseMR.colors.color_2E2942
                )
            )

            if (couponDetails != null)
                couponCodeList?.getOrNull(0)
                    ?.let {
                        headerList.add(
                            CouponCodeResponseForMandateScreenItem(couponCode = it)
                        )
                    }


            headerList.add(
                SpacePaymentPageItem(
                    space = 16,
                    bgColor = com.jar.app.core_base.shared.CoreBaseMR.colors.color_2E2942,
                    position = position++
                )
            )
            headerList.add(
                SeparatorPaymentPageItem(
                    bgColor = com.jar.app.core_base.shared.CoreBaseMR.colors.color_20_776E94,
                    position = position++
                )
            )
            if (couponDetails != null) {
                mandateEducationResp?.let {
                    headerList.add(
                        it.mandateEducationPageItem.copy(
                            isExpanded = false
                        )
                    )
                }
            } else {
                mandateEducationResp?.let { headerList.add(it.mandateEducationPageItem) }
            }
            headerList
        }

    var fetchCurrentBuyPriceResponse: FetchCurrentGoldPriceResponse? = null
    private var fetchBuyPriceJob: Job? = null

    fun applyManuallyEnteredCouponCode(
        couponCode: String,
        initiateMandatePaymentRequest: InitiateMandatePaymentRequest
    ) {
        viewModelScope.launch {
            if (fetchCurrentBuyPriceResponse != null) {
                applyCouponUseCase.applyCouponCode(
                    initiateMandatePaymentRequest.mandateAmount,
                    null,
                    couponCode,
                    null,
                    fetchCurrentBuyPriceResponse!!
                )
                    .collect(
                        onLoading = {
                            _applyCouponCodeFlow.emit(RestClientResult.loading())
                        },
                        onSuccess = { applyRes ->
                            val couponCodes = couponCodeList
                            val newList = couponCodes?.map {
                                it.copy(
                                    isSelected = true,
                                    couponAppliedDescription = applyRes?.couponCodeDesc,
                                    title = applyRes?.title,
                                    description = applyRes?.couponCodeDesc,
                                    validityInMillis = applyRes?.validity
                                )
                            }.orEmpty()
                            couponCodeList = newList
                            _couponCodesFlow.emit(RestClientResult.success(newList))
                            _applyCouponCodeFlow.emit(RestClientResult.success(applyRes))
                        },
                        onError = { errorMessage, errorCode ->
                            _applyCouponCodeFlow.emit(RestClientResult.error(errorMessage))
                        }
                    )
            }
        }
    }

    private fun fetchCurrentGoldBuyPrice() {
        fetchBuyPriceJob?.cancel()
        fetchBuyPriceJob = viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY)
                .collectUnwrapped(
                    onLoading = {
                        _currentGoldBuyPriceFlow.emit(RestClientResult.loading())
                    },
                    onSuccess = {
                        fetchCurrentBuyPriceResponse = it.data
                        _currentGoldBuyPriceFlow.emit(RestClientResult.success(it))
                    },
                    onError = { errorMessage, errorCode ->
                        _currentGoldBuyPriceFlow.emit(RestClientResult.error(errorMessage))
                    }
                )
        }
    }


    private suspend fun getUpiAppsSection(
        preferredBankPageItem: PreferredBankPageItem?,
        whiteListedApps: List<String>?
    ): List<BasePaymentPageItem> =
        withContext(Dispatchers.Default) {
            val upiAppSectionList = mutableListOf<BasePaymentPageItem>()
            upiAppSectionList.add(
                SpacePaymentPageItem(
                    space = 32,
                    bgColor = com.jar.app.core_base.shared.CoreBaseMR.colors.transparent_moko,
                    position = position++
                )
            )
            upiAppSectionList.add(
                TitlePaymentPageItem(
                    title = MR.strings.feature_mandate_payment_choose_payment_method,
                    textSize = 20
                )
            )
            upiAppSectionList.add(
                SpacePaymentPageItem(
                    space = 15,
                    bgColor = com.jar.app.core_base.shared.CoreBaseMR.colors.transparent_moko,
                    position = position++
                )
            )
            upiAppSectionList.add(
                DescriptionPaymentPageItem(
                    MR.strings.feature_mandate_payment_using_upi_app,
                    null
                )
            )
            preferredBankPageItem?.let {
                upiAppSectionList.add(
                    SpacePaymentPageItem(
                        space = 16,
                        bgColor = com.jar.app.core_base.shared.CoreBaseMR.colors.transparent_moko,
                        position = position++
                    )
                )
                upiAppSectionList.add(it)
            }
            upiAppSectionList.addAll(
                getEligibleUpiApps(
                    whiteListedApps = whiteListedApps
                )
            )
            upiAppSectionList
        }

    private suspend fun getEligibleUpiApps(
        whiteListedApps: List<String>?
    ): List<BasePaymentPageItem> =
        withContext(Dispatchers.Default) {
            val list = mutableListOf<String>()
            val installedApps = fetchInstalledUpiApps()

            val tempList =
                //Only filter apps if there is more than 1 app which supports mandate
                if (whiteListedApps.isNullOrEmpty().not() && installedApps.size > 1) {
                    val filteredApps = installedApps.filter { whiteListedApps!!.contains(it) }
                    // If filteredApps are empty then return whatever is installed
                    filteredApps.ifEmpty { installedApps }
                } else {
                    installedApps
                }
                    .toMutableList()

            // Does List Contains Google Pay
            if (tempList.contains(MandatePaymentBuildKonfig.GPAY_PACKAGE)) {
                if (remoteConfigApi.isGooglePaySupportingAllBanksForMandate().not()) {
                    if (remoteConfigApi.shouldShowGooglePayIfNoOtherAppsForMandate()) {
                        // Only Show Google Pay if that is the only app present
                        if (tempList.size > 1) {
                            tempList.remove(MandatePaymentBuildKonfig.GPAY_PACKAGE)
                        }
                    } else {
                        // Remove Google Pay If Other Apps Are Present As It Currently Support Only ICICI & HDFC With PhonePE PG.
                        tempList.remove(MandatePaymentBuildKonfig.GPAY_PACKAGE)
                    }
                } else {
                    // If Google Pay Is Supporting All Banks Via PhonePe PG Then Don't Remove It From The List..
                }
            }

            if (tempList.isNotEmpty()) {
                tempList.forEach {
                    list.add(it)
                }
            }

            val sortedList = list.sortedWith(CustomAppListSorter())
            val finalList = mutableListOf<BasePaymentPageItem>()

            val shownUpiAppList = ArrayList<String>()
            sortedList.forEachIndexed { index, upiApp ->
                shownUpiAppList.add(upiApp)
                finalList.add(
                    SpacePaymentPageItem(
                        space = if (index == 0) 12 else 8,
                        bgColor = com.jar.app.core_base.shared.CoreBaseMR.colors.transparent_moko,
                        position = position++
                    )
                )
                finalList.add(UpiAppPaymentPageItem(upiApp, index == 0))
            }

            val installedAppsNames = installedApps.joinToString(
                separator = ",",
                transform = { fetchAppNameFromPackageName(it).orEmpty() })

            analyticsApi.postEvent(
                MandatePaymentEventKey.Shown_AvailableUpiApps, mapOf(
                    MandatePaymentEventKey.PaymentMethod to MandatePaymentEventKey.MandatePayment,
                    MandatePaymentEventKey.AvailableUpiAppName to installedAppsNames.ifEmpty { "NA" },
                    MandatePaymentEventKey.UserLifecycle to paymentPageHeaderDetail?.userLifecycle.orEmpty(),
                    MandatePaymentEventKey.AvailableUpiAppPackageName to installedApps.joinToString(
                        separator = ","
                    ),
                    MandatePaymentEventKey.UpiAppsShown to if (shownUpiAppList.isNotEmpty()) shownUpiAppList.joinToString(
                        separator = ","
                    ) else "NA",
                )
            )
            finalList
        }

    private suspend fun getUpiCollectSection(): List<BasePaymentPageItem> =
        withContext(Dispatchers.Default) {
            val upiCollectSection = mutableListOf<BasePaymentPageItem>()
            upiCollectSection.add(
                SpacePaymentPageItem(
                    space = 40,
                    bgColor = com.jar.app.core_base.shared.CoreBaseMR.colors.transparent_moko,
                    position = position++
                )
            )
            upiCollectSection.add(
                DescriptionPaymentPageItem(
                    description = MR.strings.feature_mandate_payment_saved_upi_id,
                    icon = null
                )
            )
            upiCollectSection.add(
                SpacePaymentPageItem(
                    space = 12,
                    bgColor = com.jar.app.core_base.shared.CoreBaseMR.colors.transparent_moko,
                    position = position++
                )
            )
            upiCollectSection.add(UpiCollectPaymentPageItem("${BaseConstants.CDN_BASE_URL}/Images/Mandate_Payment/mandate_upi_apps.webp"))
            upiCollectSection
        }

    fun updateSelectedState(basePaymentPageItem: BasePaymentPageItem) {
        viewModelScope.launch(Dispatchers.Default) {
            val newList = mutableListOf<BasePaymentPageItem>()
            currentPaymentPageItemList?.forEachIndexed { index, tempPaymentPageItem ->
                when (tempPaymentPageItem) {
                    is TitlePaymentPageItem -> {
                        newList.add(tempPaymentPageItem.copy())
                    }

                    is DescriptionPaymentPageItem -> {
                        newList.add(tempPaymentPageItem.copy())
                    }

                    is SeparatorPaymentPageItem -> {
                        newList.add(tempPaymentPageItem)
                    }

                    is SpacePaymentPageItem -> {
                        newList.add(tempPaymentPageItem.copy())
                    }

                    is UpiAppPaymentPageItem -> {
                        var isSelected = false
                        if (basePaymentPageItem is UpiAppPaymentPageItem) {
                            isSelected =
                                if (tempPaymentPageItem.upiAppPackageName == basePaymentPageItem.upiAppPackageName) {
                                    tempPaymentPageItem.isSelected.not()
                                } else false
                        }
                        newList.add(tempPaymentPageItem.copy(isSelected = isSelected))
                    }

                    is UpiCollectPaymentPageItem -> {
                        var isSelected = false
                        if (basePaymentPageItem is UpiCollectPaymentPageItem) {
                            isSelected = tempPaymentPageItem.isSelected.not()
                        }
                        newList.add(tempPaymentPageItem.copy(isSelected = isSelected))
                    }

                    is MandateEducationPageItem -> {
                        newList.add(tempPaymentPageItem.copy())
                    }

                    is CouponCodeResponseForMandateScreenItem -> {
                        newList.add((tempPaymentPageItem.copy()))
                    }

                    is PreferredBankPageItem -> {
                        newList.add(tempPaymentPageItem.copy())
                    }
                }
            }
            _paymentPageLiveData.emit(newList)
        }
    }

    private fun fetchPreferredBank() {
        viewModelScope.launch {
            fetchPreferredBankUseCase.fetchPreferredBank().collect {
                _preferredBankLiveData.emit(it)
            }
        }
    }

    fun verifyUpiAddress(upiAddress: String) {
        viewModelScope.launch {
            verifyUpiAddressUseCase.verifyUpiAddress(upiAddress).collectLatest {
                _verifyUpiAddressLiveData.emit(it)
            }
        }
    }

    fun updateVerifyUpiAddressErrorMessage(
        errorMessage: String?,
        currentList: List<BasePaymentPageItem>
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newList = mutableListOf<BasePaymentPageItem>()
            currentList.forEach { basePaymentPageItem ->
                when (basePaymentPageItem) {
                    is TitlePaymentPageItem -> {
                        newList.add(basePaymentPageItem.copy())
                    }

                    is DescriptionPaymentPageItem -> {
                        newList.add(basePaymentPageItem.copy())
                    }

                    is SeparatorPaymentPageItem -> {
                        newList.add(basePaymentPageItem)
                    }

                    is SpacePaymentPageItem -> {
                        newList.add(basePaymentPageItem.copy())
                    }

                    is UpiAppPaymentPageItem -> {
                        newList.add(basePaymentPageItem.copy())
                    }

                    is UpiCollectPaymentPageItem -> {
                        newList.add(basePaymentPageItem.copy(errorMessage = errorMessage))
                    }

                    is CouponCodeResponseForMandateScreenItem -> {
                        newList.add(basePaymentPageItem.copy())
                    }

                    is MandateEducationPageItem -> {
                        newList.add(basePaymentPageItem.copy())
                    }

                    is PreferredBankPageItem -> {
                        newList.add(basePaymentPageItem.copy())
                    }
                }
            }
            _paymentPageLiveData.emit(newList)
        }
    }

    fun getExitSurveyData(subscriptionType: String?) {
        viewModelScope.launch {
            val fromWhichScreen = if (subscriptionType == "DAILY_SAVINGS") {
                ExitSurveyRequestEnum.DAILY_SAVINGS_TRANSACTION_SCREEN
            } else {
                ExitSurveyRequestEnum.ROUND_OFFS_TRANSACTION_SCREEN
            }
            fetchExitSurveyQuestionsUseCase.fetchExitSurveyQuestions(fromWhichScreen.toString()).collect(
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

    fun fetchInitiateMandatePaymentData(
        mandatePaymentGateway: MandatePaymentGateway,
        packageName: String,
        initiateMandatePaymentRequest: InitiateMandatePaymentRequest,
        fetchPhonePeVersionCode: () -> String?
    ) {
        viewModelScope.launch {
            initiateMandatePaymentUseCase.initiateMandatePayment(
                InitiateMandatePaymentApiRequest(
                    provider = mandatePaymentGateway.name,
                    mandateAmount = initiateMandatePaymentRequest.mandateAmount,
                    authWorkflowType = initiateMandatePaymentRequest.authWorkflowType.name,
                    packageName = packageName,
                    phonePeVersionCode = fetchPhonePeVersionCode.invoke(),
                    insuranceId = initiateMandatePaymentRequest.insuranceId,
                    subscriptionType = initiateMandatePaymentRequest.subscriptionType,
                    goalId = initiateMandatePaymentRequest.goalId,
                    couponCodeId = initiateMandatePaymentRequest.couponCodeId,
                    subsSetupType = initiateMandatePaymentRequest.subsSetupType
                )
            )
                .collectUnwrapped(
                    onLoading = {
                        _initiateMandatePaymentDataLiveData.emit(RestClientResult.loading())
                    },
                    onSuccess = {
                        it.data?.packageName = packageName
                        _initiateMandatePaymentDataLiveData.emit(RestClientResult.success(it))
                    },
                    onError = { errorMessage, errorCode ->
                        _initiateMandatePaymentDataLiveData.emit(
                            RestClientResult.error(errorMessage)
                        )
                    }
                )
        }
    }

    fun mergeApiResponse(
        paymentPageHeaderDetail: PaymentPageHeaderDetail? = this.paymentPageHeaderDetail,
        mandateEducationResp: MandateEducationResp? = this.mandatePaymentEducationResp,
        preferredBankPageItem: PreferredBankPageItem? = this.preferredBankPageItem,
        couponCodeResponseForMandateScreenItem: CouponCodeResponseForMandateScreenItem? = this.couponCodeResponseForMandateScreenItem,
        enabledPaymentMethodResponse: EnabledPaymentMethodResponse? = this.enabledPaymentMethodResponse
    ) {
        job?.cancel()
        job = viewModelScope.launch {
            val whiteListedUpiApps = enabledPaymentMethodResponse?.whitelistedUpiApps

            val finalList = mutableListOf<BasePaymentPageItem>()

            //Header Section
            if (paymentPageHeaderDetail != null) {
                finalList.addAll(
                    getHeaderSection(
                        paymentPageHeaderDetail = paymentPageHeaderDetail,
                        couponDetails = if (couponCodeList.isNullOrEmpty()) {
                            null
                        } else {
                            couponCodeList?.getOrNull(0)?.let {
                                CouponCodeResponseForMandateScreenItem(
                                    couponCode = it
                                )
                            }
                        },
                        mandateEducationResp = mandateEducationResp
                    )
                )
            }

            //Upi Collect Section
//            finalList.addAll(getUpiCollectSection())

            //Upi App Section
            finalList.addAll(
                getUpiAppsSection(
                    preferredBankPageItem = preferredBankPageItem,
                    whiteListedApps = whiteListedUpiApps
                )
            )
            _paymentPageLiveData.emit(finalList)
        }
    }

    inner class CustomAppListSorter : Comparator<String> {
        private val orderPreference = listOf(
            MandatePaymentBuildKonfig.PHONEPE_PACKAGE,
            MandatePaymentBuildKonfig.PAYTM_PACKAGE,
            MandatePaymentBuildKonfig.GPAY_PACKAGE,
            "in.org.npci.upiapp" // Bhim
        )

        override fun compare(o1: String, o2: String): Int {
            if (orderPreference.contains(o1) && orderPreference.contains(o2)) {
                // Both objects are in our ordered list. Compare them by their position in the list
                return orderPreference.indexOf(o1) - orderPreference.indexOf(o2)
            }

            if (orderPreference.contains(o1)) {
                // o1 is in the ordered list, but o2 isn't. o1 is smaller (i.e. first)
                return -1
            }

            if (orderPreference.contains(o2)) {
                // o2 is in the ordered list, but o1 isn't. o2 is smaller (i.e. first)
                return 1
            }

            return o1.compareTo(o2)
        }
    }
}

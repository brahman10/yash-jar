package com.jar.app.feature_buy_gold_v2.shared.ui

import com.jar.app.core_base.util.addPercentage
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.roundDown
import com.jar.app.core_base.util.roundUp
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousTimeResponse
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByAmountRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByVolumeRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.model.SuggestedAmountData
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentType
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldUpiApp
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchAuspiciousTimeUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchContextBannerUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchSuggestedAmountUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.BuyGoldRequestType
import com.jar.app.feature_coupon_api.domain.model.ApplyCouponCodeResponse
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_coupon_api.domain.model.CouponCodeResponse
import com.jar.app.feature_coupon_api.domain.model.CouponType
import com.jar.app.feature_coupon_api.domain.use_case.ApplyCouponUseCase
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import com.jar.app.feature_user_api.domain.model.BuyGoldPaymentMethodsInfo
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BuyGoldV2FragmentViewModel constructor(
    private val fetchAuspiciousTimeUseCase: FetchAuspiciousTimeUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val buyGoldUseCase: BuyGoldUseCase,
    private val fetchSuggestedAmountUseCase: FetchSuggestedAmountUseCase,
    private val fetchCouponCodeUseCase: FetchCouponCodeUseCase,
    private val applyCouponUseCase: ApplyCouponUseCase,
    private val fetchContextBannerUseCase: FetchContextBannerUseCase,
    private val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _auspiciousTimeFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<AuspiciousTimeResponse>>>(
            RestClientResult.none()
        )
    val auspiciousTimeFlow: CStateFlow<RestClientResult<ApiResponseWrapper<AuspiciousTimeResponse>>>
        get() = _auspiciousTimeFlow.toCommonStateFlow()

    private val _currentGoldBuyPriceFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>()
    val currentGoldBuyPriceFlow: CFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>
        get() = _currentGoldBuyPriceFlow.toCommonFlow()

    private val _volumeFromAmountFlow =
        MutableSharedFlow<RestClientResult<Float>>()
    val volumeFromAmountFlow: CFlow<RestClientResult<Float>>
        get() = _volumeFromAmountFlow.toCommonFlow()

    private val _amountFromVolumeFlow =
        MutableSharedFlow<RestClientResult<Float>>()
    val amountFromVolumeFlow: CFlow<RestClientResult<Float>>
        get() = _amountFromVolumeFlow.toCommonFlow()

    private val _suggestedAmountFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<SuggestedAmountData?>>>()
    val suggestedAmountFlow: CFlow<RestClientResult<ApiResponseWrapper<SuggestedAmountData?>>>
        get() = _suggestedAmountFlow.toCommonFlow()

    private val _couponCodesFlow =
        MutableSharedFlow<RestClientResult<CouponCodeResponse?>>()
    val couponCodesFlow: CFlow<RestClientResult<CouponCodeResponse?>>
        get() = _couponCodesFlow.toCommonFlow()

    private val _applyCouponCodeFlow =
        MutableSharedFlow<RestClientResult<ApplyCouponCodeResponse?>>()
    val applyCouponCodeFlow: CFlow<RestClientResult<ApplyCouponCodeResponse?>>
        get() = _applyCouponCodeFlow.toCommonFlow()

    private val _payableAmountFlow = MutableStateFlow<Float?>(null)
    val payableAmountFlow: CFlow<Float?>
        get() = _payableAmountFlow.toCommonFlow()

    private val _rewardAmountFlow = MutableStateFlow<Float?>(null)
    val rewardAmountFlow: CFlow<Float?>
        get() = _rewardAmountFlow.toCommonFlow()

    private val _buyGoldFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val buyGoldFlow: CFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _buyGoldFlow.toCommonFlow()

    private val _isCouponAppliedFlow = MutableStateFlow<Boolean?>(null)
    val isCouponAppliedFlow: CFlow<Boolean?>
        get() = _isCouponAppliedFlow.toCommonFlow()

    private val _preApplyCouponFlow = MutableSharedFlow<Pair<CouponCode, Float>?>()
    val preApplyCouponFlow: CFlow<Pair<CouponCode, Float>?>
        get() = _preApplyCouponFlow.toCommonFlow()

    private val _appliedCoupon = MutableStateFlow<CouponCode?>(null)
    val appliedCoupon = _appliedCoupon.toCommonFlow()

    private val _exitSurveyResponse = MutableSharedFlow<Boolean?>()
    val exitSurveyResponse: SharedFlow<Boolean?>
        get() = _exitSurveyResponse

    var apiResponseCount = 0

    fun applyCoupon(couponCode: CouponCode) {
        _appliedCoupon.value = couponCode
    }

    fun clearCoupon() {
        _appliedCoupon.value = null
    }

    init {
        viewModelScope.launch {
            _couponCodesFlow.collectLatest {
                val isCouponApplied = it.data?.couponCodes?.find { it.isSelected }?.isSelected.orFalse()
                _isCouponAppliedFlow.emit(isCouponApplied)
            }
        }

        viewModelScope.launch {
            _couponCodesFlow.combine(_suggestedAmountFlow) { coupons, suggestedOptions ->
                val preApplyCoupon = coupons.data?.couponCodes?.firstOrNull { it.preApply == true }
                val suggestedAmount =
                    suggestedOptions.data?.data?.suggestedAmount?.prefillAmount
                        ?: suggestedOptions.data?.data?.suggestedAmount?.options?.find { it.recommended.orFalse() }?.amount
                        ?: preApplyCoupon?.minimumAmount.orZero()
                val data = if (preApplyCoupon != null && suggestedAmount.orZero() != 0f) {
                    Pair(preApplyCoupon, suggestedAmount)
                } else
                    null
                return@combine data
            }.collectLatest {
                _preApplyCouponFlow.emit(it)
            }
        }
    }

    var fetchCurrentBuyPriceResponse: FetchCurrentGoldPriceResponse? = null

    private var fetchVolumeFromAmountJob: Job? = null
    private var fetchAmountFromVolumeJob: Job? = null
    private var fetchBuyPriceJob: Job? = null

    var buyAmount = 0.0f
    var buyVolume = 0.0f
    var currentGoldPrice = 0.0f
    var buyGoldRequestType = BuyGoldRequestType.AMOUNT
    var isAuspiciousTime = false

    var couponCodeResponse:CouponCodeResponse? = null

    var buyGoldPaymentMethodsInfo: BuyGoldPaymentMethodsInfo? = null
    var lastUsedUpiApp: UpiApp? = null
    var selectedUpiApp: BuyGoldUpiApp? = null
    var buyGoldPaymentType: BuyGoldPaymentType = BuyGoldPaymentType.PAYMENT_MANGER
    var initiatePaymentResponse: InitiatePaymentResponse? = null

    fun fetchData(buyGoldFlowContext: String) {
        fetchAuspiciousTime()
        fetchCouponCodes(buyGoldFlowContext)
    }

     suspend fun fetchContextBanner(flowContext: String) =  fetchContextBannerUseCase.fetchContextBanner(flowContext)

    /**
     * We are passing [selectedCouponCode] in fetch suggested coupon API because
     * we are getting the prefill amount from BE in this API
     * And BE needs to update the prefill amount logic based on user-selected coupon
     * ==============================================================================
     * [context] is the flow from which user is coming to buy gold screen.
     * It can be Jackpot Screen, Weekly Magic, HomePage, etc.
     * */
    fun fetchSuggestedAmount(selectedCouponCode: String?, flowContext: String?) {
        viewModelScope.launch {
            fetchSuggestedAmountUseCase.fetchSuggestedAmount(flowContext = flowContext, couponCode = selectedCouponCode)
                .collect {
                    buyGoldPaymentMethodsInfo = it.data?.data?.suggestedAmount?.paymentMethodsInfo
                    it.data?.data?.suggestedAmount?.options?.find { suggestedOption -> suggestedOption.prefill.orFalse() }
                        ?.let { bestOption ->
                            bestOption.isBestTag = true
                        } ?: kotlin.run {
                        it.data?.data?.suggestedAmount?.options?.find { suggestedOption -> suggestedOption.recommended.orFalse() }
                            ?.let { bestOption ->
                                bestOption.isBestTag = true
                            }
                    }
                    it.data?.data?.suggestedAmount?.volumeOptions?.find { suggestedOption -> suggestedOption.prefill.orFalse() }
                        ?.let { bestOption ->
                            bestOption.isBestTag = true
                        } ?: kotlin.run {
                        it.data?.data?.suggestedAmount?.volumeOptions?.find { suggestedOption -> suggestedOption.recommended.orFalse() }
                            ?.let { bestOption ->
                                bestOption.isBestTag = true
                            }
                    }
                    _suggestedAmountFlow.emit(it)
                }
        }
    }

    /**
     * [context] is the flow from which user is coming (ex: jackpot, weekly magic etc)
     * We are passing [context] in fetch coupon code API because BE is deciding
     * which coupon to preselect based on the flow from which the user is coming.
     * For ex: If user is coming from Winnings tab then winnings context is passed
     * and BE set pre apply to true for winnings coupon.
     * */
    private fun fetchCouponCodes(context: String) {

        viewModelScope.launch {
            fetchCouponCodeUseCase.fetchCouponCodes(context = context).collect(
                onLoading = {
                    _couponCodesFlow.emit(RestClientResult.loading())
                },
                onSuccess = {
                    couponCodeResponse = it
                    _couponCodesFlow.emit(RestClientResult.success(couponCodeResponse))
                },
                onError = { errorMessage, _ ->
                    _couponCodesFlow.emit(RestClientResult.error(errorMessage))
                }
            )
        }
    }

    private fun fetchAuspiciousTime() {
        viewModelScope.launch {
            fetchAuspiciousTimeUseCase.fetchIsAuspiciousTime().collect {
                _auspiciousTimeFlow.emit(it)
            }
        }
    }

    fun fetchCurrentGoldBuyPrice() {
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

    fun calculateVolumeFromAmount(amount: Float) {
        fetchVolumeFromAmountJob?.cancel()
        fetchVolumeFromAmountJob = viewModelScope.launch {
            buyAmount = amount
            postPaymentStripData(
                amount = amount,
                couponCodeResponse?.couponCodes?.find { it.isSelected }
            )
            buyGoldUseCase.calculateVolumeFromAmount(amount, fetchCurrentBuyPriceResponse).collect {
                _volumeFromAmountFlow.emit(it)
                it.data?.let {
                    buyVolume = it
                }
            }
        }
    }

    fun calculateAmountFromVolume(volume: Float) {
        fetchAmountFromVolumeJob?.cancel()
        fetchAmountFromVolumeJob = viewModelScope.launch {
            buyVolume = volume
            buyGoldUseCase.calculateAmountFromVolume(volume, fetchCurrentBuyPriceResponse).collect {
                _amountFromVolumeFlow.emit(it)
                it.data?.let {
                    val amount = if (volume == 0.0f) 0.0f else it
                    buyAmount = amount
                    postPaymentStripData(
                        amount = amount,
                        null
                    )
                }
            }
        }
    }

    fun getVolumeForXAmount(amount: Float): Float {
        fetchCurrentBuyPriceResponse?.let {
            val currentPriceWithTax = it.price.addPercentage(it.applicableTax!!).roundUp(2)
            return (amount / currentPriceWithTax).roundDown(4)
        } ?: kotlin.run {
            return 0f
        }
    }

    fun deselectAllCoupons() {
        viewModelScope.launch {
            val couponCodes = couponCodeResponse?.couponCodes
            couponCodes?.let{

                val newList = couponCodes.map {
                    it.copy(isSelected = false)
                }
                postPaymentStripData(
                    amount = buyAmount,
                    null
                )
                couponCodeResponse =couponCodeResponse?.copy(couponCodes = newList)
                _couponCodesFlow.emit(RestClientResult.success(couponCodeResponse?.copy(couponCodes = newList)))
            }
        }
    }

    fun canApplyCoupon(couponCode: CouponCode): Boolean {
        return buyAmount >= couponCode.minimumAmount
    }

    fun applyManuallyEnteredCouponCode(couponCode: String,screenName: String) {
        viewModelScope.launch {
            if (fetchCurrentBuyPriceResponse != null) {
                applyCouponUseCase.applyCouponCode(
                    buyAmount,
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
                            val couponCodes = couponCodeResponse?.couponCodes
                            val isCouponExistingInList =
                                couponCodes?.find { it.couponCode == couponCode
                                        ||(applyRes?.couponType == CouponType.WINNINGS.name && it.getCouponType() == CouponType.WINNINGS) } != null
                            val newList = couponCodes?.map {
                                if ((isCouponExistingInList && it.couponCode == couponCode)
                                    || (applyRes?.couponType == CouponType.WINNINGS.name && it.getCouponType() == CouponType.WINNINGS)) {
                                    it.copy(
                                        isSelected = true,
                                        couponAppliedDescription = applyRes?.couponCodeDesc,
                                    )
                                } else {
                                    it.copy(isSelected = false, couponAppliedDescription = null)
                                }
                            }.orEmpty().toMutableList()
                            if (isCouponExistingInList.not()) {
                                val coupon = CouponCode(
                                    couponCodeId = applyRes?.couponCodeId,
                                    couponCode = applyRes?.couponCode!!,
                                    title = applyRes?.title,
                                    validityInMillis = applyRes?.validity,
                                    description = applyRes?.couponCodeDesc,
                                    minimumAmount = applyRes?.offerAmount!!,
                                    isSelected = true,
                                    preApply = false,
                                    couponState = applyRes?.couponState!!,
                                    couponType = applyRes?.couponType!!,
                                    maxAmount = applyRes?.maxAmount,
                                    rewardPercentage = applyRes?.rewardPercentage,
                                    couponAppliedDescription = applyRes?.couponCodeDesc,
                                    currentTimestamp = applyRes?.currentTimestamp
                                )
                                newList.add(
                                    0,
                                    coupon
                                )
                                postPaymentStripData(
                                    amount = buyAmount,
                                    coupon
                                )
                            } else {
                                couponCodes?.find { it.couponCode == couponCode }?.let {
                                    postPaymentStripData(
                                        buyAmount,
                                        it
                                    )
                                }
                            }
                            newList.find { it.isSelected }?.let { applyCoupon(it) }
                            _couponCodesFlow.emit(RestClientResult.success(couponCodeResponse?.copy(couponCodes=newList)))
                            _applyCouponCodeFlow.emit(RestClientResult.success(applyRes?.copy(isManuallyEntered = true)))
                        },
                        onError = { errorMessage, errorCode ->
                            _applyCouponCodeFlow.emit(RestClientResult.error(errorMessage))
                        }
                    )
            }
        }
    }

    fun applyCouponCode(
        couponCode: String,
        couponType: String
    ) {
        viewModelScope.launch {
            if (fetchCurrentBuyPriceResponse != null) {
                applyCouponUseCase.applyCouponCode(
                    buyAmount,
                    null,
                    couponCode,
                    couponType,
                    fetchCurrentBuyPriceResponse!!
                ).collect(
                    onLoading = {
                        _applyCouponCodeFlow.emit(RestClientResult.loading())
                    },
                    onSuccess = { applyRes ->
                        val couponCodes = couponCodeResponse?.couponCodes
                        val coupon = couponCodes?.find { it.couponCode == couponCode }
                        val newList = couponCodes?.map {
                            if (it.couponCode == couponCode || (couponType == CouponType.WINNINGS.name && it.getCouponType() == CouponType.WINNINGS)) {
                                it.copy(
                                    isSelected = true,
                                    couponAppliedDescription = applyRes?.couponCodeDesc
                                )
                            } else {
                                it.copy(isSelected = false, couponAppliedDescription = null)
                            }
                        }.orEmpty()
                        coupon?.let {
                            postPaymentStripData(
                                amount = buyAmount,
                                it
                            )
                        }
                        couponCodeResponse =couponCodeResponse?.copy(couponCodes = newList)
                        _couponCodesFlow.emit(RestClientResult.success(couponCodeResponse?.copy(couponCodes = newList)))
                        _applyCouponCodeFlow.emit(RestClientResult.success(applyRes))
                    },
                    onError = { errorMessage, errorCode ->
                        _applyCouponCodeFlow.emit(RestClientResult.error(errorMessage))
                    }
                )
            }
        }
    }

    fun applyCouponCode(
        couponCode: CouponCode,
        screenName: String
    ) {
        viewModelScope.launch {
            if (fetchCurrentBuyPriceResponse != null) {
                applyCouponUseCase.applyCouponCode(
                    buyAmount,
                    couponCode.couponCodeId,
                    couponCode.couponCode,
                    couponCode.couponType,
                    fetchCurrentBuyPriceResponse!!
                ).collect(
                    onLoading = {
                        _applyCouponCodeFlow.emit(RestClientResult.loading())
                    },
                    onSuccess = { applyRes ->
                        applyCoupon(couponCode)
                        val couponCodes = couponCodeResponse?.couponCodes
                        val newList = couponCodes?.map {
                            if (it.couponCode == couponCode.couponCode) {
                                it.copy(
                                    isSelected = true,
                                    couponAppliedDescription = applyRes?.couponCodeDesc
                                )
                            } else {
                                it.copy(isSelected = false, couponAppliedDescription = null)
                            }
                        }.orEmpty()

                        postPaymentStripData(
                            amount = buyAmount,
                            couponCode
                        )
                        couponCodeResponse =couponCodeResponse?.copy(couponCodes = newList)
                        _couponCodesFlow.emit(RestClientResult.success(couponCodeResponse))
                        _applyCouponCodeFlow.emit(RestClientResult.success(applyRes?.copy(screenName = screenName)))
                    },
                    onError = { errorMessage, errorCode ->
                        _applyCouponCodeFlow.emit(RestClientResult.error(errorMessage))
                    }
                )
            }
        }
    }

    fun buyGoldByAmount(buyGoldByAmountRequest: BuyGoldByAmountRequest) {
        viewModelScope.launch {
            buyGoldUseCase.buyGoldByAmount(buyGoldByAmountRequest).collect {
                initiatePaymentResponse = it.data?.data
                _buyGoldFlow.emit(it)
            }
        }
    }

    fun buyGoldByVolume(buyGoldByVolumeRequest: BuyGoldByVolumeRequest) {
        viewModelScope.launch {
            buyGoldUseCase.buyGoldByVolume(buyGoldByVolumeRequest).collect {
                initiatePaymentResponse = it.data?.data
                _buyGoldFlow.emit(it)
            }
        }
    }

    private suspend fun postPaymentStripData(amount: Float, couponCode: CouponCode?) {
        _payableAmountFlow.emit(amount)
        val extraAmount = couponCode?.getMaxRewardThatCanBeAvailed(amount).orZero()
        _rewardAmountFlow.emit(extraAmount)


        couponCode?.fixedDiscount?.let {
            _rewardAmountFlow.emit(it)
        } ?: run {
            _rewardAmountFlow.emit(couponCode?.getMaxRewardThatCanBeAvailed(amount).orZero())
        }
    }

    fun resetAppliedCoupon() {
        viewModelScope.launch {
            _appliedCoupon.emit(null)
        }
    }

    fun getApplyCouponErrorMessage(
        couponCode: CouponCode
    ): StringResource? {
        return when {
            buyAmount < couponCode.minimumAmount -> {
                MR.strings.feature_buy_gold_v2_min_amount_of_n_is_req_for_this_coupon
            }

            else -> null
        }
    }

    fun getExitSurveyData() {
        viewModelScope.launch {
            fetchExitSurveyQuestionsUseCase.fetchExitSurveyQuestions(ExitSurveyRequestEnum.MANUAL_BUY.toString()).collect(
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
}
package com.jar.android.feature_post_setup.impl.ui.setup_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.android.feature_post_setup.CalendarUtil
import com.jar.android.feature_post_setup.impl.model.CalendarDayStatus
import com.jar.android.feature_post_setup.impl.model.getDayStatus
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.getFormattedDate
import com.jar.app.core_base.domain.model.GenericFaqList
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_daily_investment.shared.domain.model.PauseDailySavingData
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPauseDetails
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.FetchDailyInvestmentPauseDataUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_post_setup.domain.model.BottomSectionPageItem
import com.jar.app.feature_post_setup.domain.model.CalenderViewPageItem
import com.jar.app.feature_post_setup.domain.model.DSFailureInfo
import com.jar.app.feature_post_setup.domain.model.PostSetupFaqPageItem
import com.jar.app.feature_post_setup.domain.model.PostSetupPageItem
import com.jar.app.feature_post_setup.domain.model.SettingPageItem
import com.jar.app.feature_post_setup.domain.model.SetupDetailsPageItem
import com.jar.app.feature_post_setup.domain.model.StateAmountInfoPageItem
import com.jar.app.feature_post_setup.domain.model.UserPostSetupData
import com.jar.app.feature_post_setup.domain.model.calendar.CalendarDataResp
import com.jar.app.feature_post_setup.domain.model.calendar.FeaturePostSetUpCalendarInfo
import com.jar.app.feature_post_setup.domain.model.calendar.CalendarSavingOperations
import com.jar.app.feature_post_setup.domain.model.calendar.StateInfoDetails
import com.jar.app.feature_post_setup.domain.model.setting.PostSetupQuickActionList
import com.jar.app.feature_post_setup.domain.use_case.FetchPostSetupCalenderDataUseCase
import com.jar.app.feature_post_setup.domain.use_case.FetchPostSetupDSFailureInfoUseCase
import com.jar.app.feature_post_setup.domain.use_case.FetchPostSetupGenericFaqUseCase
import com.jar.app.feature_post_setup.domain.use_case.FetchPostSetupQuickActionsUseCase
import com.jar.app.feature_post_setup.domain.use_case.FetchPostSetupSavingOperationsUseCase
import com.jar.app.feature_post_setup.domain.use_case.FetchPostSetupUserDataUseCase
import com.jar.app.feature_post_setup.domain.use_case.InitiateFailedPaymentsUseCase
import com.jar.app.feature_post_setup.shared.PostSetupMR
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.DisableUserSavingsUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_settings.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_user_api.domain.model.PauseSavingOption
import com.jar.app.feature_user_api.domain.model.PauseSavingOptionWrapper
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.app.feature_user_api.domain.use_case.UpdatePauseSavingUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
internal class PostSetupDetailsViewModel @Inject constructor(
    private val fetchPostSetupUserDataUseCase: FetchPostSetupUserDataUseCase,
    private val fetchPostSetupCalenderDataUseCase: FetchPostSetupCalenderDataUseCase,
    private val fetchPostSetupSavingOperationsUseCase: FetchPostSetupSavingOperationsUseCase,
    private val fetchPostSetupQuickActionsUseCase: FetchPostSetupQuickActionsUseCase,
    private val fetchPostSetupGenericFaqUseCase: FetchPostSetupGenericFaqUseCase,
    private val initiateFailedPaymentsUseCase: InitiateFailedPaymentsUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val fetchPostSetupDSFailureInfoUseCase: FetchPostSetupDSFailureInfoUseCase,
    private val updatePauseSavingUseCase: UpdatePauseSavingUseCase,
    private val updateSavingPauseDurationUseCase: UpdateSavingPauseDurationUseCase,
    private val disableUserSavingsUseCase: DisableUserSavingsUseCase,
    private val calendarUtil: CalendarUtil,
    private val pauseDetailsUseCase: FetchDailyInvestmentPauseDataUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val analyticsHandler: AnalyticsApi
) : ViewModel(), BaseResources {

    companion object {
        private const val HEADER_ORDER = 10
        private const val CALENDAR_ORDER = 20
        private const val AMOUNT_INFO_ORDER = 30
        private const val QUICK_ACTIONS_ORDER = 40
        private const val FAQ_ORDER = 50
        private const val FOOTER_ORDER = 100
    }

    private val _postSetupPageLiveData =
        SingleLiveEvent<RestClientResult<List<PostSetupPageItem>>>()
    val postSetupPageLiveData: LiveData<RestClientResult<List<PostSetupPageItem>>>
        get() = _postSetupPageLiveData

    private val _postSetupUserDataLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserPostSetupData>>>()
    val postSetupUserDataLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserPostSetupData>>>
        get() = _postSetupUserDataLiveData

    private val _savingOperationsLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<CalendarSavingOperations>>>()
    val savingOperationsLiveData: LiveData<RestClientResult<ApiResponseWrapper<CalendarSavingOperations>>>
        get() = _savingOperationsLiveData

    private val _calenderLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<CalendarDataResp>>>()
    val calenderLiveData: LiveData<RestClientResult<ApiResponseWrapper<CalendarDataResp>>>
        get() = _calenderLiveData

    private val _faqsLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<GenericFaqList>>>()
    val faqsLiveData: LiveData<RestClientResult<ApiResponseWrapper<GenericFaqList>>>
        get() = _faqsLiveData

    private val _quickActionsLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<PostSetupQuickActionList>>>()
    val quickActionsLiveData: LiveData<RestClientResult<ApiResponseWrapper<PostSetupQuickActionList>>>
        get() = _quickActionsLiveData

    private val _dailySavingsDetailsLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val dailySavingsDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _dailySavingsDetailsLiveData

    private val _failedPaymentLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse>>>()
    val failedPaymentLiveData: LiveData<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse>>>
        get() = _failedPaymentLiveData

    private val _pauseOptionsLiveData =
        SingleLiveEvent<RestClientResult<ArrayList<PauseSavingOptionWrapper>>>()
    val pauseOptionsLiveData: LiveData<RestClientResult<ArrayList<PauseSavingOptionWrapper>>>
        get() = _pauseOptionsLiveData

    private val _dailySavingPauseLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>()
    val dailySavingPauseLiveData: LiveData<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _dailySavingPauseLiveData

    private val _dsFailureInfoLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<DSFailureInfo?>>>()
    val dsFailureInfoLiveData: LiveData<RestClientResult<ApiResponseWrapper<DSFailureInfo?>>>
        get() = _dsFailureInfoLiveData

    private val _roundOffDetailsLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val roundOffDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _roundOffDetailsLiveData

    private val _updatePauseDurationFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>(RestClientResult.none())
    val updatePauseDurationFlow: CStateFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _updatePauseDurationFlow.toCommonStateFlow()

    private val _pauseDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentPauseDetails>>>(RestClientResult.none())
    val pauseDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentPauseDetails>>>
        get() = _pauseDetailsFlow.toCommonStateFlow()

    private val _disableDailySavingFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>(RestClientResult.none())
    val disableDailySavingFlow: CStateFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _disableDailySavingFlow.toCommonStateFlow()

    var totalSelectedDays = MutableStateFlow<Int?>(null)

    var monthIndex = calendarUtil.getCurrentMonthIndex()

    var dsFailureInfo: DSFailureInfo? = null
    var calenderViewPageItem: CalenderViewPageItem? = null
    var stateAmountInfoPageItem: StateAmountInfoPageItem? = null
    var calenderMonthName = ""
    var shouldFetchQuickActions = true
    var currentMonth = calendarUtil.getMonthString(monthIndex = monthIndex)
    var currentDate = Date().getFormattedDate("dd-MM-yyyy")
    var isSavingPaused = false
    var isSavingsEnabled = false

    private var job: Job? = null

    fun fetchPauseDetailsDataFlow() {
        viewModelScope.launch {
            pauseDetailsUseCase.fetchDailyInvestmentPauseData().collectLatest {
                _pauseDetailsFlow.emit(it)
            }
        }
    }

    fun updateAutoInvestPauseDurationFlow(pause: Boolean, pauseDailySavingData: PauseDailySavingData?) {
        viewModelScope.launch {
            updateSavingPauseDurationUseCase.updateSavingPauseDuration(
                pause,
                pauseDailySavingData?.pauseDailySavingsOption?.name,
                SavingsType.DAILY_SAVINGS
            )
                .collectLatest {
                    _updatePauseDurationFlow.emit(it)
                }
        }
    }

    fun disableDailySavings() {
        viewModelScope.launch {
            disableUserSavingsUseCase.disableSavings(SavingsType.DAILY_SAVINGS).collectLatest {
                _disableDailySavingFlow.emit(it)
            }
        }
    }

    fun fetchPostSetupUserData() {
        _postSetupPageLiveData.postValue(RestClientResult.loading())
        viewModelScope.launch {
            fetchPostSetupUserDataUseCase.fetchPostSetupUserData().collect {
                _postSetupUserDataLiveData.postValue(it)
            }
        }
    }

    fun fetchPostSetupSavingOperations() {
        viewModelScope.launch {
            fetchPostSetupSavingOperationsUseCase.fetchPostSetupSavingOperations().collect {
                _savingOperationsLiveData.postValue(it)
            }
        }
    }

    fun fetchPostSetupCalenderData() {
        viewModelScope.launch {
            if (shouldFetchQuickActions.not()) {
                _postSetupPageLiveData.postValue(RestClientResult.loading())
            }
            if (monthIndex == -1) {
                monthIndex = CalendarUtil.LAST_MONTH_IN_YEAR_INDEX
                calendarUtil.updateCurrentCalenderYear(shouldIncreaseYear = false)
            } else if (monthIndex == 12) {
                monthIndex = CalendarUtil.FIRST_MONTH_IN_YEAR_INDEX
                calendarUtil.updateCurrentCalenderYear(shouldIncreaseYear = true)
            }
            fetchPostSetupCalenderDataUseCase.fetchPostSetupCalendarData(
                startDate = calendarUtil.getMonthFirstDay(monthIndex),
                endDate = calendarUtil.getMonthLastDay(monthIndex)
            ).collect {
                _calenderLiveData.postValue(it)
            }
        }
    }

    fun fetchPostSetupQuickAction() {
        viewModelScope.launch {
            shouldFetchQuickActions = false
            fetchPostSetupQuickActionsUseCase.fetchPostSetupQuickActions().collect {
                _quickActionsLiveData.postValue(it)
            }
        }
    }

    fun fetchPostSetupFAQS() {
        viewModelScope.launch {
            fetchPostSetupGenericFaqUseCase.fetchPostSetupFaq().collect {
                _faqsLiveData.postValue(it)
            }
        }
    }

    fun initiateFailedPayment(amount: Float, paymentProvider: String, roundOffIds: List<String>) {
        viewModelScope.launch {
            initiateFailedPaymentsUseCase.initiatePaymentForFailedTransactions(
                amount = amount,
                paymentProvider = paymentProvider,
                type = SavingsType.DAILY_SAVINGS.name,
                roundOffsLinked = roundOffIds
            ).collect {
                _failedPaymentLiveData.postValue(it)
            }
        }
    }

    fun mergeApiResponse(
        userPostSetupData: UserPostSetupData? = postSetupUserDataLiveData.value?.data?.data,
        calendarSavingOperations: CalendarSavingOperations? = savingOperationsLiveData.value?.data?.data,
        calendarDataResp: CalendarDataResp? = calenderLiveData.value?.data?.data,
        quickActionList: PostSetupQuickActionList? = quickActionsLiveData.value?.data?.data,
        genericFaqList: GenericFaqList? = faqsLiveData.value?.data?.data,
    ) {
        job?.cancel()
        job = viewModelScope.launch(dispatcherProvider.default) {
            val list = mutableListOf<PostSetupPageItem>()
            userPostSetupData?.let {
                list.add(
                    SetupDetailsPageItem(
                        order = it.headerOrder ?: HEADER_ORDER, userPostSetupData = it
                    )
                )
            }

            calendarDataResp?.let {
                val calendarData = CalendarDataResp(
                    it.successInfo,
                    it.failureInfo,
                    it.pendingInfo,
                    calendarUtil.createMonthCalendar(monthIndex, it.calendarInfo),
                    it.ladderData
                )
                calenderViewPageItem = CalenderViewPageItem(
                    order = userPostSetupData?.calendarOrder ?: CALENDAR_ORDER,
                    calendarInfo = calendarData.calendarInfo,
                    calendarSavingOperations = calendarSavingOperations,
                    yearAndMonthText = calendarUtil.getMonthAndYearString(monthIndex = monthIndex),
                    isPreviousClickEnabled = calendarUtil.shouldEnablePreviousMonthButton(
                        monthIndex, _postSetupUserDataLiveData.value?.data?.data?.startDate.orZero()
                    ),
                    isNextClickEnabled = calendarUtil.shouldEnableNextMonthButton(
                        monthIndex, _postSetupUserDataLiveData.value?.data?.data?.endDate.orZero()
                    ),
                    ladderData = calendarData.ladderData
                )
                stateAmountInfoPageItem = StateAmountInfoPageItem(
                    order = userPostSetupData?.amountInfoOrder ?: CALENDAR_ORDER,
                    stateInfoDetails = StateInfoDetails(
                        yearAndMonthText = calendarUtil.getMonthAndYearString(monthIndex = monthIndex),
                        successInfo = it.successInfo,
                        failureInfo = it.failureInfo,
                        pendingInfo = it.pendingInfo
                    )
                )
                calenderMonthName = calendarUtil.getMonthString(monthIndex = monthIndex)
                list.add(calenderViewPageItem!!.copy())
                list.add(stateAmountInfoPageItem!!.copy())
            }

            quickActionList?.let {
                list.add(
                    SettingPageItem(
                        order = _postSetupUserDataLiveData.value?.data?.data?.quickActionOrder
                            ?: QUICK_ACTIONS_ORDER,
                        titleRes = PostSetupMR.strings.feature_post_setup_quick_actions,
                        postSetupQuickActionList = it
                    )
                )
            }

            genericFaqList?.let {
                list.add(
                    PostSetupFaqPageItem(
                        order = _postSetupUserDataLiveData.value?.data?.data?.faqOrder ?: FAQ_ORDER,
                        titleRes = PostSetupMR.strings.feature_post_setup_faqs,
                        faq = it.genericFAQs
                    )
                )
                list.add(
                    BottomSectionPageItem(
                        order = _postSetupUserDataLiveData.value?.data?.data?.footerOrder
                            ?: FOOTER_ORDER,
                        imageUrl = BaseConstants.IllustrationUrls.JAR_SECURITY_PARTNERS_ASSET
                    )
                )
            }

            var newList = list.map {
                when (it) {
                    is SetupDetailsPageItem -> it.copy()
                    is CalenderViewPageItem -> it.copy()
                    is StateAmountInfoPageItem -> it.copy()
                    is SettingPageItem -> it.copy()
                    is PostSetupFaqPageItem -> it.copy()
                    is BottomSectionPageItem -> it.copy()
                }
            }

            newList = newList.sortedBy {
                it.getSortKey()
            }
            _postSetupPageLiveData.postValue(RestClientResult.success(newList))
        }
    }

    fun fetchUserDailySavingsDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.DAILY_SAVINGS).collect {
                _dailySavingsDetailsLiveData.postValue(it)
            }
        }
    }

    fun fetchDsFailureInfo() {
        viewModelScope.launch {
            fetchPostSetupDSFailureInfoUseCase.fetchPostSetupFailureInfo().collect {
                _dsFailureInfoLiveData.postValue(it)
            }
        }
    }

    fun updateTransactionStateAmountShimmer(
        currentList: List<PostSetupPageItem>,
        calendarInfo: FeaturePostSetUpCalendarInfo
    ) {
        viewModelScope.launch(dispatcherProvider.default) {
            var newList = currentList.map {
                when (it) {
                    is SetupDetailsPageItem -> it.copy()
                    is CalenderViewPageItem -> it.copy()
                    is StateAmountInfoPageItem -> {
                        val item = it.copy(
                            stateInfoDetails = it.stateInfoDetails.copy(
                                successInfo = it.stateInfoDetails.successInfo?.copy(),
                                failureInfo = it.stateInfoDetails.failureInfo?.copy(),
                                pendingInfo = it.stateInfoDetails.pendingInfo?.copy(),
                                yearAndMonthText = it.stateInfoDetails.yearAndMonthText
                            )
                        )
                        when (calendarInfo.getDayStatus()) {
                            CalendarDayStatus.SUCCESS -> {
                                item.stateInfoDetails.successInfo?.shouldShowShimmer = true
                            }

                            CalendarDayStatus.FAILED -> {
                                item.stateInfoDetails.failureInfo?.shouldShowShimmer = true
                            }

                            CalendarDayStatus.PENDING -> {
                                item.stateInfoDetails.pendingInfo?.shouldShowShimmer = true
                            }

                            else -> {
                                item.stateInfoDetails.successInfo?.shouldShowShimmer = true
                            }
                        }
                        item
                    }

                    is SettingPageItem -> it.copy()
                    is PostSetupFaqPageItem -> it.copy()
                    is BottomSectionPageItem -> it.copy()
                }
            }
            newList = newList.sortedBy {
                it.getSortKey()
            }
            _postSetupPageLiveData.postValue(RestClientResult.success(newList))
        }
    }

    fun fetchPauseOptions() {
        _pauseOptionsLiveData.postValue(RestClientResult.loading())
        viewModelScope.launch {
            _pauseOptionsLiveData.postValue(
                RestClientResult.success(
                    arrayListOf(
                        PauseSavingOptionWrapper(PauseSavingOption.TWO, isSelected = true),
                        PauseSavingOptionWrapper(PauseSavingOption.EIGHT),
                        PauseSavingOptionWrapper(PauseSavingOption.TWELVE),
                        PauseSavingOptionWrapper(PauseSavingOption.FIFTEEN),
                    )
                )
            )
        }
    }

    fun pauseOrResumeDailySavings(shouldPause: Boolean, pauseDuration: String?) {
        viewModelScope.launch {
            updatePauseSavingUseCase.updatePauseSavingValue(
                shouldPause = shouldPause,
                pauseType = SavingsType.DAILY_SAVINGS.name,
                pauseDuration = pauseDuration
            ).collect { _dailySavingPauseLiveData.postValue(it) }
        }
    }

    fun fetchUserRoundOffDetails() {
        viewModelScope.launch {
            fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.ROUND_OFFS).collect {
                _roundOffDetailsLiveData.postValue(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
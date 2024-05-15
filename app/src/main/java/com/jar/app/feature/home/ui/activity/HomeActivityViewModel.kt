package com.jar.app.feature.home.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.jar.app.BuildConfig
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.base.util.orTrue
import com.jar.app.core_analytics.EventKey.LogoutRefreshTokenExpired
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.domain.mapper.toGoldBalance
import com.jar.app.core_base.domain.mapper.toKycProgressResponse
import com.jar.app.core_base.domain.model.InfoDialogResponse
import com.jar.app.core_base.domain.model.KycProgressResponse
import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.BaseConstants.DEFAULT_VALUES_FOR_NO_OF_SMS_SYNC
import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_base.util.orZero
import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.core_network.event.LogoutEvent
import com.jar.app.core_network.event.UnusualActivityDetectedEvent
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_utils.data.NetworkUtil
import com.jar.app.feature.home.domain.model.AdSourceData
import com.jar.app.feature.home.domain.model.DashboardStaticData
import com.jar.app.feature.home.domain.model.DowntimeResponse
import com.jar.app.feature.home.domain.model.ForceUpdateResponse
import com.jar.app.feature.home.domain.model.UserDeviceDetails
import com.jar.app.feature.home.domain.model.UserRatingData
import com.jar.app.feature.home.domain.usecase.CaptureAppOpensUseCase
import com.jar.app.feature.home.domain.usecase.FetchDashboardStaticContentUseCase
import com.jar.app.feature.home.domain.usecase.FetchDowntimeUseCase
import com.jar.app.feature.home.domain.usecase.FetchForceUpdateUseCase
import com.jar.app.feature.home.domain.usecase.FetchIfKycRequiredUseCase
import com.jar.app.feature.home.domain.usecase.FetchPublicStaticContentUseCase
import com.jar.app.feature.home.domain.usecase.FetchUserRatingUseCase
import com.jar.app.feature.home.domain.usecase.UpdateAdSourceDataUseCase
import com.jar.app.feature.home.domain.usecase.UpdateSessionUseCase
import com.jar.app.feature.home.domain.usecase.UpdateUserDeviceDetailsUseCase
import com.jar.app.feature.notification_list.domain.model.NotificationMetaData
import com.jar.app.feature.notification_list.domain.use_case.FetchNotificationMetaDataUseCase
import com.jar.app.feature.survey.domain.model.Survey
import com.jar.app.feature.survey.domain.use_case.FetchUserSurveyUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByAmountRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase
import com.jar.app.feature_gifting.shared.domain.model.GoldGiftReceivedResponse
import com.jar.app.feature_gifting.shared.domain.use_case.FetchReceivedGiftsUseCase
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_homepage.shared.domain.model.FeatureRedirectionResp
import com.jar.app.feature_homepage.shared.domain.model.ShouldSendSmsOnDemand
import com.jar.app.feature_homepage.shared.domain.use_case.ClearCachedHomeFeedUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFeatureRedirectionUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchSmsIngestionUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchStaticPopupInfoUseCase
import com.jar.app.feature_lending.shared.api.usecase.FetchLoanApplicationListUseCase
import com.jar.app.feature_lending.shared.api.usecase.FetchLoanProgressStatusV2UseCase
import com.jar.app.feature_lending.shared.domain.model.LendingFlowStatusResponse
import com.jar.app.feature_mandate_payment_common.impl.util.UpiAppsUtil
import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.LogoutUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_refer_earn_v2.shared.domain.model.PostReferralAttributionData
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferralAttributionDeviceDetails
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralsShareMessageUseCase
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.PostReferralAttributionDataUseCase
import com.jar.app.feature_round_off.shared.domain.use_case.InitiateDetectedSpendPaymentUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_story.data.model.InAppStoryModel
import com.jar.app.feature_story.domain.use_cases.FetchInAppStoriesUseCase
import com.jar.app.feature_user_api.domain.mappers.toUserMetaData
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.app.feature_user_api.domain.model.UserKycStatus
import com.jar.app.feature_user_api.domain.model.UserMetaData
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserKycStatusUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserMetaUseCase
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendHomeScreenTab
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldTrendHomeScreenTabUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.NetworkApiEvent
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.NetworkEventBus
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class HomeActivityViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val buyGoldUseCase: BuyGoldUseCase,
    private val fetchDowntimeUseCase: FetchDowntimeUseCase,
    private val fetchForceUpdateUseCase: FetchForceUpdateUseCase,
    private val updateSessionUseCase: UpdateSessionUseCase,
    private val fetchUserRatingUseCase: FetchUserRatingUseCase,
    private val fetchUserSurveyUseCase: FetchUserSurveyUseCase,
    private val fetchUserMetaDataUseCase: FetchUserMetaUseCase,
    private val fetchKycProgressUseCase: com.jar.app.feature_lending_kyc.shared.api.use_case.FetchKycProgressUseCase,
    private val fetchUserKycStatusUseCase: FetchUserKycStatusUseCase,
    private val fetchReceivedGiftsUseCase: FetchReceivedGiftsUseCase,
    private val updateAdSourceDataUseCase: UpdateAdSourceDataUseCase,
    private val fetchStaticPopupInfoUseCase: FetchStaticPopupInfoUseCase,
    private val fetchUserGoldBalanceUseCase: FetchUserGoldBalanceUseCase,
    private val fetchReferralsShareMessageUseCase: FetchReferralsShareMessageUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val updateUserDeviceDetailsUseCase: UpdateUserDeviceDetailsUseCase,
    private val postReferralAttributionDataUseCase: PostReferralAttributionDataUseCase,
    private val fetchNotificationMetaDataUseCase: FetchNotificationMetaDataUseCase,
    private val fetchDashboardStaticContentUseCase: FetchDashboardStaticContentUseCase,
    private val updateDailyInvestmentStatusUseCase: UpdateDailyInvestmentStatusUseCase,
    private val initiateDetectedSpendPaymentUseCase: InitiateDetectedSpendPaymentUseCase,
    private val fetchLoanProgressStatusV2UseCase: FetchLoanProgressStatusV2UseCase,
    private val fetchLoanApplicationListUseCase: FetchLoanApplicationListUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val captureAppOpensUseCase: CaptureAppOpensUseCase,
    private val fetchSmsIngestionUseCase: FetchSmsIngestionUseCase,
    private val fetchContactListUseCase: FetchContactListUseCase,
    private val fetchIfKycRequiredUseCase: FetchIfKycRequiredUseCase,
    private val fetchPublicStaticContentUseCase: FetchPublicStaticContentUseCase,
    private val prefs: PrefsApi,
    private val appScope: CoroutineScope,
    private val deviceUtils: DeviceUtils,
    private val networkUtil: NetworkUtil,
    private val fetchInAppStoriesUseCase: FetchInAppStoriesUseCase,
    private val analyticsApi: AnalyticsApi,
    private val fetchFeatureRedirectionUseCase: FetchFeatureRedirectionUseCase,
    private val fetchGoldTrendHomeScreenTabUseCase: FetchGoldTrendHomeScreenTabUseCase,
    private val upiAppsUtil: UpiAppsUtil,
    private val remoteConfigApi: RemoteConfigApi,
    loginRepository: LoginRepository,
    clearCachedHomeFeedUseCase: ClearCachedHomeFeedUseCase,
    @AppHttpClient httpClient: HttpClient
) : ViewModel() {

    companion object {
        const val INELIGIBLE_MANDATE_APPS = "INELIGIBLE_MANDATE_APPS"
    }

    init {
        viewModelScope.launch(Dispatchers.Default.limitedParallelism(1)) {
            NetworkEventBus.events.distinctUntilChanged().collectLatest {
                when (it.event) {
                    NetworkApiEvent.REFRESH_TOKEN_EXPIRED -> {
                        EventBus.getDefault()
                            .postSticky(LogoutEvent(flowContext = LogoutRefreshTokenExpired))
                    }

                    NetworkApiEvent.UNUSUAL_ACTIVITY_DETECTED -> {
                        EventBus.getDefault().post(UnusualActivityDetectedEvent())
                    }

                    NetworkApiEvent.API_EXCEPTION -> {
                        it.exception?.let {
                            Timber.e(it)
                        }
                    }
                }
            }
        }
        if (prefs.isOnboardingComplete().not())
            fetchEligibleMandateApps()
    }

    private val _downtimeDetails =
        MutableLiveData<RestClientResult<ApiResponseWrapper<DowntimeResponse?>>>()
    val downtimeDetails: LiveData<RestClientResult<ApiResponseWrapper<DowntimeResponse?>>>
        get() = _downtimeDetails

    private val _fetchCurrentGoldPriceLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>()
    val fetchCurrentGoldPriceLiveData: LiveData<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>
        get() = _fetchCurrentGoldPriceLiveData

    private val _initiateDetectedSpendPaymentLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val initiateDetectedSpendPaymentLiveData: LiveData<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _initiateDetectedSpendPaymentLiveData

    private val _surveyLiveData = MutableLiveData<RestClientResult<ApiResponseWrapper<Survey?>>>()
    val surveyLiveData: LiveData<RestClientResult<ApiResponseWrapper<Survey?>>>
        get() = _surveyLiveData

    private val _buyGoldLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val buyGoldLiveData: LiveData<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _buyGoldLiveData

    private val _userRatingLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<UserRatingData?>>>()
    val userRatingLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserRatingData?>>>
        get() = _userRatingLiveData

    private val _userMetaLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserMetaData?>>>()
    val userMetaLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserMetaData?>>>
        get() = _userMetaLiveData

    private val _updateUserDeviceDetailsLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val updateUserDeviceDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _updateUserDeviceDetailsLiveData

    private val _receivedGiftLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<List<GoldGiftReceivedResponse>>>>()
    val receivedGiftLiveData: LiveData<RestClientResult<ApiResponseWrapper<List<GoldGiftReceivedResponse>>>>
        get() = _receivedGiftLiveData

    private val _closeNavDrawer = MutableLiveData<Boolean>()
    val closeNavDrawer: LiveData<Boolean>
        get() = _closeNavDrawer

    private val _appUpdateAvailable = MutableLiveData<Boolean>()
    val appUpdateAvailable: LiveData<Boolean>
        get() = _appUpdateAvailable

    private val _appUpdate = MutableLiveData<AppUpdateInfo?>()
    val shareMessageDetails: LiveData<String?>
        get() = _shareMessageDetails

    private val _shareMessageDetails = MutableLiveData<String?>()
    val appUpdate: LiveData<AppUpdateInfo?>
        get() = _appUpdate

    private val _popupInfoLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<InfoDialogResponse>>>()
    val popupInfoLiveData: LiveData<RestClientResult<ApiResponseWrapper<InfoDialogResponse>>>
        get() = _popupInfoLiveData

    private val _userKycStatusLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<UserKycStatus?>>>()
    val userKycStatusLiveData: LiveData<RestClientResult<ApiResponseWrapper<UserKycStatus?>>>
        get() = _userKycStatusLiveData

    private val _userLendingKycProgressLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<KycProgressResponse?>>>()
    val userLendingKycProgressLiveData: LiveData<RestClientResult<ApiResponseWrapper<KycProgressResponse?>>>
        get() = _userLendingKycProgressLiveData

    private val _shouldShowForceUpdateDialog =
        MutableLiveData<RestClientResult<ApiResponseWrapper<ForceUpdateResponse>>>()
    val shouldShowForceUpdateDialog: LiveData<RestClientResult<ApiResponseWrapper<ForceUpdateResponse>>>
        get() = _shouldShowForceUpdateDialog

    private val _customisedOnboardingLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<DashboardStaticData?>>>()
    val customisedOnboardingLiveData: LiveData<RestClientResult<ApiResponseWrapper<DashboardStaticData?>>>
        get() = _customisedOnboardingLiveData

    private val _loanApplicationsLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<LendingFlowStatusResponse?>>>()
    val loanApplicationsLiveData: LiveData<RestClientResult<ApiResponseWrapper<LendingFlowStatusResponse?>>>
        get() = _loanApplicationsLiveData

    private val _isAutoPayResetRequiredLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredLiveData: LiveData<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredLiveData

    private val _contactResponseLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListResponse?>>>()
    val contactResponseLiveData: LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListResponse?>>>
        get() = _contactResponseLiveData

    private val _smsOnDemandLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<ShouldSendSmsOnDemand>>>()
    val smsOnDemandLiveData: LiveData<RestClientResult<ApiResponseWrapper<ShouldSendSmsOnDemand>>>
        get() = _smsOnDemandLiveData

    private val _inAppStoryFlow: MutableStateFlow<RestClientResult<ApiResponseWrapper<InAppStoryModel>>> =
        MutableStateFlow(RestClientResult.loading())
    val inAppStoryFlow: StateFlow<RestClientResult<ApiResponseWrapper<InAppStoryModel>>> =
        _inAppStoryFlow

    private val _featureRedirectionFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<FeatureRedirectionResp?>>>(
            RestClientResult.none()
        )
    val featureRedirectionFlow: CStateFlow<RestClientResult<ApiResponseWrapper<FeatureRedirectionResp?>>>
        get() = _featureRedirectionFlow.toCommonStateFlow()

    private val _goldTrendHomeScreenTabFlow = MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldTrendHomeScreenTab>>>(RestClientResult.none())
    val goldTrendHomeScreenTabFlow: CStateFlow<RestClientResult<ApiResponseWrapper<GoldTrendHomeScreenTab>>>
        get() = _goldTrendHomeScreenTabFlow.toCommonStateFlow()

    var isKycRequired = false

    var isReferralDeepLinkSynced = false

    var isKycDeeplinkHandlingPending = false

    var isLoanTaken = false

    var kycFlowType = BaseConstants.KycFlowType.DEFAULT

    private var popupInfoJob: Job? = null

    var isAppsFlyerIdSynced = false

    var isEligibleForMandateFlow = false

    //Not saving this in Prefs as this is a one time thing
    var numberOfDaysOfSms: Int = DEFAULT_VALUES_FOR_NO_OF_SMS_SYNC

    fun fetchAppVersionData() {
        viewModelScope.launch {
            fetchForceUpdateUseCase.fetchForceUpdateData().collect {
                _shouldShowForceUpdateDialog.postValue(it)
            }
        }
    }

    fun shouldSendSmsOnDemand() {
        viewModelScope.launch {
            fetchSmsIngestionUseCase.shouldSendSmsOnDemand().collect {
                _smsOnDemandLiveData.postValue(it)
            }
        }
    }

    /************ Manual Payment ************/
    fun initiateDetectedSpendPayment(
        initiateDetectedRoundOffsPaymentRequest: com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest,
        paymentGateway: OneTimePaymentGateway
    ) {
        viewModelScope.launch {
            initiateDetectedSpendPaymentUseCase.makeDetectedSpendsPayment(
                initiateDetectedRoundOffsPaymentRequest, paymentGateway
            ).collect {
                _initiateDetectedSpendPaymentLiveData.postValue(it)
            }
        }
    }

    /************ End Region ************/

    fun buyGold(amount: Float, paymentGateway: OneTimePaymentGateway, flowContext: String? = null) {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY)
                .collect(onLoading = {
                    _buyGoldLiveData.postValue(RestClientResult.loading())
                }, onSuccess = {
                    val buyGoldRequest = BuyGoldByAmountRequest(
                        amount = amount,
                        fetchCurrentGoldPriceResponse = it,
                        paymentGateway = paymentGateway,
                        flowContext = flowContext
                    )
                    buyGoldUseCase.buyGoldByAmount(buyGoldRequest).collect {
                        _buyGoldLiveData.postValue(it)
                    }
                }, onError = { errorMessage, errorCode ->
                    _buyGoldLiveData.postValue(RestClientResult.error(errorMessage))
                })
        }
    }

    /************ Fetch Autopay Progress ************/
    fun fetchGoldBuyPrice() {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY).collect {
                _fetchCurrentGoldPriceLiveData.postValue(it)
            }
        }
    }

    fun updateDailySaving(amount: Float) {
        viewModelScope.launch {
            updateDailyInvestmentStatusUseCase.updateDailyInvestmentStatus(amount)
                .collect(onSuccess = {
                    EventBus.getDefault().post(RefreshDailySavingEvent())
                })
        }
    }

    fun updateSession() {
        viewModelScope.launch {
            updateSessionUseCase.updateSession(BuildConfig.VERSION_CODE).collect(onSuccess = {

            })
        }
    }

    fun fetchGoldTrendTab() {
        viewModelScope.launch {
            fetchGoldTrendHomeScreenTabUseCase.fetchGoldTrendHomeScreenTab().collect {
                _goldTrendHomeScreenTabFlow.emit(it)
            }
        }
    }

    fun fetchLendingProgress() {
        viewModelScope.launch {
            fetchLoanApplicationListUseCase.fetchLoanApplicationList().collectUnwrapped(
                onSuccess = {
                    if (it.success) {
                        it.data?.getOrNull(0)?.let {
                            fetchLoanProgressStatusV2UseCase.getLoanProgressStatus(it.applicationId.orEmpty())
                                .collect {
                                    _loanApplicationsLiveData.postValue(it)
                                }
                        } ?: run {
                            _loanApplicationsLiveData.postValue(RestClientResult.error(message = ""))
                        }
                    } else {
                        _loanApplicationsLiveData.postValue(RestClientResult.error(message = it.errorMessage.orEmpty()))
                    }
                },
                onError = { message, errorCode ->
                    _loanApplicationsLiveData.postValue(RestClientResult.error(message = message))
                }
            )
        }
    }

    fun fetchUserKycStatus() {
        viewModelScope.launch {
            fetchUserKycStatusUseCase.fetchUserKycStatus().collectLatest {
                _userKycStatusLiveData.postValue(it)
            }
        }
    }

    fun fetchIfKycIsRequired() {
        viewModelScope.launch {
            fetchIfKycRequiredUseCase.fetchIfKycRequired().collectLatest {
                isKycRequired = it.data?.data?.kycRequired.orTrue()
            }
        }
    }

    fun sendReferralId(
        referrerUserId: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val deviceId = deviceUtils.getDeviceId()
            val advertisingId = deviceUtils.getAdvertisingId()
            val osName = deviceUtils.getOsName()
            val data = PostReferralAttributionData(
                deviceDetails = ReferralAttributionDeviceDetails(
                    deviceId = deviceId,
                    advertisingId = advertisingId,
                    osName = osName,
                ), referrerUserId = referrerUserId
            )
            postReferralAttributionDataUseCase.postReferralAttribution(data).collectLatest {
                if (!referrerUserId.isNullOrEmpty() && it.status == RestClientResult.Status.SUCCESS) {
                    isReferralDeepLinkSynced = true
                    prefs.setAppsFlyerReferralUserId("")
                }
            }
        }
    }

    fun fetchReferEarnMsgLinks(appsFlyerInviteLink: String) {
        viewModelScope.launch {
            fetchReferralsShareMessageUseCase.fetchReferralShareMessage(appsFlyerInviteLink)
                .collect { data ->
                    _shareMessageDetails.value = data.data?.data?.whatsAppShareMessage
                }
        }
    }

    fun updateDeviceDetails(
        referralLink: String? = null,
        appsFlyerId: String? = null,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val deviceId = deviceUtils.getDeviceId()
            val advertisingId = deviceUtils.getAdvertisingId()
            val osName = deviceUtils.getOsName()
            updateUserDeviceDetailsUseCase.updateUserDeviceDetails(
                UserDeviceDetails(
                    com.jar.app.feature_user_api.domain.model.DeviceDetails(
                        advertisingId = advertisingId,
                        deviceId = deviceId,
                        os = osName,
                        ipAddress = networkUtil.getLocalIpAddress(),
                        phoneModel = deviceUtils.getManufacturer() + " " + deviceUtils.getModel(),
                        marketingSource = null
                    ), referralLink, appsFlyerId
                )
            ).collect {
                _updateUserDeviceDetailsLiveData.postValue(it)
            }
        }
    }

    fun logout() {
        appScope.launch {
            logoutUseCase.logout(deviceId = null, prefs.getRefreshToken()).collect {

            }
        }
    }

    /**
     * If [forceShowSurvey] is true, show the survey even if it has been shown before
     * Currently used by survey deeplink
     */
    fun fetchSurvey(forceShowSurvey: Boolean = false) {
        if (forceShowSurvey) prefs.setLastSurveyConsumed("") //Flag used to check whether survey was show before or not

        viewModelScope.launch {
            _surveyLiveData.postValue(fetchUserSurveyUseCase.fetchUserSurvey())
        }
    }

    fun fetchDowntime() {
        viewModelScope.launch {
            fetchDowntimeUseCase.fetchDownTime().collect {
                _downtimeDetails.postValue(it)
            }
        }
    }

    var userGoldBalance: Float? = null

    fun fetchUserGoldBalance() {
        viewModelScope.launch {
            fetchUserGoldBalanceUseCase.fetchUserGoldBalance()
                .mapToDTO {
                    it?.toGoldBalance()
                }
                .collect(onSuccess = {
                    userGoldBalance = it?.volume.orZero()
                })
        }
    }

    fun fetchUserRating() {
        viewModelScope.launch {
            fetchUserRatingUseCase.getUserRating().collect {
                _userRatingLiveData.postValue(it)
            }
        }
    }

    fun fetchUserMetaData() {
        viewModelScope.launch {
            if (prefs.isLoggedIn().not()) return@launch
            fetchUserMetaDataUseCase.fetchUserMeta()
                .mapToDTO {
                    it?.toUserMetaData()
                }
                .collect {
                    _userMetaLiveData.postValue(it)
                }
        }
    }

    fun updateLocalNotificationMetaData(notificationMetaData: NotificationMetaData) {
        viewModelScope.launch {
            fetchNotificationMetaDataUseCase.updateNotificationMetaData(notificationMetaData)
        }
    }

    fun fetchReceivedGift() {
        viewModelScope.launch {
            fetchReceivedGiftsUseCase.fetchReceivedGift().collectLatest {
                _receivedGiftLiveData.postValue(it)
            }
        }
    }


    fun fetchStaticPopupInfo(contentType: String) {
        popupInfoJob?.cancel()
        popupInfoJob = viewModelScope.launch {
            fetchStaticPopupInfoUseCase.fetchStaticPopupInfo(contentType).collectLatest {
                _popupInfoLiveData.postValue(it)
            }
        }
    }

    fun updateAdSourceData(adSourceData: AdSourceData) {
        appScope.launch {
            updateAdSourceDataUseCase.updateAdSourceData(adSourceData).collectLatest {
                Timber.d(it.status.toString())
            }
        }
    }

    fun fetchUserLendingKycProgress() {
        viewModelScope.launch {
            fetchKycProgressUseCase.fetchKycProgress()
                .mapToDTO {
                    it?.toKycProgressResponse()
                }
                .collectLatest {
                    _userLendingKycProgressLiveData.postValue(it)
                }
        }
    }

    private fun fetchEligibleMandateApps() {
        viewModelScope.launch {
            val supportedMandateUpiAppsList = remoteConfigApi.getMandateSupportedUpiApps().split(",")
            // Use the intersect function to find common elements
            val commonElements = upiAppsUtil.getMandateReadyUpiAppsPackageName()
                .intersect(supportedMandateUpiAppsList.toSet())

            // Check if the result contains any common elements
            isEligibleForMandateFlow = commonElements.isNotEmpty()
        }
    }

    fun fetchCustomisedOnboardingFlow(phoneNumber: String) {
        viewModelScope.launch {
            if (prefs.hasShownCustomOnboarding().not()) {
                fetchPublicStaticContentUseCase.fetchPublicStaticContent(
                    StaticContentType.CUSTOM_ONBOARDING,
                    phoneNumber,
                    if (isEligibleForMandateFlow) null else INELIGIBLE_MANDATE_APPS
                )
                    .collectLatest {
                        _customisedOnboardingLiveData.postValue(it)
                    }
            }
        }
    }

    fun isAutoPayResetRequired(newAmount: Float) {
        viewModelScope.launch {
            isAutoInvestResetRequiredUseCase.isAutoInvestResetRequired(
                newAmount,
                SavingsType.DAILY_SAVINGS.name
            ).collect {
                _isAutoPayResetRequiredLiveData.postValue(it)
            }
        }
    }

    fun captureAppOpens() {
        viewModelScope.launch {
            captureAppOpensUseCase.captureAppOpens().collect {

            }
        }
    }

    fun fetchFeatureRedirectionData() {
        viewModelScope.launch {
            fetchFeatureRedirectionUseCase.fetchFeatureRedirectionData().collect {
                _featureRedirectionFlow.emit(it)
            }
        }
    }

    fun closeNavDrawer() {
        _closeNavDrawer.postValue(true)
    }

    private var appUpdateInfo: AppUpdateInfo? = null
    fun appUpdateAvailable(appUpdateInfo: AppUpdateInfo) {
        this.appUpdateInfo = appUpdateInfo
        _appUpdateAvailable.postValue(true)
    }

    fun updateApp() {
        _appUpdate.postValue(appUpdateInfo)
    }

    fun fetchContactList() {
        viewModelScope.launch {
            val data = fetchContactListUseCase.fetchContactList(
                INITIAL_PAGE,
                PAGE_SIZE,
                com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO,
                null
            )
            _contactResponseLiveData.postValue(data)
        }
    }

    fun fetchInAppStory() {
        viewModelScope.launch {
            fetchInAppStoriesUseCase.fetchInAppStories().collect {
                _inAppStoryFlow.value = it
            }
        }
    }

    fun postAnalyticsEvent(eventName: String, value: Map<String, Any>? = null) {
        value?.let {
            analyticsApi.postEvent(eventName, value)
        } ?: run {
            analyticsApi.postEvent(eventName)
        }
    }

    override fun onCleared() {
        popupInfoJob?.cancel()
        super.onCleared()
    }
}

private const val INITIAL_PAGE = 0
private const val PAGE_SIZE = 20

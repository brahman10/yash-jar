package com.jar.app.feature_homepage.shared.ui

import com.jar.app.core_base.domain.mapper.toGoldBalance
import com.jar.app.core_base.domain.mapper.toKycProgressResponse
import com.jar.app.core_base.domain.model.GoldBalance
import com.jar.app.core_base.domain.model.KycProgressResponse
import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.Label
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.TextList
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.DynamicCardUtil
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.toJsonElement
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByAmountRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_coupon_api.domain.model.CouponCodeResponse
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchPendingInvitesUseCase
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentStatus
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentStatusUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchGoldSavingUseCase
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchIsSavingPausedUseCase
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_homepage.shared.domain.model.AlertCardData
import com.jar.app.feature_homepage.shared.domain.model.AppWalkthroughResp
import com.jar.app.feature_homepage.shared.domain.model.AppWalkthroughSection
import com.jar.app.feature_homepage.shared.domain.model.FirstCoinHomeScreenData
import com.jar.app.feature_homepage.shared.domain.model.HeaderSection
import com.jar.app.feature_homepage.shared.domain.model.HelpVideosData
import com.jar.app.feature_homepage.shared.domain.model.HelpVideosResponse
import com.jar.app.feature_homepage.shared.domain.model.HomeScreenPrompt
import com.jar.app.feature_homepage.shared.domain.model.ImageCardCarouselData
import com.jar.app.feature_homepage.shared.domain.model.ImageCardData
import com.jar.app.feature_homepage.shared.domain.model.InAppReviewStatus
import com.jar.app.feature_homepage.shared.domain.model.JarDuoData
import com.jar.app.feature_homepage.shared.domain.model.KeyValueData
import com.jar.app.feature_homepage.shared.domain.model.LendingProgressCardData
import com.jar.app.feature_homepage.shared.domain.model.LoanCardData
import com.jar.app.feature_homepage.shared.domain.model.PreNotificationDismissalType
import com.jar.app.feature_homepage.shared.domain.model.PreNotifyAutopay
import com.jar.app.feature_homepage.shared.domain.model.PreNotifyAutopayCardData
import com.jar.app.feature_homepage.shared.domain.model.QuickActionResponse
import com.jar.app.feature_homepage.shared.domain.model.RecommendedHomeCardData
import com.jar.app.feature_homepage.shared.domain.model.SectionType
import com.jar.app.feature_homepage.shared.domain.model.VasooliCardData
import com.jar.app.feature_homepage.shared.domain.model.WeeklyMagicHomecardData
import com.jar.app.feature_homepage.shared.domain.model.current_investment.CurrentGoldInvestmentCardData
import com.jar.app.feature_homepage.shared.domain.model.detected_spends.DetectedSpendData
import com.jar.app.feature_homepage.shared.domain.model.gold_sip.GoldSipCard
import com.jar.app.feature_homepage.shared.domain.model.gold_sip.GoldSipData
import com.jar.app.feature_homepage.shared.domain.model.partner_banner.BannerList
import com.jar.app.feature_homepage.shared.domain.model.partner_banner.BannersData
import com.jar.app.feature_homepage.shared.domain.model.payment_prompt.PaymentPromptData
import com.jar.app.feature_homepage.shared.domain.model.single_home_feed.SingleHomeFeedCardMetaData
import com.jar.app.feature_homepage.shared.domain.model.single_home_feed.SinglePageHomeFeedData
import com.jar.app.feature_homepage.shared.domain.model.update_daily_saving.UpdateDailySavingCardData
import com.jar.app.feature_homepage.shared.domain.model.update_daily_saving.UpdateDailySavingInfo
import com.jar.app.feature_homepage.shared.domain.model.viba.VibaHorizontalCard
import com.jar.app.feature_homepage.shared.domain.model.viba.VibaHorizontalCardData
import com.jar.app.feature_homepage.shared.domain.use_case.ClaimBonusUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.DismissUpcomingPreNotificationUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchAppWalkthroughUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchBottomNavStickyCardDataUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchDailySavingsCardUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFeatureViewUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinHomeScreenDataUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHelpVideosUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeFeedActionsUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeFeedImagesUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomePageExperimentsUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeScreenBottomSheetPromptUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchInAppReviewStatusUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchPartnerBannerUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchQuickActionsUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUpcomingPreNotificationUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUpdateDailySavingAmountInfoUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchVibaCardUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.UpdateAppWalkthroughCompletedUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.UpdateLockerViewShownUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.UpdateUserInteractionUseCase
import com.jar.app.feature_homepage.shared.util.EventKey
import com.jar.app.feature_homepage.shared.util.HomeConstants
import com.jar.app.feature_jar_duo.shared.domain.model.DuoContactsMetaData
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchGroupListUseCase
import com.jar.app.feature_lending_kyc.shared.api.use_case.FetchKycProgressUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments_common.shared.PostPaymentReward
import com.jar.app.feature_one_time_payments_common.shared.PostPaymentRewardCard
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_spin.shared.domain.model.SpinsMetaData
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinsMetaDataUseCase
import com.jar.app.feature_user_api.domain.mappers.toDetectedSpends
import com.jar.app.feature_user_api.domain.mappers.toUserGoldSipDetails
import com.jar.app.feature_user_api.domain.mappers.toUserMetaData
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.app.feature_user_api.domain.model.DetectedSpendsData
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import com.jar.app.feature_user_api.domain.model.UserMetaData
import com.jar.app.feature_user_api.domain.use_case.FetchDetectedSpendInfoUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserMetaUseCase
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class HomeFragmentViewModel constructor(
    private val groupsUseCase: FetchGroupListUseCase,
    private val claimBonusUseCase: ClaimBonusUseCase,
    private val fetchUserMetaUseCase: FetchUserMetaUseCase,
    private val fetchGoldSavingUseCase: FetchGoldSavingUseCase,
    private val fetchCouponCodeUseCase: FetchCouponCodeUseCase,
    private val fetchHelpVideosUseCase: FetchHelpVideosUseCase,
    private val fetchFeatureViewUseCase: FetchFeatureViewUseCase,
    private val fetchKycProgressUseCase: FetchKycProgressUseCase,
    private val fetchContactListUseCase: FetchContactListUseCase,
    private val fetchQuickActionsUseCase: FetchQuickActionsUseCase,
    private val fetchSpinsMetaDataUseCase: FetchSpinsMetaDataUseCase,
    private val fetchPartnerBannerUseCase: FetchPartnerBannerUseCase,
    private val fetchVibaHorizontalCardUseCase: FetchVibaCardUseCase,
    private val fetchHomeFeedImagesUseCase: FetchHomeFeedImagesUseCase,
    private val fetchPendingInvitesUseCase: FetchPendingInvitesUseCase,
    private val fetchGoldSipDetailsUseCase: FetchGoldSipDetailsUseCase,
    private val fetchIsSavingPausedUseCase: FetchIsSavingPausedUseCase,
    private val fetchUserGoldBalanceUseCase: FetchUserGoldBalanceUseCase,
    private val fetchHomeFeedActionsUseCase: FetchHomeFeedActionsUseCase,
    private val fetchDailySavingsCardUseCase: FetchDailySavingsCardUseCase,
    private val updateUserInteractionUseCase: UpdateUserInteractionUseCase,
    private val updateLockerViewShownUseCase: UpdateLockerViewShownUseCase,
    private val fetchInAppReviewStatusUseCase: FetchInAppReviewStatusUseCase,
    private val fetchDetectedSpendInfoUseCase: FetchDetectedSpendInfoUseCase,
    private val fetchHomePageExperimentsUseCase: FetchHomePageExperimentsUseCase,
    private val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase,
    private val fetchDailyInvestmentStatusUseCase: FetchDailyInvestmentStatusUseCase,
    private val fetchUpcomingPreNotificationUseCase: FetchUpcomingPreNotificationUseCase,
    private val fetchFirstCoinHomeScreenDataUseCase: FetchFirstCoinHomeScreenDataUseCase,
    private val fetchWeeklyChallengeMetaDataUseCase: FetchWeeklyChallengeMetaDataUseCase,
    private val dismissUpcomingPreNotificationUseCase: DismissUpcomingPreNotificationUseCase,
    private val fetchUpdateDailySavingAmountInfoUseCase: FetchUpdateDailySavingAmountInfoUseCase,
    private val fetchHomeScreenBottomSheetPromptUseCase: FetchHomeScreenBottomSheetPromptUseCase,
    private val fetchAppWalkthroughUseCase: FetchAppWalkthroughUseCase,
    private val updateAppWalkthroughCompletedUseCase: UpdateAppWalkthroughCompletedUseCase,
    private val fetchBottomNavStickyCardDataUseCase: FetchBottomNavStickyCardDataUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val buyGoldUseCase: BuyGoldUseCase,
    private val prefs: PrefsApi,
    private val remoteConfigApi: RemoteConfigApi,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private var job: Job? = null

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    var isManualPaymentLoading = false

    var isAutopayInfoShown = false

    companion object {
        private const val INITIAL_PAGE = 0
        private const val PAGE_SIZE = 20
    }

    private val _buyGoldFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val buyGoldFlow: CFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _buyGoldFlow.toCommonFlow()

    private val _homeScreenPromptLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<HomeScreenPrompt?>>>()
    val homeScreenPromptLiveData: CFlow<RestClientResult<ApiResponseWrapper<HomeScreenPrompt?>>>
        get() = _homeScreenPromptLiveData.toCommonFlow()

    private val _goldBalanceLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldBalance?>>>(RestClientResult.none())
    val goldBalanceLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<GoldBalance?>>>
        get() = _goldBalanceLiveData.toCommonStateFlow()

    private val _dailySavingStatusLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus>>>(
            RestClientResult.none()
        )
    val dailyInvestmentStatusLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus>>>
        get() = _dailySavingStatusLiveData.toCommonStateFlow()

    private val _giftingUserSettingsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val giftingUserSettingsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _giftingUserSettingsLiveData.toCommonStateFlow()

    private val _lendingHomeCardLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val lendingHomeCardLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _lendingHomeCardLiveData.toCommonStateFlow()

    private val _healthInsuranceSingleCardLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val healthInsuranceSingleCardLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _healthInsuranceSingleCardLiveData.toCommonStateFlow()

    private val _goldLeaseHomeCardLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val goldLeaseHomeCardLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _goldLeaseHomeCardLiveData.toCommonStateFlow()

    private val _spinsMetaDataLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<SpinsMetaData>>>(RestClientResult.none())
    val spinsMetaDataLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<SpinsMetaData>>>
        get() = _spinsMetaDataLiveData.toCommonStateFlow()

    private val _goldDeliveryUserSettingsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val goldDeliveryUserSettingsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _goldDeliveryUserSettingsLiveData.toCommonStateFlow()

    private val _spendTrackerHomeCardLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val spendTrackerHomeCardLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _spendTrackerHomeCardLiveData.toCommonStateFlow()

    private val _dailSavingGoalHomeCardLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val dailSavingGoalHomeCardLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _dailSavingGoalHomeCardLiveData.toCommonStateFlow()

    private val _healthInsuranceHomeCardLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val healthInsuranceHomeCardLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _healthInsuranceHomeCardLiveData.toCommonStateFlow()

    private val _promoCodeUserSettingsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val promoCodeUserSettingsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _promoCodeUserSettingsLiveData.toCommonStateFlow()

    private val _buyGoldUserSettingsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val buyGoldUserSettingsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _buyGoldUserSettingsLiveData.toCommonStateFlow()

    private val _roundOffCardLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val roundOffCardLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _roundOffCardLiveData.toCommonStateFlow()

    private val _duoCardLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val duoCardLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _duoCardLiveData.toCommonStateFlow()

    private val _vasooliCardLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val vasooliCardLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _vasooliCardLiveData.toCommonStateFlow()

    private val _homePageExperimentsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val homePageExperimentsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _homePageExperimentsLiveData.toCommonStateFlow()

    private val _detectedSpendInfoLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DetectedSpendsData?>>>(RestClientResult.none())
    val detectedSpendInfoLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<DetectedSpendsData?>>>
        get() = _detectedSpendInfoLiveData.toCommonStateFlow()

    private val _autoInvestPauseLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>(RestClientResult.none())
    val autoInvestPauseLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _autoInvestPauseLiveData.toCommonStateFlow()

    private val _dailyInvestPauseLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>(RestClientResult.none())
    val dailyInvestPauseLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _dailyInvestPauseLiveData.toCommonStateFlow()

    private val _roundOffPauseLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>(RestClientResult.none())
    val roundOffPauseLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _roundOffPauseLiveData.toCommonStateFlow()

    private val _userMetaLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserMetaData?>>>(RestClientResult.none())
    val userMetaLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<UserMetaData?>>>
        get() = _userMetaLiveData.toCommonStateFlow()

    private val _partnerBannerLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<BannerList>>>(RestClientResult.none())
    val partnerBannerLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<BannerList>>>
        get() = _partnerBannerLiveData.toCommonStateFlow()

    private val _listLiveData = MutableSharedFlow<List<DynamicCard>>()
    val listLiveData: CFlow<List<DynamicCard>>
        get() = _listLiveData.toCommonFlow()

    private val _claimedBonusLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val claimedBonusLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _claimedBonusLiveData.toCommonStateFlow()

    private val _hasVideoLiveData = MutableStateFlow<Boolean>(false)
    val hasVideoLiveData: CStateFlow<Boolean>
        get() = _hasVideoLiveData.toCommonStateFlow()

    private val _userLendingKycProgressLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<KycProgressResponse?>>>(
            RestClientResult.none()
        )
    val userLendingKycProgressLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<KycProgressResponse?>>>
        get() = _userLendingKycProgressLiveData.toCommonStateFlow()

    private val _weeklyChallengeMetaDataLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeMetaData?>>>(
            RestClientResult.none()
        )
    val weeklyChallengeMetaDataLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeMetaData?>>>
        get() = _weeklyChallengeMetaDataLiveData.toCommonStateFlow()

    private val _fetchUpdateDailySavingAmountLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UpdateDailySavingInfo>>>(
            RestClientResult.none()
        )
    val fetchUpdateDailySavingAmountLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<UpdateDailySavingInfo>>>
        get() = _fetchUpdateDailySavingAmountLiveData.toCommonStateFlow()

    private val _userGoldSipDetailsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails?>>>(RestClientResult.none())
    val userGoldSipDetailsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails?>>>
        get() = _userGoldSipDetailsLiveData.toCommonStateFlow()

    private val _couponCodesLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<CouponCodeResponse?>>>(RestClientResult.none())
    val couponCodesLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<CouponCodeResponse?>>>
        get() = _couponCodesLiveData.toCommonStateFlow()

    private val _vibaHorizontalCardFlowData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<List<VibaHorizontalCardData>>>>(
            RestClientResult.none()
        )
    val vibaHorizontalCardFlowData: CStateFlow<RestClientResult<ApiResponseWrapper<List<VibaHorizontalCardData>>>>
        get() = _vibaHorizontalCardFlowData.toCommonStateFlow()

    private val _helpVideosLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<HelpVideosResponse>>>(RestClientResult.none())
    val helpVideosLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<HelpVideosResponse>>>
        get() = _helpVideosLiveData.toCommonStateFlow()

    private val _fetchPreNotificationLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PreNotifyAutopay>>>(RestClientResult.none())
    val fetchPreNotificationLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<PreNotifyAutopay>>>
        get() = _fetchPreNotificationLiveData.toCommonStateFlow()

    private val _dismissPreNotificationLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PreNotifyAutopay>>>(RestClientResult.none())
    val dismissPreNotificationLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<PreNotifyAutopay>>>
        get() = _dismissPreNotificationLiveData.toCommonStateFlow()

    private val _fetchQuickActionButtonsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<QuickActionResponse?>>>(
            RestClientResult.none()
        )
    val fetchQuickActionButtonsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<QuickActionResponse?>>>
        get() = _fetchQuickActionButtonsLiveData.toCommonStateFlow()

    private val _autoInvestedGoldData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PostPaymentReward?>>>(RestClientResult.none())
    val autoInvestedGoldData: CStateFlow<RestClientResult<ApiResponseWrapper<PostPaymentReward?>>>
        get() = _autoInvestedGoldData.toCommonStateFlow()

    private val _fetchFirstCoinHomeScreenDataLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<FirstCoinHomeScreenData?>>>(
            RestClientResult.none()
        )
    val fetchFirstCoinHomeScreenDataLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<FirstCoinHomeScreenData?>>>
        get() = _fetchFirstCoinHomeScreenDataLiveData.toCommonStateFlow()

    private val _fetchHomeFeedActionsUseCaseLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val fetchHomeFeedActionsUseCaseLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _fetchHomeFeedActionsUseCaseLiveData.toCommonStateFlow()

    private val _duoContactMetaDataLiveData = MutableStateFlow<DuoContactsMetaData?>(null)
    val duoContactMetaDataLiveData: CStateFlow<DuoContactsMetaData?>
        get() = _duoContactMetaDataLiveData.toCommonStateFlow()

    private val _fetchDailySavingsCardLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_homepage.shared.domain.model.DailySavingsV2CardResponse?>>>(
            RestClientResult.none()
        )
    val fetchDailySavingsCardLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_homepage.shared.domain.model.DailySavingsV2CardResponse?>>>
        get() = _fetchDailySavingsCardLiveData.toCommonStateFlow()

    private val _homeFeedImageLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<List<KeyValueData>>>>(RestClientResult.none())
    val homeFeedImageLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<List<KeyValueData>>>>
        get() = _homeFeedImageLiveData.toCommonStateFlow()

    private val _fetchInAppReviewStatusLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<InAppReviewStatus>>>(RestClientResult.none())
    val fetchInAppReviewStatusLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<InAppReviewStatus>>>
        get() = _fetchInAppReviewStatusLiveData.toCommonStateFlow()

    private val _updateUserInteractionLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val updateUserInteractionLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _updateUserInteractionLiveData.toCommonStateFlow()

    private val _fetchAppWalkThroughFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<AppWalkthroughResp?>>>()
    val fetchAppWalkThroughFlow: CFlow<RestClientResult<ApiResponseWrapper<AppWalkthroughResp?>>>
        get() = _fetchAppWalkThroughFlow.toCommonFlow()

    private val _updateAppWalkThroughCompletedFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val updateAppWalkThroughCompletedFlow: CFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _updateAppWalkThroughCompletedFlow.toCommonFlow()

    private val _isAutoPayResetRequiredFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>()
    val isAutoPayResetRequiredFlow: CFlow<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
        get() = _isAutoPayResetRequiredFlow.toCommonFlow()

    var newDsAmount = 0f

    private val _updateDailySavingCardFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val updateDailySavingCardFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _updateDailySavingCardFlow.toCommonStateFlow()

    private val _bottomNavStickyCardDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<LibraryCardData?>>>(RestClientResult.none())

    val bottomNavStickyCardDataFlow: CStateFlow<RestClientResult<ApiResponseWrapper<LibraryCardData?>>>
        get() = _bottomNavStickyCardDataFlow.toCommonStateFlow()

    var shouldScrollToTop = true
    var shouldAllowToSkipAppWalkthrough = true

    var appWalkthroughSectionList = ArrayList<AppWalkthroughSection>()

    var shouldShowAllCards = true

    var homeFragmentLaunchCount = 0

    fun buyGold(
        amount: Float,
        paymentGateway: OneTimePaymentGateway
    ) {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY)
                .collect(onLoading = {
                    _buyGoldFlow.emit(RestClientResult.loading())
                }, onSuccess = {
                    val buyGoldRequest = BuyGoldByAmountRequest(
                        amount = amount,
                        fetchCurrentGoldPriceResponse = it,
                        paymentGateway = paymentGateway,
                        flowContext = BaseConstants.BuyGoldFlowContext.INVEST_PROMPT_HOMEFEED
                    )
                    buyGoldUseCase.buyGoldByAmount(buyGoldRequest).collect {
                        _buyGoldFlow.emit(it)
                    }
                }, onError = { errorMessage, errorCode ->
                    _buyGoldFlow.emit(RestClientResult.error(errorMessage))
                })
        }
    }

    fun fetchAppWalkThroughData() {
        viewModelScope.launch {
            fetchAppWalkthroughUseCase.fetchAppWalkthrough().collect {
                _fetchAppWalkThroughFlow.emit(it)
                it.data?.data?.appWalkthroughSections?.let {
                    appWalkthroughSectionList.addAll(it)
                }
                shouldAllowToSkipAppWalkthrough = it.data?.data?.allowSkip.orFalse()
            }
        }
    }

    fun fetchBottomStickyCardData() {
        viewModelScope.launch {
            fetchBottomNavStickyCardDataUseCase.fetchBottomNavStickyCardData().collectLatest {
                _bottomNavStickyCardDataFlow.emit(it)
            }
        }
    }

    fun updateAppWalkThroughCompleted() {
        viewModelScope.launch {
            updateAppWalkthroughCompletedUseCase.updateAppWalkthroughCompleted().collect {
                _updateAppWalkThroughCompletedFlow.emit(it)
            }
        }
    }


    fun fetchHomePageData(isRefreshed: Boolean = false) {
        fetchQuickActionButtons()
        fetchUserGoldBalance()
        fetchWeeklyChallengeMetaData()
        if (remoteConfigApi.shouldShowPreNotificationCard()) fetchUpcomingPreNotification()
        fetchWeeklyChallengeMetaData()
        fetchLendingHomeCard()
        fetchHeadingInsuranceSingleCard()
        fetchHomeFeedActions()
        fetchDailySavingsCardData()
        fetchGoldLeaseHomeCard()
        fetchPartnerBanners()
        fetchDetectedSpendsPaymentInfo()
        fetchPromoCodeUserSettings()
        fetchBuyGoldUserSettings()
        fetchRoundOffCard()
        fetchDuoCard()
        fetchVasooliCard()
        fetchGoldDeliveryUserSettings()
        fetchGiftingUserSettings()
        fetchSpinsMetaData()
        fetchHomePageExperiments()
        fetchAutoInvestUserSettings()
        fetchRoundOffUserSettings()
        fetchDailyInvestUserSettings()
        fetchUserMeta()
        fetchDuoData()
        fetchUserLendingKycProgress()
        fetchUpdateDailySavingAmountInfo()
        fetchUserGoldSipDetails()
        fetchCouponCodes()
        fetchVibaHorizontalCards()
        fetchHelpVideos()
        fetchFirstCoinGamificationData()
        fetchHomeFeedImages()
        fetchInAppReviewStatus()
        fetchAutoInvestedGoldData()
        fetchSpentTrackerHomeCard()
        fetchDailySavingStatus()
        fetchHealthInsuranceHomeCard()
        fetchDailySavingGoalHomeCard()
        fetchDailySavingUpdateJourney()
        fetchHomeScreenBottomSheetPrompt()
        if (!isRefreshed) fetchBottomStickyCardData()
    }

    fun isAutoPayResetRequired(newAmount: Float) {
        viewModelScope.launch {
            newDsAmount = newAmount
            isAutoInvestResetRequiredUseCase.isAutoInvestResetRequired(
                newAmount,
                SavingsType.DAILY_SAVINGS.name
            ).collect {
                _isAutoPayResetRequiredFlow.emit(it)
            }
        }
    }

    fun fetchHomeScreenBottomSheetPrompt() {
        viewModelScope.launch {
            fetchHomeScreenBottomSheetPromptUseCase.fetchHomeScreenBottomSheetPrompt().collect {
                _homeScreenPromptLiveData.emit(it)
            }
        }
    }

    fun fetchSinglePageHomeFeed() {

    }

    fun fetchInAppReviewStatus() {
        viewModelScope.launch {
            fetchInAppReviewStatusUseCase.fetchInAppReviewStatus().collect {
                _fetchInAppReviewStatusLiveData.emit(it)
            }
        }
    }

    fun fetchDailySavingsCardData() {
        viewModelScope.launch {
            fetchDailySavingsCardUseCase.fetchDSCardData().collect {
                _fetchDailySavingsCardLiveData.emit(it)
            }
        }
    }

    fun fetchFirstCoinGamificationData() {
        viewModelScope.launch {
            fetchFirstCoinHomeScreenDataUseCase.fetchFirstCoinHomeScreenData().collect {
                _fetchFirstCoinHomeScreenDataLiveData.emit(it)
            }
        }
    }

    fun fetchHomeFeedActions() {
        viewModelScope.launch {
            fetchHomeFeedActionsUseCase.fetchHomeFeedActions().collect {
                _fetchHomeFeedActionsUseCaseLiveData.emit(it)
            }
        }
    }

    private fun fetchAutoInvestedGoldData() {
        viewModelScope.launch {
            fetchGoldSavingUseCase.fetchDailyInvestedGoldSaving().collect {
                _autoInvestedGoldData.emit(it)
            }
        }
    }

    fun fetchDuoData() {
        viewModelScope.launch {
            fetchContactListUseCase.fetchContactListFlow(
                INITIAL_PAGE,
                PAGE_SIZE,
                ContactListFeatureType.DUO,
                searchText = null
            )
                .zip(fetchPendingInvitesUseCase.fetchPendingInvites(ContactListFeatureType.DUO)) { f1, f2 ->
                    Pair(
                        f1,
                        f2
                    )
                }.zip(groupsUseCase.fetchGroupList()) { f1, f2 -> Pair(f1, f2) }.collectLatest {
                    val pendingInvites = it.first.second
                    val contactList = it.first.first
                    val groupList = it.second
                    if (contactList.status == RestClientResult.Status.SUCCESS && pendingInvites.status == RestClientResult.Status.SUCCESS) {
                        val totalContactsToBeInvited = (contactList.data?.data?.totalContacts
                            ?: 0) - (contactList.data?.data?.totalContactsOnJar ?: 0)

                        _duoContactMetaDataLiveData.emit(
                            DuoContactsMetaData(
                                totalContactsToBeInvited = totalContactsToBeInvited,
                                totalPendingInvites = pendingInvites.data?.data?.list,
                                totalGroupCount = groupList.data?.data ?: emptyList(),
                                userName = prefs.getUserName()
                            )
                        )
                    }

                }
        }

    }

    fun fetchUserGoldBalance() {
        viewModelScope.launch {
            fetchUserGoldBalanceUseCase.fetchUserGoldBalance(includeView = true)
                .mapToDTO {
                    it?.toGoldBalance()
                }
                .collectLatest {
                    _goldBalanceLiveData.emit(it)
                }
        }
    }

    fun fetchDetectedSpendsPaymentInfo() {
        viewModelScope.launch {
            fetchDetectedSpendInfoUseCase.fetchDetectedSpendInfo(includeView = true)
                .mapToDTO {
                    it?.toDetectedSpends()
                }
                .collectUnwrapped(
                    onLoading = {
                        isManualPaymentLoading = true
                        _detectedSpendInfoLiveData.emit(RestClientResult.loading())
                    },
                    onSuccess = {
                        isManualPaymentLoading = false
                        _detectedSpendInfoLiveData.emit(RestClientResult.success(it))
                    },
                    onError = { errorMessage, _ ->
                        isManualPaymentLoading = false
                        _detectedSpendInfoLiveData.emit(RestClientResult.error(errorMessage))
                    }
                )
        }
    }

    fun fetchDailySavingStatus() {
        viewModelScope.launch {
            fetchDailyInvestmentStatusUseCase.fetchDailyInvestmentStatus(includeView = true)
                .collectLatest {
                    _dailySavingStatusLiveData.emit(it)
                }
        }
    }

    fun fetchGiftingUserSettings() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.GIFT_GOLD)
                .collectLatest {
                    _giftingUserSettingsLiveData.emit(it)
                }
        }
    }

    fun fetchSpentTrackerHomeCard() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.SPENDS_TRACKER)
                .collectLatest {
                    _spendTrackerHomeCardLiveData.emit(it)
                }
        }
    }

    fun fetchHealthInsuranceHomeCard() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.HEALTH_INSURANCE)
                .collectLatest {
                    _healthInsuranceHomeCardLiveData.emit(it)
                }
        }
    }

    fun fetchDailySavingGoalHomeCard() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.DAILY_SAVINGS_V3)
                .collectLatest {
                    _dailSavingGoalHomeCardLiveData.emit(it)
                }
        }
    }

    fun fetchLendingHomeCard() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.READY_CASH)
                .collectLatest {
                    _lendingHomeCardLiveData.emit(it)
                }
        }
    }

    fun fetchHeadingInsuranceSingleCard() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.HEALTH_INSURANCE_SINGLE_CARD_HOMEFEED)
                .collectLatest {
                    _healthInsuranceSingleCardLiveData.emit(it)
                }
        }
    }

    fun fetchGoldLeaseHomeCard() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.GOLD_LEASE_V2)
                .collectLatest {
                    _goldLeaseHomeCardLiveData.emit(it)
                }
        }
    }

    fun fetchGoldDeliveryUserSettings() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.GOLD_DELIVERY)
                .collectLatest {
                    _goldDeliveryUserSettingsLiveData.emit(it)
                }
        }
    }

    fun fetchPromoCodeUserSettings() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.PROMO_CODE)
                .collectLatest {
                    _promoCodeUserSettingsLiveData.emit(it)
                }
        }
    }

    fun fetchBuyGoldUserSettings() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.BUY_GOLD)
                .collectLatest {
                    _buyGoldUserSettingsLiveData.emit(it)
                }
        }
    }

    fun fetchRoundOffCard() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.ROUNDOFF)
                .collectLatest {
                    _roundOffCardLiveData.emit(it)
                }
        }
    }

    fun fetchDuoCard() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.DUO)
                .collectLatest {
                    _duoCardLiveData.emit(it)
                }
        }
    }

    fun fetchVasooliCard() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.VASOOLI)
                .collectLatest {
                    _vasooliCardLiveData.emit(it)
                }
        }
    }

    fun fetchSpinsMetaData() {
        viewModelScope.launch {
            fetchSpinsMetaDataUseCase.fetchSpinsMetaData(includeView = true).collectLatest {
                _spinsMetaDataLiveData.emit(it)
            }
        }
    }

    fun fetchPartnerBanners() {
        viewModelScope.launch {
            fetchPartnerBannerUseCase.fetchPartnerBanners(includeView = true).collectLatest {
                _partnerBannerLiveData.emit(it)
            }
        }
    }

    fun fetchHomePageExperiments() {
        viewModelScope.launch {
            fetchHomePageExperimentsUseCase.fetchHomePageExperiments().collectLatest {
                _homePageExperimentsLiveData.emit(it)
            }
        }
    }

    fun fetchCouponCodes() {
        viewModelScope.launch {
            fetchCouponCodeUseCase.fetchCouponCodes(context = null, includeView = true)
                .collect {
                    _couponCodesLiveData.emit(it)
                }
        }
    }

    fun fetchVibaHorizontalCards() {
        viewModelScope.launch {
            fetchVibaHorizontalCardUseCase.fetchVibaCardDetails().collect {
                _vibaHorizontalCardFlowData.emit(it)
            }
        }
    }

    fun fetchAutoInvestUserSettings() {
        viewModelScope.launch {
            fetchIsSavingPausedUseCase.fetchIsSavingPaused(
                SavingsType.AUTO_INVEST, includeView = true
            ).collectLatest {
                _autoInvestPauseLiveData.emit(it)
            }
        }
    }

    fun fetchDailyInvestUserSettings() {
        viewModelScope.launch {
            fetchIsSavingPausedUseCase.fetchIsSavingPaused(
                SavingsType.DAILY_SAVINGS, includeView = true
            ).collectLatest {
                _dailyInvestPauseLiveData.emit(it)
            }
        }
    }

    fun fetchRoundOffUserSettings() {
        viewModelScope.launch {
            fetchIsSavingPausedUseCase.fetchIsSavingPaused(
                SavingsType.ROUND_OFFS, includeView = true
            ).collectLatest {
                _roundOffPauseLiveData.emit(it)
            }
        }
    }

    fun fetchUserMeta() {
        viewModelScope.launch {
            fetchUserMetaUseCase.fetchUserMeta()
                .mapToDTO {
                    it?.toUserMetaData()
                }
                .collect {
                    _userMetaLiveData.emit(it)
                }
        }
    }

    fun fetchHelpVideos(language: String = "en") {
        viewModelScope.launch {
            fetchHelpVideosUseCase.fetchHelpVideos(language, includeView = true).collect {
                _helpVideosLiveData.emit(it)
            }
        }
    }

    fun fetchUpcomingPreNotification() {
        viewModelScope.launch {
            fetchUpcomingPreNotificationUseCase.fetchUpcomingPreNotification(includeView = true)
                .collect {
                    _fetchPreNotificationLiveData.emit(it)
                }
        }
    }

    fun dismissUpcomingPreNotification(
        dismissalType: PreNotificationDismissalType,
        preNotificationIds: List<String>
    ) {
        viewModelScope.launch {
            dismissUpcomingPreNotificationUseCase.dismissUpcomingPreNotification(
                dismissalType.name, preNotificationIds
            ).collect {
                _dismissPreNotificationLiveData.emit(it)
            }
        }
    }

    fun fetchQuickActionButtons() {
        viewModelScope.launch {
            fetchQuickActionsUseCase.fetchQuickActions(HomeConstants.QuickActionType.LOCKER.name)
                .collect {
                    _fetchQuickActionButtonsLiveData.emit(it)
                }
        }
    }

    fun updateUserInteraction(order: Int, featureType: String) {
        viewModelScope.launch {
            updateUserInteractionUseCase.updateUserInteraction(order, featureType)
                .collect {
                    _updateUserInteractionLiveData.emit(it)
                }
        }
    }

    fun fetchUserLendingKycProgress() {
        viewModelScope.launch {
            fetchKycProgressUseCase.fetchKycProgress()
                .mapToDTO {
                    it?.toKycProgressResponse()
                }
                .collect {
                    _userLendingKycProgressLiveData.emit(it)
                }
        }
    }

    fun fetchUserGoldSipDetails() {
        viewModelScope.launch {
            fetchGoldSipDetailsUseCase.fetchGoldSipDetails(includeView = true)
                .mapToDTO {
                    it?.toUserGoldSipDetails()
                }
                .collect {
                    _userGoldSipDetailsLiveData.emit(it)
                }
        }
    }

    fun fetchWeeklyChallengeMetaData() {
        viewModelScope.launch {
            fetchWeeklyChallengeMetaDataUseCase.fetchWeeklyChallengeMetaData(true).collect {
                _weeklyChallengeMetaDataLiveData.emit(it)
            }
        }
    }

    fun updateLockerViewShown() {
        viewModelScope.launch {
            updateLockerViewShownUseCase.updateLockerViewShown().collectUnwrapped(
                onSuccess = {
                    fetchUserGoldBalance()
                },
                onSuccessWithNullData = {
                    fetchUserGoldBalance()
                }
            )
        }
    }

    fun fetchUpdateDailySavingAmountInfo() {
        viewModelScope.launch {
            fetchUpdateDailySavingAmountInfoUseCase.fetchUpdateDailySavingAmountInfo()
                .collectLatest {
                    _fetchUpdateDailySavingAmountLiveData.emit(it)
                }
        }
    }

    fun fetchDailySavingUpdateJourney() {
        viewModelScope.launch {
            fetchFeatureViewUseCase.fetchFeature(com.jar.app.feature_homepage.shared.domain.model.FeatureFlag.DS_UPDATE_JOURNEY)
                .collectLatest {
                    _updateDailySavingCardFlow.emit(it)
                }
        }
    }

    fun mergeApiResponse(
        goldBalanceData: ApiResponseWrapper<GoldBalance?>? = goldBalanceLiveData.value.data,
        preNotifyAutopay: ApiResponseWrapper<PreNotifyAutopay>? = fetchPreNotificationLiveData.value.data,
        detectedSpendsData: ApiResponseWrapper<DetectedSpendsData?>? = detectedSpendInfoLiveData.value.data,
        promoCodeData: ApiResponseWrapper<Unit?>? = promoCodeUserSettingsLiveData.value.data,
        buyGoldData: ApiResponseWrapper<Unit?>? = buyGoldUserSettingsLiveData.value.data,
        dailyInvestmentStatusData: ApiResponseWrapper<DailyInvestmentStatus>? = dailyInvestmentStatusLiveData.value.data,
        spinsMetaData: ApiResponseWrapper<SpinsMetaData>? = spinsMetaDataLiveData.value.data,
        giftingData: ApiResponseWrapper<Unit?>? = giftingUserSettingsLiveData.value.data,
        goldDeliverData: ApiResponseWrapper<Unit?>? = goldDeliveryUserSettingsLiveData.value.data,
        roundOffCardData: ApiResponseWrapper<Unit?>? = roundOffCardLiveData.value.data,
        duoCardData: ApiResponseWrapper<Unit?>? = duoCardLiveData.value.data,
        vasooliCardData: ApiResponseWrapper<Unit?>? = vasooliCardLiveData.value.data,
        homePageExperimentsData: ApiResponseWrapper<Unit?>? = homePageExperimentsLiveData.value.data,
        autoInvestPauseData: ApiResponseWrapper<PauseSavingResponse>? = autoInvestPauseLiveData.value.data,
        dailyInvestPauseData: ApiResponseWrapper<PauseSavingResponse>? = dailyInvestPauseLiveData.value.data,
        roundOffPauseData: ApiResponseWrapper<PauseSavingResponse>? = roundOffPauseLiveData.value.data,
        bannerData: ApiResponseWrapper<BannerList>? = partnerBannerLiveData.value.data,
        userMetaData: ApiResponseWrapper<UserMetaData?>? = userMetaLiveData.value.data,
        userKycProgress: ApiResponseWrapper<KycProgressResponse?>? = userLendingKycProgressLiveData.value.data,
        updateDailySavingInfo: ApiResponseWrapper<UpdateDailySavingInfo>? = fetchUpdateDailySavingAmountLiveData.value.data,
        userGoldSipDetails: ApiResponseWrapper<UserGoldSipDetails?>? = userGoldSipDetailsLiveData.value.data,
        couponCodeDiscoveryData: ApiResponseWrapper<CouponCodeResponse?>? = couponCodesLiveData.value.data,
        vibaHorizontalCardData: ApiResponseWrapper<List<VibaHorizontalCardData>>? = vibaHorizontalCardFlowData.value.data,
        helpVideosData: ApiResponseWrapper<HelpVideosResponse>? = helpVideosLiveData.value.data,
        quickActionButtonResponse: ApiResponseWrapper<QuickActionResponse?>? = fetchQuickActionButtonsLiveData.value.data,
        lendingHomeCard: ApiResponseWrapper<Unit?>? = lendingHomeCardLiveData.value.data,
        healthInsuranceSingleHomeCard: ApiResponseWrapper<Unit?>? = healthInsuranceSingleCardLiveData.value.data,
        goldLeaseHomeCardData: ApiResponseWrapper<Unit?>? = goldLeaseHomeCardLiveData.value.data,
        firstCoinHomeScreenData: ApiResponseWrapper<FirstCoinHomeScreenData?>? = fetchFirstCoinHomeScreenDataLiveData.value.data,
        homeFeedActions: ApiResponseWrapper<Unit?>? = fetchHomeFeedActionsUseCaseLiveData.value.data,
        dailySavingCardData: com.jar.app.feature_homepage.shared.domain.model.DailySavingsV2CardResponse? = fetchDailySavingsCardLiveData.value.data?.data,
        duoContactsMetaData: DuoContactsMetaData? = duoContactMetaDataLiveData.value,
        spendTrackerData: ApiResponseWrapper<Unit?>? = spendTrackerHomeCardLiveData.value.data,
        healthInsuranceCardData: ApiResponseWrapper<Unit?>? = healthInsuranceHomeCardLiveData.value.data,
        weeklyMagicHomeCard: ApiResponseWrapper<WeeklyChallengeMetaData?>? = weeklyChallengeMetaDataLiveData.value.data,
        dailySavingGoalData: ApiResponseWrapper<Unit?>? = dailSavingGoalHomeCardLiveData.value.data,
        updateDailySavingData: ApiResponseWrapper<Unit?>? = updateDailySavingCardFlow.value?.data,
    ) {
//        job?.cancel()
        job = viewModelScope.launch(Dispatchers.Default) {
//            delay(1000)
            var list = mutableListOf<DynamicCard>()

            //Gold-Balance & Current Milestone Card
            if (goldBalanceData != null) {
                val view: LibraryCardData? = goldBalanceData.getViewData()
                if (view?.getCardType() != null && view.getCardType() == DynamicCardType.NONE) {
                    if (view.showCard && goldBalanceData.data != null) {
                        val currentGoldInvestmentCardData =
                            CurrentGoldInvestmentCardData(
                                goldBalance = goldBalanceData.data!!,
                                weeklyChallengeMetaData = weeklyChallengeMetaDataLiveData.value.data?.data,
                                order = view.order,
                                cardType = view.cardType,
                                header = view.header,
                                featureType = view.featureType,
                                firstCoinData = firstCoinHomeScreenData?.data,
                                quickActionsButtonData = quickActionButtonResponse?.data?.quickActionList,
                                shouldRunShimmer = view.cardMeta?.shouldRunShimmer.orFalse(),
                            )
                        list.add(currentGoldInvestmentCardData)
                    }
                } else {
                    if (view != null && view.showCard.orFalse())
                        list.add(view)
                }
            }

            //WeeklyMagicHomecard
            if (weeklyMagicHomeCard != null) {
                val view: LibraryCardData? = weeklyMagicHomeCard.getViewData()
                if (view?.getCardType() == DynamicCardType.NONE && view.showCard) {
                    val weeklyMagicHomecardData = WeeklyMagicHomecardData(
                        order = view.order,
                        cardType = view.cardType,
                        cardMeta = view.cardMeta,
                        featureType = view.featureType,
                        header = view.header,
                        cardsLeft = weeklyMagicHomeCard.data?.cardsLeft.orZero()
                    )
                    list.add(weeklyMagicHomecardData)
                }
            }

            //Gold Lease
            if (goldLeaseHomeCardData != null) {
                val view: LibraryCardData? = goldLeaseHomeCardData.getViewData()
                if (view != null && view.showCard.orFalse()) {
                    list.add(view)
                }
            }

            //Pre notification for autopay card
            if (preNotifyAutopay?.data != null) {
                val view: LibraryCardData? = preNotifyAutopay.getViewData()
                if (view?.getCardType() == DynamicCardType.NONE && view.showCard.orFalse() && preNotifyAutopay.data.shouldShowCard.orFalse()) {
                    val data = PreNotifyAutopayCardData(
                        order = view.order,
                        cardType = view.cardType,
                        featureType = view.featureType,
                        header = view.header,
                        ctaText = view.cardMeta?.cta?.text,
                        backgroundUrl = view.cardMeta?.cardBackground?.overlayImage,
                        iconUrl = view.cardMeta?.startIcon,
                        preNotifyAutopay = preNotifyAutopay.data
                    )
                    list.add(data)
                }
            }

            //Detected Spends Card
            if (detectedSpendsData != null) {
                val view: LibraryCardData? = detectedSpendsData.getViewData()
                if (view?.getCardType() == DynamicCardType.NONE) {
                    if (isPgEnabled() && detectedSpendsData.data != null) {
                        val detectedSpendCard = DetectedSpendData(
                            detectedSpendsData = detectedSpendsData.data!!,
                            order = view.order,
                            cardType = view.cardType,
                            header = view.header,
                            cta = view.cardMeta?.cta,
                            featureType = view.featureType,
                            shouldRunShimmer = view.cardMeta?.shouldRunShimmer.orFalse()
                        )
                        list.add(detectedSpendCard)
                    } else {
                        val paymentPrompt = PaymentPromptData(
                            order = view.order,
                            cardType = view.cardType,
                            header = view.header,
                            cta = view.cardMeta?.cta,
                            investPromptSuggestions = detectedSpendsData.data?.investPromptSuggestions,
                            featureType = view.featureType,
                            investPromptTitle = detectedSpendsData.data?.investPromptTitle,
                            shouldRunShimmer = view.cardMeta?.shouldRunShimmer.orFalse()
                        )
                        list.add(paymentPrompt)
                    }
                } else {
                    if (view != null && view.showCard.orFalse())
                        list.add(view)
                }
            }

            //Partner Banner Card
            if (bannerData != null) {
                val view: LibraryCardData? = bannerData.getViewData()
                if (view?.getCardType() == DynamicCardType.NONE) {
                    if (view.showCard.orFalse()) {
                        if (bannerData.data.banners.isEmpty().not()) {
                            val data = BannersData(
                                banners = bannerData.data.banners,
                                order = view.order,
                                cardType = view.cardType,
                                header = view.header,
                                cta = view.cardMeta?.cta,
                                featureType = view.featureType
                            )
                            list.add(data)
                        }
                    }
                } else {
                    if (view != null && view.showCard.orFalse())
                        list.add(view)
                }
            }

            //Promo Code Card
            if (promoCodeData != null) {
                val view: LibraryCardData? = promoCodeData.getViewData()
                if (view != null && view.showCard)
                    list.add(view)
            }

            //Spins Card
            if (spinsMetaData != null) {
                val view: LibraryCardData? = spinsMetaData.getViewData()
                if (view != null && view.showCard)
                    list.add(view)
            }

            //Buy Gold Card
            if (buyGoldData != null) {
                val view: LibraryCardData? = buyGoldData.getViewData()
                if (view != null && view.showCard)
                    list.add(view)
            }

            //Round Off Card
            if (roundOffCardData != null) {
                val view: LibraryCardData? = roundOffCardData.getViewData()
                if (view != null && view.showCard)
                    list.add(view)
            }

            if (spendTrackerData != null) {
                val view: LibraryCardData? = spendTrackerData.getViewData()
                if (view != null && view.showCard) {
                    list.add(view)
                }
            }
            if (healthInsuranceCardData != null) {
                val view: LibraryCardData? = healthInsuranceCardData.getViewData()
                if (view != null && view.showCard) {
                    list.add(view)
                }
            }

            if (dailySavingGoalData != null) {
                val view: LibraryCardData? = dailySavingGoalData.getViewData()
                if (view != null && view.showCard) {
                    list.add(view)
                }
            }

            if (updateDailySavingData != null) {
                val view: LibraryCardData? = updateDailySavingData.getViewData()
                if (view != null && view.showCard) {
                    list.add(view)
                }
            }

            //Gifting Card
            if (giftingData != null) {
                val view: LibraryCardData? = giftingData.getViewData()
                if (view != null && view.showCard)
                    list.add(view)
            }

            //Gold Delivery Data
            if (goldDeliverData != null) {
                val view: LibraryCardData? = goldDeliverData.getViewData()
                if (view != null && view.showCard)
                    list.add(view)
            }

            //Auto-Save Paused Card
            if (autoInvestPauseData != null) {
                val view: AlertCardData? = autoInvestPauseData.getViewData()
                view?.savingsType = SavingsType.AUTO_INVEST
                if (view != null && view.showCard)
                    list.add(view)
            }

            //Daily-Saving Paused Card
            if (dailyInvestPauseData != null) {
                val view: AlertCardData? = dailyInvestPauseData.getViewData()
                view?.savingsType = SavingsType.DAILY_SAVINGS
                if (view != null && view.showCard)
                    list.add(view)
            }

            //Round-Off Paused Card
            if (roundOffPauseData != null) {
                val view: AlertCardData? = roundOffPauseData.getViewData()
                view?.savingsType = SavingsType.ROUND_OFFS
                if (view != null && view.showCard)
                    list.add(view)
            }

            //Experiment Cards
            if (homePageExperimentsData != null) {
                val views: List<LibraryCardData>? = homePageExperimentsData.getViewData()
                views?.forEach {
                    if (it.showCard)
                        list.add(it)
                }
            }

            //Home Feed Actions
            if (homeFeedActions != null) {
                val views: List<LibraryCardData?>? = homeFeedActions.getViewData()
                views?.forEach {
                    it?.let {
                        if (it.showCard)
                            list.add(it)
                    }
                }
            }

            //Setup Daily-Saving Card
//            if (dailyInvestmentStatusData != null && dailySavingCardData != null) {
//                val view: LibraryCardData? = dailyInvestmentStatusData.getViewData()
//                if (false && view != null && view.showCard) {
//                    val data = com.jar.app.feature_homepage.shared.domain.model.DailySavingCardData(
//                        order = view.order,
//                        cardType = view.cardType,
//                        featureType = view.featureType,
//                        header = view.header,
//                        dailySavingV2CardData = dailySavingCardData
//                    )
//                    list.add(data)
//                }
//            }

            //Temporary Loan Card
            if (userMetaData?.data?.shouldShowLoanCard.orFalse()) {
                val view = LoanCardData(
                    100,
                    DynamicCardType.NONE.name,
                    HomeConstants.HomeCardFeatureType.LOAN_CARD + userKycProgress?.data?.kycVerified.orFalse(),
                    userKycProgress?.data?.kycVerified.orFalse()
                )
                list.add(view)
            }

            //Vasooli Card
            if (vasooliCardData != null) {
                val view: LibraryCardData? = vasooliCardData.getViewData()
                if (view?.getCardType() == DynamicCardType.NONE) {
                    if (view.showCard.orFalse()) {
                        val data = VasooliCardData(
                            order = view.order,
                            cardType = view.cardType,
                            featureType = view.featureType,
                            header = view.header
                        )
                        list.add(data)
                    }
                }
            }

            //Duo Card
            if (duoCardData != null && duoContactsMetaData != null) {
                val view: LibraryCardData? = duoCardData.getViewData()
                if (view?.getCardType() == DynamicCardType.NONE && view.showCard.orFalse()) {
                    val data = JarDuoData(
                        order = view.order,
                        cardType = view.cardType,
                        featureType = view.featureType,
                        header = view.header,
                        duoContactsMetaData = duoContactsMetaData
                    )
                    list.add(data)
                }
            }

            //Update Daily Saving Card
            if (updateDailySavingInfo != null) {
                val view: LibraryCardData? = updateDailySavingInfo.getViewData()
                if (view != null && view.showCard && updateDailySavingInfo.data.updateDailySavingData?.eligibleForDSUpdate.orFalse()) {
                    val data = UpdateDailySavingCardData(
                        order = view.order,
                        cardType = view.cardType,
                        featureType = view.featureType,
                        header = view.header,
                        updateDailySavingData = updateDailySavingInfo.data.updateDailySavingData
                    )
                    list.add(data)
                }
            }

            //Gold Sip Card
            if (userGoldSipDetails != null) {
                val view = userGoldSipDetails.getViewData<LibraryCardData?>()
                if (userGoldSipDetails.data?.enabled.orFalse() && userGoldSipDetails.data?.manualPaymentDetails != null && view != null && view.showCard && userGoldSipDetails.data != null) {
                    val goldSipCard = GoldSipCard(
                        order = view.order,
                        cardType = view.cardType,
                        featureType = view.featureType,
                        header = view.header,
                        description = view.description,
                        goldSipData = GoldSipData(
                            amount = userGoldSipDetails.data!!.manualPaymentDetails?.txnAmt?.toInt()
                                .orZero(),
                            subscriptionType = userGoldSipDetails.data!!.subscriptionType.orEmpty(),
                            subscriptionStatus = userGoldSipDetails.data!!.subscriptionStatus,
                            postSipTitleText = userGoldSipDetails.data!!.manualPaymentDetails?.description,
                            orderId = userGoldSipDetails.data!!.manualPaymentDetails?.orderId
                        ),
                    )
                    list.add(goldSipCard)
                }
                //Post SIP setup Card
                else if (
                    userGoldSipDetails.data?.enabled.orFalse().not()
                    && view != null && view.showCard
                ) {
                    val goldSipCard = GoldSipCard(
                        order = view.order,
                        cardType = view.cardType,
                        featureType = view.featureType,
                        header = view.header,
                        description = view.description,
                        goldSipData = GoldSipData(
                            amount = userGoldSipDetails.data?.subscriptionAmount?.toInt()
                                .orZero(),
                            subscriptionType = userGoldSipDetails.data?.subscriptionType.orEmpty()
                        ),
                    )
                    list.add(goldSipCard)
                }
            }
            //Coupon Code Discovery
            if (couponCodeDiscoveryData?.data != null && couponCodeDiscoveryData.data?.couponCodes != null && couponCodeDiscoveryData.data?.couponCodes!!.any { it.showOnHomeScreen.orFalse() && it.getCouponState() == CouponState.ACTIVE }) {
                val view: LibraryCardData? = couponCodeDiscoveryData.getViewData()
                if (view != null && view.showCard) {
                    val data =
                        com.jar.app.feature_homepage.shared.domain.model.CouponCodeDiscoveryData(
                            order = view.order,
                            cardType = view.cardType,
                            featureType = view.featureType,
                            header = view.header,
                            data = couponCodeDiscoveryData.data?.couponCodes!!.filter { it.showOnHomeScreen.orFalse() && it.getCouponState() == CouponState.ACTIVE }
                        )
                    list.add(data)
                }
            }

            //Help Videos
            if (helpVideosData != null && helpVideosData.data.helpVideoData.isEmpty().not()) {
                val view: LibraryCardData? = helpVideosData.getViewData()
                if (view != null && view.showCard) {
                    val data = HelpVideosData(
                        order = view.order,
                        cardType = view.cardType,
                        featureType = view.featureType,
                        header = view.header,
                        data = helpVideosData.data.helpVideoData
                    )
                    list.add(data)
                }
            }

            // Viba Card
            if (vibaHorizontalCardData != null) {
                val vibaCardData: LibraryCardData? = vibaHorizontalCardData.getViewData()
                if (vibaCardData != null && vibaCardData.showCard && vibaCardData.getCardType() == DynamicCardType.NONE) {
                    val data = VibaHorizontalCard(
                        order = vibaCardData.order,
                        cardType = vibaCardData.cardType,
                        featureType = vibaCardData.featureType,
                        header = vibaCardData.header,
                        vibaCardData = vibaHorizontalCardData.data
                    )
                    list.add(data)
                }
            }

            //Lending Home Card
            if (lendingHomeCard != null) {
                val view: LibraryCardData? = lendingHomeCard.getViewData()
                if (view?.cardMeta != null) {
                    if (view.showCard) {
                        if (view.featureType.contains(
                                BaseConstants.LENDING_PROGRESS_CARD_FEATURE_TYPE,
                                true
                            )
                        ) {
                            //Progress Card
                            list.add(
                                LendingProgressCardData(
                                    order = view.order,
                                    cardType = view.cardType,
                                    featureType = view.featureType,
                                    header = view.header,
                                    progress = view.data?.jsonObject?.get("progressBar")?.jsonPrimitive?.intOrNull.orZero(),
                                    cardData = view.cardMeta!!,
                                )
                            )
                        } else {
                            //Regular Card Used in First time user, Repeat withdrawal, real time
                            list.add(
                                RecommendedHomeCardData(
                                    order = view.order,
                                    cardType = DynamicCardType.NONE.name,
                                    featureType = view.featureType,
                                    cardData = view.cardMeta,
                                    offerAmount = view.data?.jsonObject?.get("availableLimit")?.jsonPrimitive?.intOrNull,
                                    header = view.header,
                                    verticalPosition = view.verticalPosition,
                                    horizontalPosition = view.horizontalPosition,
                                    shouldShowLabelTop = view.shouldShowLabelTop
                                )
                            )
                        }

                    }
                }

            }
            if (healthInsuranceSingleHomeCard != null) {
                val view: LibraryCardData? = healthInsuranceSingleHomeCard.getViewData()

                //Check to sort between old and new cards based on data.cardType

                if (view?.cardMeta != null) {
                    if (view.showCard) {
                            list.add(
                                RecommendedHomeCardData(
                                    order = view.order,
                                    cardType = DynamicCardType.NONE.name,
                                    featureType = view.featureType,
                                    cardData = view.cardMeta,
                                    offerAmount = view.data?.jsonObject?.get("availableLimit")?.jsonPrimitive?.intOrNull,
                                    header = view.header,
                                    verticalPosition = view.verticalPosition,
                                    horizontalPosition = view.horizontalPosition,
                                    shouldShowLabelTop = view.shouldShowLabelTop
                                )
                            )
                    }
                }
                if(view?.data?.jsonObject?.get("cardType")?.jsonPrimitive?.content == BaseConstants.HEALTH_INSURANCE_HOMEFEED_CAROUSEL) {
                    view.data?.let { jsonElement ->
                        val imageCardData = Json.decodeFromJsonElement<ImageCardData>(jsonElement)
                        list.add(
                            ImageCardCarouselData(
                                order = view.order,
                                cardType = DynamicCardType.NONE.name,
                                featureType = view.featureType,
                                data = imageCardData,
                                header = view.header,
                                verticalPosition = view.verticalPosition,
                                horizontalPosition = view.horizontalPosition,
                                shouldShowLabelTop = view.shouldShowLabelTop
                            )
                        )
                    }
                }
            }

            DynamicCardUtil.rearrangeDynamicCards(list)

//            //PreLoad and Caching for exoplayer
//            list.filter { it.getCardType() == DynamicCardType.VIDEO }.forEach {
//                if (!_hasVideoLiveData.value.orFalse()) _hasVideoLiveData.emit(true)
//                (it as? LibraryCardData)?.cardMeta?.infographic?.url?.let { url ->
//                    exoplayerCachingUtil.cache(url, viewModelScope)
//                }
//            }

            //Set Position Before Adding Headers
            list.forEachIndexed { index, dynamicCard ->
                dynamicCard.verticalPosition = index
            }

            val newList = ArrayList<DynamicCard>(list.size * 2)
            list.forEachIndexed { index, current ->
                val prev = list.getOrNull(index - 1)
                val currentHeader = current.getCardHeader()
                val currentDesc = current.getCardDescription()
                val prevHeader = prev?.getCardHeader()
                if (currentHeader?.convertToRawString()
                        ?.equals(prevHeader?.convertToRawString()) == false
                ) {
                    newList.add(
                        HeaderSection(
                            title = currentHeader,
                            description = currentDesc,
                            position = index
                        )
                    )
                }
                newList.add(current)
            }
            _listLiveData.emit(newList)
        }
    }

    private fun isPgEnabled(): Boolean {
        return detectedSpendInfoLiveData.value.data?.data?.isPGEnabled.orFalse()
    }

    fun claimBonus(orderId: String) {
        viewModelScope.launch {
            claimBonusUseCase.claimBonus(orderId).collectLatest {
                _claimedBonusLiveData.emit(it)
            }
        }
    }

    private fun fetchHomeFeedImages() {
        viewModelScope.launch {
            fetchHomeFeedImagesUseCase.fetchHomeFeedImages().collectLatest {
                _homeFeedImageLiveData.emit(it)
            }
        }
    }

    fun postAppWalkthroughEvents(section: SectionType, buttonType: String) {
        analyticsApi.postEvent(
            EventKey.Appwalkthrough_shown,
            mapOf(
                EventKey.Step to section.name,
                EventKey.ButtonType to buttonType
            )
        )
    }
}
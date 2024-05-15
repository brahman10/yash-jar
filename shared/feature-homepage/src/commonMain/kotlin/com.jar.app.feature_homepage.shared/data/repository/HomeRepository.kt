package com.jar.app.feature_homepage.shared.data.repository

import com.jar.app.core_base.domain.model.InfoDialogResponse
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.feature_homepage.shared.domain.model.AppWalkthroughResp
import com.jar.app.feature_homepage.shared.domain.model.hamburger.HamburgerData
import com.jar.app.feature_homepage.shared.domain.model.DailySavingsV2CardResponse
import com.jar.app.feature_homepage.shared.domain.model.FeatureFlag
import com.jar.app.feature_homepage.shared.domain.model.FeatureRedirectionResp
import com.jar.app.feature_homepage.shared.domain.model.FirstCoinHomeScreenData
import com.jar.app.feature_homepage.shared.domain.model.FirstCoinProgressData
import com.jar.app.feature_homepage.shared.domain.model.FirstCoinTransitionData
import com.jar.app.feature_homepage.shared.domain.model.FirstGoldCoinIntro
import com.jar.app.feature_homepage.shared.domain.model.HelpVideosResponse
import com.jar.app.feature_homepage.shared.domain.model.HomeScreenPrompt
import com.jar.app.feature_homepage.shared.domain.model.HomeStaticCardOrderingData
import com.jar.app.feature_homepage.shared.domain.model.InAppReviewStatus
import com.jar.app.feature_homepage.shared.domain.model.KeyValueData
import com.jar.app.feature_homepage.shared.domain.model.PreNotifyAutopay
import com.jar.app.feature_homepage.shared.domain.model.QuickActionResponse
import com.jar.app.feature_homepage.shared.domain.model.ShouldSendSmsOnDemand
import com.jar.app.feature_homepage.shared.domain.model.partner_banner.BannerList
import com.jar.app.feature_homepage.shared.domain.model.round_off.RoundOffData
import com.jar.app.feature_homepage.shared.domain.model.single_home_feed.SingleHomeFeedCardMetaData
import com.jar.app.feature_homepage.shared.domain.model.update_daily_saving.UpdateDailySavingInfo
import com.jar.app.feature_homepage.shared.domain.model.user_gold_breakdown.UserGoldBreakdownResponse
import com.jar.app.feature_homepage.shared.domain.model.viba.VibaHorizontalCardData
import com.jar.internal.library.jar_core_network.api.data.BaseRepositoryV2
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface HomeRepository : BaseRepositoryV2 {

    suspend fun fetchHomeScreenBottomSheetPrompt(): Flow<RestClientResult<ApiResponseWrapper<HomeScreenPrompt?>>>

    suspend fun fetchFeature(featureFlag: FeatureFlag): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchHomePageExperiments(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchStaticPopupInfo(contentType: String): Flow<RestClientResult<ApiResponseWrapper<InfoDialogResponse>>>

    suspend fun fetchPartnerBanners(includeView: Boolean): Flow<RestClientResult<ApiResponseWrapper<BannerList>>>

    suspend fun claimBonus(orderId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchRoundOffCardData(): Flow<RestClientResult<ApiResponseWrapper<RoundOffData?>>>

    suspend fun fetchHomeStaticCardsOrdering(): Flow<RestClientResult<ApiResponseWrapper<HomeStaticCardOrderingData>>>

    suspend fun fetchUpdateDailySavingAmountInfo(includeView: Boolean): Flow<RestClientResult<ApiResponseWrapper<UpdateDailySavingInfo>>>

    suspend fun fetchHelpVideos(
        language: String,
        includeView: Boolean
    ): Flow<RestClientResult<ApiResponseWrapper<HelpVideosResponse>>>

    suspend fun fetchUpcomingPreNotification(includeView: Boolean): Flow<RestClientResult<ApiResponseWrapper<PreNotifyAutopay>>>

    suspend fun fetchQuickActions(type: String): Flow<RestClientResult<ApiResponseWrapper<QuickActionResponse?>>>

    suspend fun dismissUpcomingPreNotification(
        dismissalType: String,
        preNotificationIds: List<String>
    ): Flow<RestClientResult<ApiResponseWrapper<PreNotifyAutopay>>>

    suspend fun fetchFirstGoldCoinIntro(): Flow<RestClientResult<ApiResponseWrapper<FirstGoldCoinIntro>>>

    suspend fun fetchFirstCoinHomeScreenData(): Flow<RestClientResult<ApiResponseWrapper<FirstCoinHomeScreenData?>>>

    suspend fun fetchFirstCoinProgressData(): Flow<RestClientResult<ApiResponseWrapper<FirstCoinProgressData>>>

    suspend fun fetchFirstCoinTransitionPageData(): Flow<RestClientResult<ApiResponseWrapper<FirstCoinTransitionData>>>

    suspend fun sendFirstCoinOnboardingStatus(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun updateFirstCoinDeliveryStatus(orderId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchHomeFeedActions(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchDSCardData(): Flow<RestClientResult<ApiResponseWrapper<DailySavingsV2CardResponse?>>>

    suspend fun updateUserInteraction(
        order: Int,
        featureType: String
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun shouldSendSmsOnDemand(): Flow<RestClientResult<ApiResponseWrapper<ShouldSendSmsOnDemand>>>
    suspend fun fetchHomeFeedImages(): Flow<RestClientResult<ApiResponseWrapper<List<KeyValueData>>>>

    suspend fun fetchInAppReviewStatus(): Flow<RestClientResult<ApiResponseWrapper<InAppReviewStatus>>>
    suspend fun updateLockerViewShown(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchAppWalkthrough(): Flow<RestClientResult<ApiResponseWrapper<AppWalkthroughResp?>>>

    suspend fun updateAppWalkthroughCompleted(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun clearAllData()

    suspend fun fetchVibaCardDetails(): Flow<RestClientResult<ApiResponseWrapper<List<VibaHorizontalCardData>>>>

    suspend fun fetchHamburgerData(): Flow<RestClientResult<ApiResponseWrapper<HamburgerData?>>>

    suspend fun fetchUserGoldBreakdown(): Flow<RestClientResult<ApiResponseWrapper<UserGoldBreakdownResponse?>>>

    suspend fun fetchFeatureRedirectionData(): Flow<RestClientResult<ApiResponseWrapper<FeatureRedirectionResp?>>>

    suspend fun fetchBottomNavStickyCardData(): Flow<RestClientResult<ApiResponseWrapper<LibraryCardData?>>>
}
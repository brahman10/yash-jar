package com.jar.app.feature_homepage.shared.data.network

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.toJsonElement
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
import com.jar.app.feature_homepage.shared.util.HomeConstants.Endpoints
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

class HomeDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchHomeScreenBottomSheetPrompt() =
        getResult<ApiResponseWrapper<HomeScreenPrompt?>> {
            client.get {
                url(Endpoints.FETCH_HOME_SCREEN_BOTTOM_SHEET_PROMPT)
            }
        }

    suspend fun fetchFeature(featureFlag: FeatureFlag) =
        getResultV2<ApiResponseWrapper<Unit?>>(
            getCachingKey = {
                featureFlag.name
            }
        ) {
            client.get {
                url(Endpoints.FETCH_FEATURES)
                parameter("feature", featureFlag.name)
            }
        }

    suspend fun fetchHomePageExperiments() =
        getResultV2<ApiResponseWrapper<Unit?>>(
            getCachingKey = {
                "fetchHomePageExperiments"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_HOME_PAGE_EXPERIMENTS)
            }
        }

    suspend fun fetchStaticPopupInfo(contentType: String) =
        getResultV2<ApiResponseWrapper<InfoDialogResponse>>(
            getCachingKey = {
                "fetchStaticPopupInfo$contentType"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_STATIC_POPUP_INFO)
                parameter("contentType", contentType)
            }
        }

    suspend fun fetchUpdateDailySavingAmountInfo(
        contentType: String = BaseConstants.StaticContentType.UPDATE_DS_AMOUNT.name,
        includeView: Boolean
    ) =
        getResultV2<ApiResponseWrapper<UpdateDailySavingInfo>>(
            getCachingKey = {
                "fetchUpdateDailySavingAmountInfo$contentType$includeView"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_UPDATE_DAILY_SAVING_AMOUNT_INFO)
                parameter("contentType", contentType)
                parameter("includeView", includeView)
            }
        }

    suspend fun fetchPartnerBanners(includeView: Boolean) =
        getResultV2<ApiResponseWrapper<BannerList>>(
            getCachingKey = {
                "fetchPartnerBanners$includeView"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_PARTNER_BANNER)
                parameter("includeView", includeView)
            }
        }

    suspend fun claimBonus(orderId: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.CLAIM_PARTNER_BONUS)
                setBody(
                    JsonObject(
                        mapOf(
                            "orderId" to orderId.toJsonElement()
                        )
                    )
                )
            }
        }

    suspend fun fetchRoundOffCardData() =
        getResultV2<ApiResponseWrapper<RoundOffData?>>(
            getCachingKey = {
                "fetchRoundOffCardData"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_ROUND_OFF_CARD_DATA)
            }
        }

    suspend fun fetchHomeStaticCardsOrdering() =
        getResultV2<ApiResponseWrapper<HomeStaticCardOrderingData>>(
            getCachingKey = {
                "fetchHomeStaticCardsOrdering"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_HOME_STATIC_CARD_ORDERING)
                parameter("contentType", BaseConstants.StaticContentType.CARD_ORDER.name)
            }
        }

    suspend fun fetchHelpVideos(contentType: String, language: String, includeView: Boolean) =
        getResultV2<ApiResponseWrapper<HelpVideosResponse>>(
            getCachingKey = {
                "fetchHelpVideos$contentType$language$includeView"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_HELP_VIDEOS)
                parameter("contentType", contentType)
                parameter("language", language)
                parameter("includeView", includeView)
            }
        }

    suspend fun fetchUpcomingPreNotification(includeView: Boolean) =
        getResultV2<ApiResponseWrapper<PreNotifyAutopay>>(
            getCachingKey = {
                "fetchUpcomingPreNotification$includeView"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_UPCOMING_PRE_NOTIFICATION)
                parameter("includeView", includeView)
            }
        }

    suspend fun dismissUpcomingPreNotification(
        dismissalType: String,
        preNotificationIds: List<String>
    ) = getResult<ApiResponseWrapper<PreNotifyAutopay>> {
        client.post {
            url(Endpoints.DISMISS_UPCOMING_PRE_NOTIFICATION)
            setBody(
                mapOf(
                    "dismissalType" to dismissalType.toJsonElement(),
                    "preNotificationIds" to JsonArray(preNotificationIds.map { it.toJsonElement() })
                )
            )
        }
    }

    suspend fun fetchQuickActions(type: String) =
        getResultV2<ApiResponseWrapper<QuickActionResponse?>>(
            getCachingKey = {
                "fetchQuickActions$type"
            }
        ) {
            client.get {
                url(Endpoints.GET_ALL_QUICK_ACTION_CARDS)
                parameter("type", type)
            }
        }

    suspend fun fetchFirstGoldCoinIntro() =
        getResultV2<ApiResponseWrapper<FirstGoldCoinIntro>>(
            getCachingKey = {
                "fetchFirstGoldCoinIntro"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_FIRST_GOLD_COIN_INTRO)
            }
        }

    suspend fun fetchFirstCoinHomeScreenData() =
        getResultV2<ApiResponseWrapper<FirstCoinHomeScreenData?>>(
            getCachingKey = {
                "fetchFirstCoinHomeScreenData"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_FIRST_GOLD_COIN_LANDING_DATA)
            }
        }

    suspend fun fetchFirstCoinProgressData() =
        getResultV2<ApiResponseWrapper<FirstCoinProgressData>>(
            getCachingKey = {
                "fetchFirstCoinProgressData"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_FIRST_GOLD_COIN_PROGRESS)
            }
        }

    suspend fun fetchFirstCoinTransitionPageData() =
        getResultV2<ApiResponseWrapper<FirstCoinTransitionData>>(
            getCachingKey = {
                "fetchFirstCoinTransitionPageData"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_FIRST_GOLD_COIN_TRANSITION_PAGE_DATA)
            }
        }

    suspend fun sendFirstCoinOnboardingStatus() =
        getResult<ApiResponseWrapper<Unit?>> {
            client.get {
                url(Endpoints.FETCH_FIRST_GOLD_COIN_ONBOARDING_DATA)
            }
        }

    suspend fun updateFirstCoinDeliveryStatus(
        orderId: String
    ) = getResult<ApiResponseWrapper<Unit?>> {
        client.post {
            url(Endpoints.UPDATE_FIRST_COIN_DELIVERY_PROGRESS)
            setBody(
                JsonObject(
                    mapOf(
                        "orderId" to orderId.toJsonElement()
                    )
                )
            )
        }
    }

    suspend fun fetchHomeFeedActions() =
        getResultV2<ApiResponseWrapper<Unit?>>(
            getCachingKey = {
                "fetchHomeFeedActions"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_HOME_FEED_ACTIONS)
            }
        }

    suspend fun fetchDSCardData() =
        getResultV2<ApiResponseWrapper<DailySavingsV2CardResponse?>>(
            getCachingKey = {
                "fetchDSCardData"
            }
        ) {
            client.get {
                url(Endpoints.FETCH_DS_CARD_DATA)
            }
        }

    suspend fun fetchHomeFeedImages() =
        getResult<ApiResponseWrapper<List<KeyValueData>>> {
            client.get {
                url(Endpoints.FETCH_HOME_FEED_IMAGES)
            }
        }

    suspend fun updateLockerViewShown() =
        getResult<ApiResponseWrapper<Unit?>> {
            client.get {
                url(Endpoints.UPDATE_LOCKER_VIEW_SHOWN)
            }
        }

    suspend fun fetchInAppReviewStatus() =
        getResult<ApiResponseWrapper<InAppReviewStatus>> {
            client.get {
                url(Endpoints.FETCH_IN_APP_REVIEW_STATUS)
            }
        }

    suspend fun updateUserInteraction(order: Int, featureType: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.UPDATE_USER_INTERACTION)
                parameter("order", order)
                parameter("featureType", featureType)
            }
        }

    suspend fun shouldSendSmsOnDemand() =
        getResult<ApiResponseWrapper<ShouldSendSmsOnDemand>> {
            client.get {
                url(Endpoints.SHOULD_SEND_SMS_ON_DEMAND)
            }
        }

    suspend fun fetchVibaCardDetails() =
        getResult<ApiResponseWrapper<List<VibaHorizontalCardData>>> {
            client.get {
                url(Endpoints.VIBA_HOME_FEED)
            }
        }

    suspend fun fetchHamburgerData() =
        getResult<ApiResponseWrapper<HamburgerData?>> {
            client.get {
                url(Endpoints.FETCH_HAMBURGER_DATA)
                parameter("contentType", "HAMBURGER_ITEMS")
            }
        }

    suspend fun fetchAppWalkthrough() =
        getResult<ApiResponseWrapper<AppWalkthroughResp?>> {
            client.get {
                url(Endpoints.FETCH_APP_WALKTHROUGH)
            }
        }

    suspend fun updateAppWalkthroughCompleted() =
        getResult<ApiResponseWrapper<Unit?>> {
            client.put {
                url(Endpoints.UPDATE_APP_WALKTHROUGH_COMPLETED)
            }
        }


    suspend fun fetchUserGoldBreakdown() =
        getResult<ApiResponseWrapper<UserGoldBreakdownResponse?>> {
            client.get {
                url(Endpoints.FETCH_USER_GOLD_BREAKDOWN)
            }
        }

    suspend fun fetchBottomNavStickyCardData() =
        getResult<ApiResponseWrapper<LibraryCardData?>> {
            client.get{
                url(Endpoints.FETCH_BOTTOM_NAV_STICKY_CARD)
            }
        }


    suspend fun fetchFeatureRedirectionData() =
        getResult<ApiResponseWrapper<FeatureRedirectionResp?>> {
            client.get {
                url(Endpoints.FETCH_FEATURE_REDIRECTION_DATA)
            }
        }
}
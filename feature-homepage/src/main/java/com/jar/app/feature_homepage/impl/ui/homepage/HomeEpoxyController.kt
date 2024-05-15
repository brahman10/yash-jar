package com.jar.app.feature_homepage.impl.ui.homepage

import android.view.View
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player.REPEAT_MODE_OFF
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.jar.app.core_base.domain.model.GoldBalanceViewType
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.GridListCard
import com.jar.app.core_base.domain.model.card_library.HorizontalListCard
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.StaticInfoData
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.dynamic_cards.base.CustomGridCarousalModel
import com.jar.app.core_ui.dynamic_cards.base.CustomLinearCarousalModel
import com.jar.app.core_ui.dynamic_cards.card_library.LargeCardEpoxyModel
import com.jar.app.core_ui.dynamic_cards.card_library.MediumCardEpoxyModel
import com.jar.app.core_ui.dynamic_cards.card_library.ShimmerEpoxyModel
import com.jar.app.core_ui.dynamic_cards.card_library.SmallCardEpoxyModel
import com.jar.app.core_ui.dynamic_cards.card_library.StripOneCardEpoxyModel
import com.jar.app.core_ui.dynamic_cards.card_library.StripTwoCardEpoxyModel
import com.jar.app.core_ui.dynamic_cards.card_library.TicketCardEpoxyModel
import com.jar.app.core_ui.dynamic_cards.card_library.VideoCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.BigRightImageOldBtnEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.ContentHeaderEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.CouponCodeDiscoveryCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.CurrentGoldInvestmentCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.DailySavingsV2EpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.DetectedSpendCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.GoldSipCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.HeaderSectionEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.HelpVideosCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.HomeFeedPlotlineEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.ImageCarouselEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.LendingEligibilityCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.LendingProgressCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.LendingReadyCashCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.LendingSecondLoanCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.LoanCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.PartnerBannerEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.PaymentPromptEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.PostSetupGoldSipCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.PreNotifyAutopayEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.SinglePageHomeFeedEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.TypeBigRightImageEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.TypeBigRightImageWithTwoCtaEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.TypeEightEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.TypeElevenEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.TypeFiveEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.TypeFourEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.TypeNineEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.TypeOneEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.TypeSevenEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.TypeSixEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.TypeTenEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.TypeThreeEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.TypeTwoEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.UpdateDailySavingEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.VasooliCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.VibaCardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.WeeklyMagicHomecardEpoxyModel
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.jarDuoV2.JarDuoEpoxyModelV2
import com.jar.app.feature_homepage.shared.domain.model.CouponCodeDiscoveryData
import com.jar.app.feature_homepage.shared.domain.model.FestivalCampaignData
import com.jar.app.feature_homepage.shared.domain.model.HeaderSection
import com.jar.app.feature_homepage.shared.domain.model.HelpVideosData
import com.jar.app.feature_homepage.shared.domain.model.ImageCardCarouselData
import com.jar.app.feature_homepage.shared.domain.model.JarDuoData
import com.jar.app.feature_homepage.shared.domain.model.LendingEligibilityCardData
import com.jar.app.feature_homepage.shared.domain.model.LendingProgressCardData
import com.jar.app.feature_homepage.shared.domain.model.LendingSecondLoanCardData
import com.jar.app.feature_homepage.shared.domain.model.LoanCardData
import com.jar.app.feature_homepage.shared.domain.model.PreNotifyAutopayCardData
import com.jar.app.feature_homepage.shared.domain.model.RecommendedHomeCardData
import com.jar.app.feature_homepage.shared.domain.model.VasooliCardData
import com.jar.app.feature_homepage.shared.domain.model.WeeklyMagicHomecardData
import com.jar.app.feature_homepage.shared.domain.model.current_investment.CurrentGoldInvestmentCardData
import com.jar.app.feature_homepage.shared.domain.model.detected_spends.DetectedSpendData
import com.jar.app.feature_homepage.shared.domain.model.gold_sip.GoldSipCard
import com.jar.app.feature_homepage.shared.domain.model.gold_sip.GoldSipData
import com.jar.app.feature_homepage.shared.domain.model.partner_banner.Banner
import com.jar.app.feature_homepage.shared.domain.model.partner_banner.BannersData
import com.jar.app.feature_homepage.shared.domain.model.payment_prompt.PaymentPromptData
import com.jar.app.feature_homepage.shared.domain.model.single_home_feed.SinglePageHomeFeedData
import com.jar.app.feature_homepage.shared.domain.model.update_daily_saving.UpdateDailySavingCardData
import com.jar.app.feature_homepage.shared.domain.model.viba.VibaHorizontalCard
import kotlinx.coroutines.CoroutineScope

internal class HomeEpoxyController(
    private val uiScope: CoroutineScope,
    private val festivalCampaignData: FestivalCampaignData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit = { },
    private val onPrimaryCtaClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val onShowMoreCtaClick: (cardEventData: CardEventData) -> Unit = { _ -> },
    private val onVibaCardClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val onEndIconClick: (staticInfoData: StaticInfoData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val onPromptInvestClick: (amount: Float, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val onDetectedSpendsInvestClick: (cardEventData: CardEventData) -> Unit = { },
    private val onAmountClick: () -> Unit = {},
    private val onPartPaymentClick: () -> Unit = {},
    private val onGoldBreakdownClick: (whichType: GoldBalanceViewType) -> Unit = {},
    private val onClaimPartnerBonusClick: (banner: Banner) -> Unit = {},
    private val onShowAllPartnerBonusClick: () -> Unit = {},
    private val onSliderMoved: (amount: Int) -> Unit = { _ -> },
    private val onGoldSipManualPaymentClicked: (cardEventData: CardEventData, goldSipData: GoldSipData) -> Unit = { _, _ -> },
    private val footerCtaClick: (preNotifyAutopayCardData: PreNotifyAutopayCardData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val neverShowAgainCtaClicked: (preNotifyAutopayCardData: PreNotifyAutopayCardData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val dismissCtaClick: (preNotifyAutopayCardData: PreNotifyAutopayCardData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val onFirstCoinClick: (deepLink: String, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val onMagicHatNotchClick: () -> Unit,
    private val invokeNextChallenge: () -> Unit,
    private val onFirstTransactionAnimationEnd: () -> Unit,
    private val hasContactPermission: Boolean
) : EpoxyController() {

    init {
        Carousel.setDefaultGlobalSnapHelperFactory(null)
    }

    var cards: List<DynamicCard>? = null
        set(value) {
            field = value
            cancelPendingModelBuild()
            requestModelBuild()
        }

    var exoPlayer: ExoPlayer? = null
        set(value) {
            field = value
            exoPlayer?.repeatMode = REPEAT_MODE_OFF
        }

    var scope: CoroutineScope? = null

    var cacheDataSourceFactory: CacheDataSource.Factory? = null

    override fun buildModels() {
        if (cards.isNullOrEmpty()) {
            ShimmerEpoxyModel()
                .id("shimmer_loader")
                .addTo(this)
        } else {
            val list = cards!!

            list.forEach {
                when (it.getCardType()) {
                    /** If [LARGE]: add it as usual & move forward **/
                    DynamicCardType.HEADER -> {
                        HeaderSectionEpoxyModel(
                            it as HeaderSection
                        )
                            .id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.HOMEFEED_TYPE_CONTENT_HEADER -> {
                        ContentHeaderEpoxyModel(it as LibraryCardData)
                            .id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.HOMEFEED_TYPE_FOUR -> {
                        TypeFourEpoxyModel(
                            it as LibraryCardData,
                            uiScope,
                            onCardShown,
                            onPrimaryCtaClick
                        )
                            .id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.HOMEFEED_TYPE_BIG_RIGHT_IMAGE -> {
                        TypeBigRightImageEpoxyModel(
                            libraryCardViewData = it as LibraryCardData,
                            uiScope = uiScope,
                            onCardShown = onCardShown,
                            onActionClick = onPrimaryCtaClick
                        ).id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.HOMEFEED_TYPE_SIX -> {
                        TypeSixEpoxyModel(
                            libraryCardViewData = it as LibraryCardData,
                            uiScope = uiScope,
                            onCardShown = onCardShown,
                            onActionClick = onPrimaryCtaClick,
                            onEndIconClick = onEndIconClick,
                        ).id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.HOMEFEED_TYPE_BIG_RIGHT_IMAGE_TWO_CTA -> {
                        TypeBigRightImageWithTwoCtaEpoxyModel(
                            libraryCardViewData = it as LibraryCardData,
                            uiScope = uiScope,
                            onCardShown = onCardShown,
                            onActionClick = onPrimaryCtaClick
                        ).id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.HOMEFEED_PLOTLINE -> {
                        val data = (it as LibraryCardData)
                        HomeFeedPlotlineEpoxyModel(
                            featureType = data.featureType
                        ).id(data.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.HOMEFEED_TYPE_SEVEN -> {
                        TypeSevenEpoxyModel(
                            libraryCardViewData = it as LibraryCardData,
                            uiScope = uiScope,
                            onCardShown = onCardShown,
                            onActionClick = onPrimaryCtaClick
                        ).id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.HOMEFEED_TYPE_TEN -> {
                        TypeTenEpoxyModel(
                            libraryCardViewData = it as LibraryCardData,
                            uiScope = uiScope,
                            onCardShown = onCardShown,
                            onActionClick = onPrimaryCtaClick
                        ).id(it.uniqueId)
                            .addTo(this)
                    }

                    DynamicCardType.HOMEFEED_TYPE_BIGIMAGE_OLD_BTN -> {
                        BigRightImageOldBtnEpoxyModel(
                            libraryCardViewData = it as LibraryCardData,
                            uiScope = uiScope,
                            onCardShown = onCardShown,
                            onActionClick = onPrimaryCtaClick
                        ).id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.HOMEFEED_TYPE_FIVE -> {
                        TypeFiveEpoxyModel(
                            it as LibraryCardData,
                            uiScope,
                            onCardShown,
                            onPrimaryCtaClick
                        )
                            .id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.HOMEFEED_TYPE_EIGHT -> {
                        TypeEightEpoxyModel(
                            it as LibraryCardData,
                            uiScope,
                            onCardShown,
                            onPrimaryCtaClick
                        )
                            .id(it.featureType)
                            .addTo(this)
                    }

                    DynamicCardType.HOMEFEED_TYPE_NINE -> {
                        TypeNineEpoxyModel(
                            it as LibraryCardData,
                            uiScope,
                            onCardShown,
                            onPrimaryCtaClick
                        )
                            .id(it.featureType)
                            .addTo(this)
                    }

                    DynamicCardType.LARGE -> {
                        LargeCardEpoxyModel(
                            uiScope,
                            it as LibraryCardData,
                            onCardShown,
                            onPrimaryCtaClick,
                            onEndIconClick
                        )
                            .id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.MEDIUM -> {//automate_roundoff
                        /** If [MEDIUM]: add it as usual & move forward **/
                        MediumCardEpoxyModel(
                            uiScope,
                            it as LibraryCardData,
                            onCardShown,
                            onPrimaryCtaClick,
                            onEndIconClick
                        )
                            .id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.TICKET -> {
                        /** If [TICKET]: add it as usual & move forward **/
                        TicketCardEpoxyModel(
                            uiScope,
                            it as LibraryCardData,
                            onCardShown,
                            onPrimaryCtaClick
                        )
                            .id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.VIDEO -> {
                        /** If [VIDEO]: add it as usual & move forward **/
                        VideoCardEpoxyModel(
                            exoPlayer!!,
                            cacheDataSourceFactory!!,
                            scope,
                            it as LibraryCardData,
                            onCardShown,
                            onPrimaryCtaClick
                        )
                            .id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.STRIP_ONE -> {
                        /** If [STRIP_ONE]: add it as usual & move forward **/
                        StripOneCardEpoxyModel(
                            uiScope,
                            it as LibraryCardData,
                            onCardShown,
                            onPrimaryCtaClick
                        )
                            .id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.STRIP_TWO -> {
                        /** If [STRIP_TWO]: add it as usual & move forward **/
                        StripTwoCardEpoxyModel(
                            uiScope,
                            it as LibraryCardData,
                            onCardShown,
                            onPrimaryCtaClick
                        )
                            .id(it.uniqueId)
                            .addTo(this)

                    }

                    DynamicCardType.NONE -> {
                        /** If [NONE]: add it as usual & move forward **/
                        when (it) {
                            is CurrentGoldInvestmentCardData -> {
                                val firstCoinHomeScreenData = it.firstCoinData
                                CurrentGoldInvestmentCardEpoxyModel(
                                    item = it,
                                    uiScope = uiScope,
                                    festivalCampaignData = festivalCampaignData,
                                    firstCoinHomeScreenData = firstCoinHomeScreenData,
                                    onCardShown = onCardShown,
                                    onCurrentValueClick = onGoldBreakdownClick,
                                    onFirstCoinClick = onFirstCoinClick,
                                    quickActionsButtonData = it.quickActionsButtonData,
                                    onPrimaryCtaClick = onPrimaryCtaClick,
                                    onMagicHatNotchClick = onMagicHatNotchClick,
                                    invokeNextChallenge = invokeNextChallenge,
                                    onFirstTransactionAnimationEnd = onFirstTransactionAnimationEnd
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is SinglePageHomeFeedData -> {
                                it.singleHomeFeedCardMetaData?.let { cardData ->
                                    SinglePageHomeFeedEpoxyModel(
                                        uiScope = uiScope,
                                        singleHomeFeedCardMetaData = cardData,
                                        onCardShown = onCardShown,
                                        onPrimaryCtaClick = onPrimaryCtaClick,
                                        onShowMoreCtaClick = onShowMoreCtaClick
                                    )
                                        .id(it.uniqueId)
                                        .addTo(this)
                                }
                            }


                            is JarDuoData -> {
                                JarDuoEpoxyModelV2(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onPrimaryCtaClick,
                                    hasContactPermission
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is DetectedSpendData -> {
                                DetectedSpendCardEpoxyModel(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onDetectedSpendsInvestClick,
                                    onAmountClick,
                                    onPartPaymentClick
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is PaymentPromptData -> {
                                PaymentPromptEpoxyModel(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onPromptInvestClick
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is BannersData -> {
                                PartnerBannerEpoxyModel(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onClaimPartnerBonusClick,
                                    onShowAllPartnerBonusClick
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is LoanCardData -> {
                                LoanCardEpoxyModel(
                                    it,
                                    onCardShown,
                                    onEndIconClick,
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is VasooliCardData -> {
                                VasooliCardEpoxyModel(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onPrimaryCtaClick,
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is LendingProgressCardData -> {
                                LendingProgressCardEpoxyModel(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onPrimaryCtaClick,
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is LendingEligibilityCardData -> {
                                LendingEligibilityCardEpoxyModel(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onPrimaryCtaClick,
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is LendingSecondLoanCardData -> {
                                LendingSecondLoanCardEpoxyModel(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onPrimaryCtaClick,
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is RecommendedHomeCardData -> {
                                LendingReadyCashCardEpoxyModel(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onPrimaryCtaClick,
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is ImageCardCarouselData -> {
                                ImageCarouselEpoxyModel(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onPrimaryCtaClick,
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is UpdateDailySavingCardData -> {
                                UpdateDailySavingEpoxyModel(
                                    it,
                                    uiScope,
                                    onCardShown,
                                    onPrimaryCtaClick,
                                    onSliderMoved
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is GoldSipCard -> {
                                if (it.goldSipData?.subscriptionStatus.isNullOrEmpty())
                                    GoldSipCardEpoxyModel(
                                        it,
                                        uiScope,
                                        onCardShown,
                                        onPrimaryCtaClick,
                                    )
                                        .id(it.uniqueId)
                                        .addTo(this)
                                else
                                    PostSetupGoldSipCardEpoxyModel(
                                        it,
                                        uiScope,
                                        onCardShown,
                                        onGoldSipManualPaymentClicked
                                    )
                                        .id(it.uniqueId)
                                        .addTo(this)
                            }

                            is WeeklyMagicHomecardData -> {
                                WeeklyMagicHomecardEpoxyModel(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onPrimaryCtaClick
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is CouponCodeDiscoveryData -> {
                                val isSingleCouponFlow = it.data.size == 1
                                val couponCodeModels = it.data.map { couponCode ->
                                    CouponCodeDiscoveryCardEpoxyModel(
                                        uiScope,
                                        couponCode,
                                        it,
                                        isSingleCouponFlow,
                                        onCardShown,
                                        onPrimaryCtaClick
                                    )
                                        .id(it.uniqueId)
                                }

                                val id = it.data.joinToString { it.couponCode }
                                CustomLinearCarousalModel()
                                    .id(id)
                                    .initialPrefetchItemCount(it.data.size)
                                    .padding(
                                        Carousel.Padding.dp(
                                            0, 16
                                        )
                                    )
                                    .models(couponCodeModels)
                                    .numViewsToShowOnScreen(if (isSingleCouponFlow) 1.0f else 1.5f)
                                    .addTo(this)
                            }

                            is VibaHorizontalCard -> {
                                VibaCardEpoxyModel(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onVibaCardClick
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is HelpVideosData -> {
                                HelpVideosCardEpoxyModel(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onEndIconClick,
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is PreNotifyAutopayCardData -> {
                                PreNotifyAutopayEpoxyModel(
                                    it,
                                    uiScope,
                                    onCardShown,
                                    footerCtaClick,
                                    dismissCtaClick,
                                    neverShowAgainCtaClicked
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }

                            is com.jar.app.feature_homepage.shared.domain.model.DailySavingCardData -> {
                                DailySavingsV2EpoxyModel(
                                    uiScope,
                                    it,
                                    onCardShown,
                                    onPrimaryCtaClick,
                                )
                                    .id(it.uniqueId)
                                    .addTo(this)
                            }
                        }

                    }

                    DynamicCardType.HOMEFEED_TYPE_HORIZONTAL_LIST -> {
                        val it = it as HorizontalListCard
                        CustomLinearCarousalModel()
                            .id(it.uniqueId)
                            .initialPrefetchItemCount(it.cards.size)
                            .padding(
                                Carousel.Padding.dp(
                                    0, 12
                                )
                            )
                            .hasFixedSize(true)
                            .models(
                                getEpoxyModelForHorizontalList(
                                    it.cards,
                                    it.verticalPosition.orZero()
                                )
                            )
                            .numViewsToShowOnScreen(
                                when (it.cards.firstOrNull()?.getCardType()) {
                                    DynamicCardType.HOMEFEED_TYPE_ELEVEN -> if (it.cards.size > 1) 1.25f else 1f
                                    else -> if (it.cards.size > 2) 2.12f else 2.0f
                                }
                            )
                            .addTo(this)
                    }

                    DynamicCardType.HOMEFEED_TYPE_GRID -> {
                        val it = it as GridListCard
                        CustomGridCarousalModel()
                            .id(it.uniqueId)
                            .initialPrefetchItemCount(it.cards.size)
                            .hasFixedSize(true)
                            .padding(
                                Carousel.Padding.dp(
                                    0, 12
                                )
                            )
                            .models(getEpoxyModelForHorizontalList(it.cards))
                            .addTo(this)
                    }

                    else -> {
                        // Ignore..
                    }
                }
            }
        }
    }

    private fun getEpoxyModelForHorizontalList(
        cards: List<DynamicCard>,
        verticalPosition: Int = 0
    ): List<EpoxyModel<*>> {
        return cards.map {
            when (it.getCardType()) {
                DynamicCardType.SMALL -> {
                    SmallCardEpoxyModel(
                        uiScope,
                        it as LibraryCardData,
                        onCardShown,
                        onPrimaryCtaClick
                    ).id(it.uniqueId)
                }

                DynamicCardType.HOMEFEED_TYPE_ONE -> {
                    TypeOneEpoxyModel(
                        it as LibraryCardData,
                        uiScope,
                        onCardShown,
                        onPrimaryCtaClick
                    ).id(it.uniqueId)
                }

                DynamicCardType.HOMEFEED_TYPE_TWO -> {
                    TypeTwoEpoxyModel(
                        it as LibraryCardData,
                        uiScope,
                        onCardShown,
                        onPrimaryCtaClick
                    ).id(it.uniqueId)
                }

                DynamicCardType.HOMEFEED_TYPE_THREE -> {
                    TypeThreeEpoxyModel(
                        it as LibraryCardData,
                        uiScope,
                        onCardShown,
                        onPrimaryCtaClick
                    ).id(it.uniqueId)
                }

                DynamicCardType.HOMEFEED_TYPE_ELEVEN -> {
                    TypeElevenEpoxyModel(
                        it as LibraryCardData,
                        isSingleCard = cards.size == 1,
                        totalCards = cards.size,
                        verticalPosition = verticalPosition,
                        uiScope,
                        onCardShown,
                        onPrimaryCtaClick
                    ).id(it.uniqueId)
                }

                else -> throw IllegalArgumentException("Unsupported horizontalCard type")
            }
        }
    }

    private fun parseHorizontalTypeEPoxyModelAndGetData(model: EpoxyModel<View>?): LibraryCardData {
        return when (model) {
            is SmallCardEpoxyModel -> model.libraryCardViewData
            is TypeOneEpoxyModel -> model.libraryCardViewData
            is TypeTwoEpoxyModel -> model.libraryCardViewData
            is TypeThreeEpoxyModel -> model.libraryCardViewData
            else -> throw IllegalArgumentException("Unsupported Exception")
        }
    }
}
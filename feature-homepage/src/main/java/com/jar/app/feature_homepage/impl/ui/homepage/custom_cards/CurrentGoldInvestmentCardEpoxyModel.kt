package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.toSpannable
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.facebook.shimmer.ShimmerFrameLayout
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.base.util.setHtmlText
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.base.util.shakeAnimation
import com.jar.app.core_base.domain.model.GoldBalance
import com.jar.app.core_base.domain.model.GoldBalanceViewType
import com.jar.app.core_base.domain.model.JarWinningsFooterObject
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_ui.extension.animateViewLeftToRightFadeIn
import com.jar.app.core_ui.extension.animateViewLeftToRightFadeOut
import com.jar.app.core_ui.extension.animateViewRightToLeftFadeIn
import com.jar.app.core_ui.extension.animateViewRightToLeftFadeOut
import com.jar.app.core_ui.extension.animateViewTopToBottomFadeIn
import com.jar.app.core_ui.extension.animateViewTransform
import com.jar.app.core_ui.extension.animateViewWithFadeInAnimation
import com.jar.app.core_ui.extension.animateViewWithFadeOutAnimation
import com.jar.app.core_ui.extension.fadeOutInColors
import com.jar.app.core_ui.extension.getHtmlTextValue
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.updateDrawableTint
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import com.jar.app.core_ui.widget.button.ButtonType
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellGoldLockerBinding
import com.jar.app.feature_homepage.shared.domain.model.FestivalCampaignData
import com.jar.app.feature_homepage.shared.domain.model.QuickActionData
import com.jar.app.feature_homepage.shared.domain.model.current_investment.CurrentGoldInvestmentCardData
import com.jar.app.feature_homepage.shared.util.EventKey
import com.jar.app.feature_homepage.shared.util.HomeConstants
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.threeten.bp.DateTimeException
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.lang.ref.WeakReference

internal class CurrentGoldInvestmentCardEpoxyModel(
    private val item: CurrentGoldInvestmentCardData,
    private val festivalCampaignData: FestivalCampaignData,
    private val uiScope: CoroutineScope,
    private val firstCoinHomeScreenData: com.jar.app.feature_homepage.shared.domain.model.FirstCoinHomeScreenData?,
    private val quickActionsButtonData: List<QuickActionData>?,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onPrimaryCtaClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val onCurrentValueClick: (whichType: GoldBalanceViewType) -> Unit,
    private val onFirstCoinClick: (deepLink: String, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val onMagicHatNotchClick: () -> Unit,
    private val invokeNextChallenge: () -> Unit,
    private val onFirstTransactionAnimationEnd: () -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageCellGoldLockerBinding>(
        R.layout.feature_homepage_cell_gold_locker
    ), BaseResources {

    companion object {
        const val FIRST_TIME_DATA = "70_percent_5_years"
        const val FIRST_LOCKER = "First_Locker"
    }

    private var job: Job? = null
    private var timerJob: Job? = null
    private var firstTransactionViewJob: Job? = null
    private var shimmerLayout: ShimmerFrameLayout? = null
    private var animator = ArrayList<ObjectAnimator>()

    private val eventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to if (item.goldBalance.firstTransactionLockerDataObject != null) FIRST_LOCKER else item.cardType,
                DynamicCardEventKey.FeatureType to item.featureType,
                DynamicCardEventKey.Winnings_Status to item.goldBalance.jarWinningsFooter?.text.orEmpty().getHtmlTextValue().toString()
            )
        )
    }
    private var visibilityState: Int? = null

    private val celebrationLottieAnimationListener = ValueAnimator.AnimatorUpdateListener {
        val progress = (it.animatedValue.toString().toFloat().orZero() * 100)
        uiScope.launch {
            if (progress == 100f) {
                item.isFirstTimeAnimationCompleted = true
                binding?.lottieCelebration?.isVisible = false
                onFirstTransactionAnimationEnd.invoke()
            }
        }
    }

    private var binding: FeatureHomepageCellGoldLockerBinding? = null

    override fun bindItem(binding: FeatureHomepageCellGoldLockerBinding) {
        this.binding = binding
        shimmerLayout = binding.shimmerLayout
        val balanceViewType = item.goldBalance.getBalanceViewData()
        binding.root.setPlotlineViewTag(tag = item.featureType)
        binding.homeJarBalanceParent.setBackgroundResource(com.jar.app.core_ui.R.drawable.core_ui_rounded_black_bg_16dp)
        binding.tvMarquee.isSelected = true
        try {
            ContextCompat.getDrawable(
                binding.root.context,
                com.jar.app.core_ui.R.drawable.core_ui_ic_safe_gold_brinks
            )?.let {
                binding.tvMarquee.setCompoundDrawablesWithIntrinsicBounds(null, null, it, null)
            }
        } catch (_: Exception) {

        }
        setGoldText(
            item.goldBalance,
            balanceViewType,
            binding.tvCurrentValue,
            binding.tvSubtext
        )

        binding.tvCurrentValue.setDebounceClickListener {
            if (item.goldBalance.hasGold() || item.goldBalance.isGoldLeased())
                onCurrentValueClick.invoke(balanceViewType)
        }

        binding.infoButton.isVisible =
            (item.goldBalance.currentValue.orZero() > 0.0f || item.goldBalance.isGoldLeased())
        binding.infoButton.setDebounceClickListener {
            if (item.goldBalance.hasGold() || item.goldBalance.isGoldLeased())
                onCurrentValueClick.invoke(balanceViewType)
        }

        if (item.goldBalance.firstTransactionLockerDataObject != null) {
            //Animating First Transaction View
            if (item.isFirstTimeAnimationCompleted.not()) {
                binding.bgContainerView.isInvisible = false
                setViewForFirstTransaction()
                binding.expandableLayout.collapse(true)
            }
        } else {
            hideFirstTransactionViewAndShowGoldBalance(binding)
        }

        item.goldBalance.jarWinningsFooter?.let { jarWinningsFooter ->
            setJarWinningsFooterData(jarWinningsFooter)
        }
        if (festivalCampaignData.isFestivalCampaignEnabled) {
            binding.festivalCampaignGroup.isVisible = true

            val glide = Glide.with(binding.root.context)
            glide.load(festivalCampaignData.bannerImage).into(binding.ivFestivalCampaign)
            glide.load(festivalCampaignData.lampImage).into(binding.ivLampTopLeft)

        } else {
            binding.festivalCampaignGroup.isVisible = false
        }
    }

    private fun hideFirstTransactionViewAndShowGoldBalance(binding: FeatureHomepageCellGoldLockerBinding) {
        //Making First Transaction View Invisible
        binding.bgContainerView.isInvisible = true
        binding.tvTitle.isInvisible = true
        binding.tvDescription.isInvisible = true
        binding.ivBgFirstTransaction.isInvisible = true
        binding.btnFirstTransactionAction.isInvisible = true

        //Making Locker View Not Invisible
        if(festivalCampaignData.isFestivalCampaignEnabled.not()){
            binding.tvHelloUser.isInvisible = false
        }

        binding.btnWeeklyChallenge.isInvisible = false
        binding.clLockerContainer.isInvisible = false
        binding.clQuickAccess.isInvisible = false
        binding.clBottomDataContainer.isInvisible = false
        setLockerViewAndData()

        binding.btn1.setPlotlineViewTag(HomeConstants.HomeFeedCustomViewTag.lockerWithdrawal)
        binding.btn2.setPlotlineViewTag(HomeConstants.HomeFeedCustomViewTag.lockerSaveMore)
    }

    private fun setLockerViewAndData() {
        binding?.apply {
            if(festivalCampaignData.isFestivalCampaignEnabled){
                tvHelloUserFestival.text =
                    item.header?.convertToString(WeakReference(root.context))
            }else{
                tvHelloUser.text =
                    item.header?.convertToString(WeakReference(root.context))
            }

            setFirstCoinView()
            setQuickActionsVisibility(this)
            btnWeeklyChallenge.isInvisible = item.weeklyChallengeMetaData == null
            item.weeklyChallengeMetaData?.let {
                setWeeklyMagicNotchText(it)
            }
        }
    }

    private fun setViewForFirstTransaction() {
        binding?.apply {
            val firstTimeUserData = item.goldBalance.firstTransactionLockerDataObject!!
            Glide.with(root)
                .load(firstTimeUserData.backgroundImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .into(ivBgFirstTransaction)

            tvHeader.text = firstTimeUserData.header
            tvTitle.text = firstTimeUserData.title
            tvDescription.text = firstTimeUserData.subTitle

            firstTimeUserData.primaryTextColor?.let {
                val color = Color.parseColor(it)
                tvHeader.setTextColor(color)
                tvTitle.setTextColor(color)
            }
            firstTimeUserData.secondaryTextColor?.let {
                val color = Color.parseColor(it)
                tvDescription.setTextColor(color)
            }
            btnFirstTransactionAction.setText(firstTimeUserData.firstTransactionLockerCtaObject?.ctaText.orEmpty())
            if (item.goldBalance.firstTransactionLockerDataObject?.showGoldBalanceAnimation.orFalse()) {
//                setAnimationForFirstTransaction()
                directlyShowCongratulationsSection()
            } else {
                btnFirstTransactionAction.setPlotlineViewTag(HomeConstants.HomeFeedCustomViewTag.firstTransactionBottomContainer)
                btnFirstTransactionAction.shakeAnimation()
            }

            btnFirstTransactionAction.setDebounceClickListener {
                firstTimeUserData.firstTransactionLockerCtaObject?.ctaDeeplink?.let {
                    onPrimaryCtaClick.invoke(
                        PrimaryActionData(
                            type = PrimaryActionType.DEEPLINK,
                            value = it,
                            order = item.getSortKey(),
                            cardType = item.getCardType(),
                            featureType = item.featureType
                        ),
                        CardEventData(
                            mutableMapOf(
                                DynamicCardEventKey.FeatureType to item.featureType,
                                DynamicCardEventKey.CardType to FIRST_LOCKER,
                                DynamicCardEventKey.Data to item.goldBalance.firstTransactionLockerDataObject?.variant.orEmpty()
                            )
                        )
                    )
                }
            }
        }
    }

    private fun setAnimationForFirstTransaction() {
        binding?.apply {
            firstTransactionViewJob?.cancel()
            firstTransactionViewJob = uiScope.launch {
                delay(1000)
                tvHeader.animateViewRightToLeftFadeOut(500)
                tvTitle.animateViewRightToLeftFadeOut(500)
                tvDescription.animateViewRightToLeftFadeOut(500)
                ivBgFirstTransaction.animateViewLeftToRightFadeOut(500) {
                    bgContainerView.fadeOutInColors(
                        ContextCompat.getColor(
                            root.context, com.jar.app.core_ui.R.color.color_3A227C
                        ),
                        ContextCompat.getColor(
                            root.context, com.jar.app.core_ui.R.color.color_6038CE
                        ),
                        1000
                    )
                    clFirstTransactionSuccessContainer.animateViewTopToBottomFadeIn(
                        800,
                        onAnimationEnd = {
                            uiScope.launch {
                                delay(700)
                                dummyLockerView.isInvisible = false
                                animateFirstTimeTransactionCongratulations()
                            }
                        },
                        onAnimationUpdate = {}
                    )
                }
            }
        }
    }

    private fun directlyShowCongratulationsSection() {
        binding?.apply {
            ivBgFirstTransaction.isInvisible = true
            tvHeader.isInvisible = true
            tvTitle.isInvisible = true
            tvDescription.isInvisible = true
            btnFirstTransactionAction.isInvisible = true
            bgContainerView.fadeOutInColors(
                ContextCompat.getColor(
                    root.context, com.jar.app.core_ui.R.color.color_3A227C
                ),
                ContextCompat.getColor(
                    root.context, com.jar.app.core_ui.R.color.color_6038CE
                ),
                100
            )
            clFirstTransactionSuccessContainer.animateViewWithFadeInAnimation(100) {
                uiScope.launch {
                    delay(500)
                    animateFirstTimeTransactionCongratulations()
                }
            }
        }
    }

    private fun animateFirstTimeTransactionCongratulations() {
        binding?.apply {
            ivCheckmark.animateViewWithFadeOutAnimation(500)
            tvCongratsTitle.animateViewWithFadeOutAnimation(
                500
            )
            tvCongratsSubTitle.animateViewWithFadeOutAnimation(
                500
            )
            bgContainerView.animateViewTransform(
                dummyLockerView,
                600,
                onAnimationEnd = {
                    animateGoldLockerAfterFirstTransaction()
                },
                onAnimationUpdate = {
                    if (it == 0f) {
                        val drawable = ContextCompat.getDrawable(
                            root.context,
                            com.jar.app.core_ui.R.drawable.core_ui_rounded_black_bg_16dp
                        )
                        drawable?.updateDrawableTint(
                            ContextCompat.getColor(
                                root.context,
                                com.jar.app.core_ui.R.color.color_6038CE
                            )
                        )
                        bgContainerView.background = drawable
                    }
                    if (it <= 35f) {
                        lottieCelebration.playLottieWithUrlAndExceptionHandling(
                            root.context, BaseConstants.LottieUrls.CONFETTI_OPTIMISED
                        )
                        lottieCelebration.addAnimatorUpdateListener(
                            celebrationLottieAnimationListener
                        )
                    }
                }
            )
        }
    }

    private fun animateGoldLockerAfterFirstTransaction() {
        binding?.apply {
            setLockerViewAndData()
            dummyLockerView.animateViewWithFadeOutAnimation(500)
            clLockerContainer.animateViewWithFadeInAnimation(500)
            if(festivalCampaignData.isFestivalCampaignEnabled.not()) {
                tvHelloUser.animateViewLeftToRightFadeIn(500)
            }
            btn1.animateViewLeftToRightFadeIn(500)
            btnWeeklyChallenge.animateViewRightToLeftFadeIn(500)
            btn2.animateViewRightToLeftFadeIn(500) {
                clBottomDataContainer.isInvisible = false
                binding?.expandableLayout?.collapse(false)
                uiScope.launch {
                    delay(2000)
                    if (item.shouldRunShimmer)
                        startShimmer()
                    binding?.expandableLayout?.collapse(true)
                }
            }
        }
    }

    private fun setFirstCoinView() {
        binding?.apply {
            if (firstCoinHomeScreenData != null) {
                clFirstCoin.isVisible = true
                firstCoinAnimView.isVisible =
                    firstCoinHomeScreenData.deliveryStatus.isNullOrEmpty()
                firstCoinProgressBar.isVisible =
                    firstCoinHomeScreenData.deliveryStatus.isNullOrEmpty()
                if (firstCoinHomeScreenData.deliveryStatus.isNullOrEmpty()) {
                    firstCoinProgressBar.progress =
                        firstCoinHomeScreenData.percentageCompleted.toInt()
                } else {
                    firstCoinText.isVisible = true
                    firstCoinIcon.isVisible = true
                    firstCoinText.text = firstCoinHomeScreenData.header
                    Glide.with(root)
                        .load(firstCoinHomeScreenData.iconUrl)
                        .into(firstCoinIcon)
                }
                firstCoinArrow.setImageResource(com.jar.app.feature_daily_investment.R.drawable.feature_daily_investment_forward_arrow)
                clFirstCoin.setDebounceClickListener {
                    if (firstCoinHomeScreenData.deliveryStatus.isNullOrEmpty()) {
                        if (firstCoinHomeScreenData.onboarded) {
                            onFirstCoinClick.invoke(
                                BaseConstants.InternalDeepLinks.FIRST_COIN_PROGRESS + "/" + System.currentTimeMillis(),
                                CardEventData(
                                    mutableMapOf(
                                        DynamicCardEventKey.FeatureType to EventKey.First_coin_card,
                                        DynamicCardEventKey.Data to EventKey.See_Details,
                                        DynamicCardEventKey.CardType to ""
                                    )
                                )
                            )
                        } else {
                            onFirstCoinClick.invoke(
                                BaseConstants.InternalDeepLinks.FIRST_COIN_TRANSITION,
                                CardEventData(
                                    mutableMapOf(
                                        DynamicCardEventKey.FeatureType to EventKey.First_coin_card,
                                        DynamicCardEventKey.Data to EventKey.See_Details,
                                        DynamicCardEventKey.CardType to ""
                                    )
                                )
                            )
                        }
                    } else {
                        onFirstCoinClick.invoke(
                            "${BaseConstants.InternalDeepLinks.FIRST_COIN_DELIVERY}/${firstCoinHomeScreenData.deliveryOrderId}",
                            CardEventData(
                                mutableMapOf(
                                    DynamicCardEventKey.FeatureType to EventKey.First_coin_card,
                                    DynamicCardEventKey.Data to EventKey.See_Details,
                                    DynamicCardEventKey.CardType to ""
                                )
                            )
                        )
                    }
                }
                onCardShown.invoke(
                    CardEventData(
                        mutableMapOf(
                            DynamicCardEventKey.CardType to EventKey.First_coin_card,
                            DynamicCardEventKey.FeatureType to EventKey.First_coin_card
                        )
                    )
                )
            }
        }
    }

    private fun setQuickActionsVisibility(binding: FeatureHomepageCellGoldLockerBinding) {
        binding.clQuickAccess.isInvisible =
            quickActionsButtonData.isNullOrEmpty()
        if (!quickActionsButtonData.isNullOrEmpty()) {
            when (quickActionsButtonData.size) {
                0 -> {
                    binding.btn1.isVisible = false
                    binding.btn2.isVisible = false
                }

                1 -> {
                    binding.btn1.isInvisible = false
                    binding.btn1.setCustomButtonStyle(
                        if (quickActionsButtonData[0].isPrimary.orFalse())
                            ButtonType.primaryButton
                        else
                            ButtonType.secondaryHollowButton
                    )
                    binding.btn1.setText(quickActionsButtonData[0].title.toString())
                    binding.btn1.setOnClickListener {
                        quickActionsButtonData[0].deeplink?.let {
                            onPrimaryCtaClick.invoke(
                                PrimaryActionData(
                                    type = PrimaryActionType.DEEPLINK,
                                    value = it,
                                    order = item.getSortKey(),
                                    cardType = item.getCardType(),
                                    featureType = item.featureType
                                ),
                                CardEventData(
                                    mutableMapOf(
                                        DynamicCardEventKey.FeatureType to item.featureType,
                                        DynamicCardEventKey.CardType to item.cardType,
                                        DynamicCardEventKey.Data to quickActionsButtonData[0].title.orEmpty()
                                    )
                                )
                            )
                        }
                    }
                }

                2 -> {
                    binding.btn1.isInvisible = false
                    binding.btn2.isInvisible = false
                    binding.btn1.setCustomButtonStyle(
                        if (quickActionsButtonData[0].isPrimary.orFalse())
                            ButtonType.primaryButton
                        else
                            ButtonType.secondaryHollowButton
                    )
                    binding.btn1.setText(quickActionsButtonData[0].title.toString())
                    binding.btn1.setOnClickListener {
                        quickActionsButtonData[0].deeplink?.let {
                            onPrimaryCtaClick.invoke(
                                PrimaryActionData(
                                    type = PrimaryActionType.DEEPLINK,
                                    value = it,
                                    order = item.getSortKey(),
                                    cardType = item.getCardType(),
                                    featureType = item.featureType
                                ),
                                CardEventData(
                                    mutableMapOf(
                                        DynamicCardEventKey.FeatureType to item.featureType,
                                        DynamicCardEventKey.CardType to item.cardType,
                                        DynamicCardEventKey.Data to quickActionsButtonData[0].title.orEmpty()
                                    )
                                )
                            )
                        }
                    }
                    binding.btn2.setCustomButtonStyle(
                        if (quickActionsButtonData[1].isPrimary.orFalse())
                            ButtonType.primaryButton
                        else
                            ButtonType.secondaryHollowButton
                    )
                    binding.btn2.setText(quickActionsButtonData[1].title.toString())
                    binding.btn2.setOnClickListener {
                        quickActionsButtonData[1].deeplink?.let {
                            onPrimaryCtaClick.invoke(
                                PrimaryActionData(
                                    type = PrimaryActionType.DEEPLINK,
                                    value = it,
                                    order = item.getSortKey(),
                                    cardType = item.getCardType(),
                                    featureType = item.featureType
                                ),
                                CardEventData(
                                    mutableMapOf(
                                        DynamicCardEventKey.FeatureType to item.featureType,
                                        DynamicCardEventKey.CardType to item.cardType,
                                        DynamicCardEventKey.Data to quickActionsButtonData[1].title.orEmpty()
                                    )
                                )
                            )
                        }
                    }
                }

                else -> {
                    binding.btn1.isVisible = false
                    binding.btn2.isVisible = false
                }
            }
        }
    }


    private fun setGoldText(
        goldBalance: GoldBalance,
        goldBalanceViewType: GoldBalanceViewType?,
        tvCurrentValue: AppCompatTextView,
        tvSubtext: AppCompatTextView
    ) {
        val context = tvCurrentValue.context
        when (goldBalanceViewType) {
            GoldBalanceViewType.ONLY_GM -> {
                tvCurrentValue.text = tvCurrentValue.context.getString(
                    R.string.feature_homepage_total_savings
                )
                tvSubtext.text = goldBalance.getGoldVolumeWithUnit()
            }

            GoldBalanceViewType.ONLY_RS -> {
                tvCurrentValue.text = tvCurrentValue.context.getString(
                    R.string.feature_homepage_total_savings
                )
                tvSubtext.text = getCustomStringFormatted(
                    context,
                    MR.strings.feature_buy_gold_v2_currency_sign_x_string,
                    (goldBalance.investedValue
                        ?: goldBalance.currentValue.orZero()).getFormattedAmount()
                )
            }

            GoldBalanceViewType.RS_ND_GM -> {
                tvCurrentValue.text = goldBalance.getGoldVolumeWithUnit()
                tvSubtext.text = getCustomStringFormatted(
                    context,
                    MR.strings.feature_buy_gold_v2_currency_sign_x_string,
                    (goldBalance.investedValue
                        ?: goldBalance.currentValue.orZero()).getFormattedAmount()
                )
            }

            GoldBalanceViewType.GM_ND_RS -> {
                tvCurrentValue.text = getCustomStringFormatted(
                    context,
                    MR.strings.feature_buy_gold_v2_currency_sign_x_string,
                    (goldBalance.investedValue
                        ?: goldBalance.currentValue.orZero()).getFormattedAmount()
                )
                tvSubtext.text = goldBalance.getGoldVolumeWithUnit()
            }

            else -> {
                tvCurrentValue.text = getCustomStringFormatted(
                    context,
                    MR.strings.feature_buy_gold_v2_currency_sign_x_string,
                    (goldBalance.investedValue
                        ?: goldBalance.currentValue.orZero()).getFormattedAmount()
                )
                tvSubtext.text = goldBalance.getGoldVolumeWithUnit()
            }
        }
    }


    private fun setWeeklyMagicNotchText(data: WeeklyChallengeMetaData) {
        binding?.apply {
            magicHatFabLottie.playLottieWithUrlAndExceptionHandling(
                root.context,
                BaseConstants.LottieUrls.WEEKLY_CHALLENGE_IDLE_STATE
            )
            magicHatFabTextTwo.isVisible = true
            when {
                data.userOnboarded == false -> {
                    magicHatFabTextOne.setTypeface(
                        magicHatFabTextOne.typeface,
                        Typeface.BOLD
                    )
                    magicHatFabTextOne.text =
                        getCustomString(
                            root.context,
                            com.jar.app.feature_weekly_magic_common.shared.MR.strings.feature_weekly_magic_common_magic_hat
                        )
                    magicHatFabTextTwo.text =
                        getCustomString(
                            root.context,
                            com.jar.app.feature_weekly_magic_common.shared.MR.strings.feature_weekly_magic_common_is_here
                        )
                }

                data.totalCards.orZero() != 0 && data.cardsWon.orZero() < data.totalCards.orZero() -> {
                    val fabOneText = buildSpannedString {
                        color(
                            ContextCompat.getColor(
                                root.context, com.jar.app.core_ui.R.color.white
                            )
                        ) {
                            append(
                                getCustomString(
                                    root.context,
                                    com.jar.app.feature_weekly_magic_common.shared.MR.strings.feature_weekly_magic_common_you_got
                                )
                            )
                        }
                    }.toSpannable()
                    magicHatFabTextOne.text = fabOneText

                    val fabTwoText = buildSpannedString {
                        bold {
                            color(
                                ContextCompat.getColor(
                                    root.context, com.jar.app.core_ui.R.color.color_EBB46A
                                )
                            ) {
                                append(
                                    getCustomStringFormatted(
                                        root.context,
                                        com.jar.app.feature_weekly_magic_common.shared.MR.strings.feature_weekly_magic_common_n_n_,
                                        data.cardsWon.orZero(),
                                        data.totalCards.orZero()
                                    )
                                )
                                append(
                                    " "
                                )
                            }
                        }.append(
                            getCustomString(
                                root.context,
                                com.jar.app.feature_weekly_magic_common.shared.MR.strings.feature_weekly_magic_common_cards
                            )
                        )
                    }.toSpannable()
                    magicHatFabTextTwo.text = fabTwoText
                }

                data.challengeCompletedViewed == false -> {
                    magicHatFabTextOne.setTypeface(
                        magicHatFabTextOne.typeface,
                        Typeface.BOLD
                    )
                    magicHatFabTextOne.text =
                        getCustomString(
                            root.context,
                            com.jar.app.feature_weekly_magic_common.shared.MR.strings.feature_weekly_magic_common_you_won
                        )
                    magicHatFabTextTwo.isVisible = false
                    magicHatFabTextTwo.text = ""
                }

                data.challengeCompletedViewed == true -> {
                    magicHatFabTextOne.setTypeface(
                        magicHatFabTextOne.typeface,
                        Typeface.BOLD
                    )
                    magicHatFabTextOne.text = data.currentWeekNumber?.let {
                        getCustomStringFormatted(
                            root.context,
                            com.jar.app.feature_weekly_magic_common.shared.MR.strings.feature_weekly_magic_common_week_n,
                            (it + 1).toString()
                        )
                    } ?: root.context.getString(com.jar.app.core_ui.R.string.core_ui_next)
                    magicHatFabTextTwo.text = setNextChallengeDate(data)
                }

                else -> {
                    magicHatFabTextOne.text = ""
                    magicHatFabTextTwo.text = ""
                }
            }

            btnWeeklyChallenge.setDebounceClickListener {
                onMagicHatNotchClick.invoke()
            }
        }
    }

    private fun setNextChallengeDate(data: WeeklyChallengeMetaData?): String {
        if (binding != null) {
            try {
                Instant.parse(data?.nextChallengeStartDate)?.let {
                    val today = Instant.now().atZone(ZoneId.systemDefault()).toEpochSecond()
                    val endDay = ZonedDateTime.parse(data?.nextChallengeStartDate).toEpochSecond()
                    val daysBetween = (endDay - today) / (60 * 60 * 24)
                    if (daysBetween < 0) {
                        return ""
                    } else if (daysBetween < 1) {
                        val timeStamp = (endDay - today) * 1000
                        setNextChallengeTimer(timeStamp)
                        return timeStamp.milliSecondsToCountDown(true)
                    }
                    return "${
                        getCustomString(
                            binding!!.root.context,
                            com.jar.app.feature_weekly_magic_common.shared.MR.strings.feature_weekly_magic_common_in_
                        )
                    } ${
                        if (daysBetween < 2) {
                            "1 ${
                                getCustomString(
                                    binding!!.root.context,
                                    com.jar.app.feature_weekly_magic_common.shared.MR.strings.feature_weekly_magic_common_day
                                )
                            }"
                        } else {
                            "$daysBetween ${
                                getCustomString(
                                    binding!!.root.context,
                                    com.jar.app.feature_weekly_magic_common.shared.MR.strings.feature_weekly_magic_common_days
                                )
                            }"
                        }
                    }"
                }
            } catch (e: DateTimeException) {

            }
            return getCustomString(
                binding!!.root.context,
                com.jar.app.feature_weekly_magic_common.shared.MR.strings.feature_weekly_magic_common_you_won
            )
        } else {
            return ""
        }
    }

    private fun setNextChallengeTimer(timeStamp: Long) {
        binding?.apply {
            timeStamp.takeIf { it > 0L }?.let { validity ->
                magicHatFabTextTwo.isVisible = true
                checkAndCancelTimerJob()
                timerJob = uiScope.countDownTimer(
                    validity,
                    onInterval = {
                        magicHatFabTextTwo.text =
                            it.milliSecondsToCountDown(true)
                    },
                    onFinished = {
                        invokeNextChallenge.invoke()
                    }
                )
                timerJob?.start()
            } ?: kotlin.run {
                magicHatFabTextTwo.isVisible = false
            }
        }
    }

    private fun setJarWinningsFooterData(jarWinningsFooter: JarWinningsFooterObject) {
        binding?.apply {
            groupJarWinningsFooter.isVisible = jarWinningsFooter.text.isNullOrEmpty().not()
            tvJarWinningsFooter.setHtmlText(jarWinningsFooter.text.orEmpty())
            ivJarWinningsFooter.isVisible = jarWinningsFooter.iconUrl.isNullOrEmpty().not()
            jarWinningsFooter.iconUrl?.takeIf { it.isNotEmpty() }?.let { iconUrl ->
                Glide.with(root).load(iconUrl).into(ivJarWinningsFooter)
            }
        }
    }


    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            this.visibilityState = visibilityState
            if (item.goldBalance.firstTransactionLockerDataObject == null)
                uiScope.launch {
                    delay(2000)
                    binding?.expandableLayout?.collapse(true)
                }
            startShowEventJob(
                uiScope,
                isCardFullyVisible = {
                    this.visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE
                },
                onCardShownEvent = {
                    if (item.goldBalance.firstTransactionLockerDataObject != null)
                        eventData.map[DynamicCardEventKey.Data] =
                            item.goldBalance.firstTransactionLockerDataObject?.variant.orEmpty()
                    onCardShown.invoke(eventData)
                }
            )
            if (item.shouldRunShimmer && item.goldBalance.firstTransactionLockerDataObject == null)
                startShimmer()
        } else if (visibilityState == VisibilityState.INVISIBLE) {
            stopShimmer()
        } else if (visibilityState == VisibilityState.PARTIAL_IMPRESSION_INVISIBLE) {
            stopShimmer()
        }
    }

    override fun getBinding(view: View): FeatureHomepageCellGoldLockerBinding {
        return FeatureHomepageCellGoldLockerBinding.bind(view)
    }

    private fun checkAndCancelTimerJob() {
        if (timerJob?.isActive == true) {
            timerJob?.cancel()
            timerJob = null
        }
    }

    private fun startShimmer() {
        job?.cancel()
        job = uiScope.launch {
            delay(1000)
            if (isActive) {
                shimmerLayout?.showShimmer(false)
                shimmerLayout?.startShimmer()
            }
        }
    }

    private fun stopShimmer() {
        job?.cancel()
        shimmerLayout?.stopShimmer()
        shimmerLayout?.hideShimmer()
        shimmerLayout?.clearAnimation()
        firstTransactionViewJob?.cancel()
        checkAndCancelTimerJob()
        item.isFirstTimeAnimationCompleted = true
    }
}
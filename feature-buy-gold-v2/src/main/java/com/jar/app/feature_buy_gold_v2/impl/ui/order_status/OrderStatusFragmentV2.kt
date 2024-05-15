package com.jar.app.feature_buy_gold_v2.impl.ui.order_status

import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.LottieDrawable
import com.app.feature_in_app_review.util.InAppReviewUtil
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.RefreshUserGoldBalanceEvent
import com.jar.app.base.data.event.RefreshWeeklyChallengeMetaEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.base.util.orFalse
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_analytics.EventKey.TransactionsV2.paramters.button_type
import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.isAlreadyInflated
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.winnings.customAnimations.MysteryCardAnimation
import com.jar.app.core_ui.winnings.customAnimations.SpinsAnimation
import com.jar.app.core_ui.winnings.customAnimations.WeeklyMagicAnimation
import com.jar.app.core_base.domain.model.WinningsType
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.FeaturePendingFailureV2Binding
import com.jar.app.feature_buy_gold_v2.databinding.FeatureSuccessTransactionV2Binding
import com.jar.app.feature_buy_gold_v2.databinding.FragmentOrderStatusV2Binding
import com.jar.app.feature_buy_gold_v2.impl.ui.order_status.custom_card.WeeklyMagicNew
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.domain.event.RewardsEventKey
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldInputData
import com.jar.app.feature_buy_gold_v2.shared.domain.model.OrderStatusModel
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2Constants
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey.Clicked_Refresh_OrderProcessingScreen
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey.Clicked_TryAgain_OrderProcessingScreen
import com.jar.app.feature_one_time_payments_common.shared.BuyGoldCrossPromotionInfographicType
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus
import com.jar.app.weekly_magic_common.api.WeeklyChallengeCommonApi
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class OrderStatusFragmentV2 : BaseFragment<FragmentOrderStatusV2Binding>() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    @Inject
    lateinit var weeklyChallengeCommonApi: WeeklyChallengeCommonApi

    @Inject
    lateinit var inAppReviewUtil: InAppReviewUtil

    private val args by navArgs<OrderStatusFragmentV2Args>()

    private val orderStatusModel by lazy {
        serializer.decodeFromString<OrderStatusModel?>(
            decodeUrl(args.orderStatusModelString.orEmpty())
        )
    }

    private val viewModelProvider: OrderStatusViewModelAndroid by viewModels()

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        }

    private var successBinding: FeatureSuccessTransactionV2Binding? = null
    private var failureBinding: FeaturePendingFailureV2Binding? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOrderStatusV2Binding
        get() = FragmentOrderStatusV2Binding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        getData()
        observeLiveData()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchManualPaymentResponseLiveData.collect(
                    onSuccess = {
                        if (it.showInAppRating == true)
                            inAppReviewUtil.showInAppReview(requireActivity())
                        checkForWeeklyChallenge(it)
                        it?.let {
                            viewModel.fetchManualPaymentStatusResponse = it
                        }
                        setViewsAccordingToStatusType(it)
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }

    private fun setViewsAccordingToStatusType(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse) {
        when (fetchManualPaymentStatusResponse.getManualPaymentStatus()) {
            ManualPaymentStatus.SUCCESS -> fetchManualPaymentStatusResponse.getManualPaymentGoldStatus()
                ?.let {
                    when (it) {
                        ManualPaymentStatus.SUCCESS -> setOrderSuccessView()
                        ManualPaymentStatus.FAILURE -> setOrderProcessingView()
                        ManualPaymentStatus.PENDING -> setOrderProcessingView(true)
                    }
                } ?: kotlin.run {
                setOrderSuccessView()
            }

            ManualPaymentStatus.FAILURE -> {
                setOrderFailureView()
            }

            ManualPaymentStatus.PENDING -> {
                return when (fetchManualPaymentStatusResponse.paymentProvider) {
                    OneTimePaymentGateway.JUSPAY.name -> setOrderFailureView()
                    OneTimePaymentGateway.PAYTM.name -> setOrderProcessingView()
                    else -> setOrderProcessingView()
                }
            }
        }
    }

    private fun setOrderSuccessView() {
        with(binding) {
            // check if it is a refresh result, we can do it by checking pending view is inflared or not
            if (failureBinding != null) {
                failureBinding?.root?.visibility = View.GONE
            }
            if (successScreen.isAlreadyInflated().not()) {
                val view = successScreen.inflate()
                successBinding = FeatureSuccessTransactionV2Binding.bind(view)
                successBinding?.apply {
                    downloadIn.paint.isUnderlineText = true

                    successLottie.apply {
                        repeatCount = LottieDrawable.INFINITE
                        playLottieWithUrlAndExceptionHandling(
                            requireContext(),
                            BaseConstants.LottieUrls.SMALL_CHECK
                        )
                    }

                    animConfetti.apply {
                        playLottieWithUrlAndExceptionHandling(
                            requireContext(),
                            BaseConstants.LottieUrls.CONFETTI_FROM_TOP
                        )
                    }

                    // setting the buy text
                    viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.let { oneTimeInvestment ->
                        val amountGramString = getCustomStringFormatted(
                            MR.strings.feature_buy_gold_v2_amount_and_quantity,
                            oneTimeInvestment.goldVolume,
                            oneTimeInvestment.totalAmt,
                        )
                        successBinding?.gm?.text = amountGramString
                    }

                    // setting up rewards recycler view
                    setUpRewardsCards()

                    // setting up click listener
                    setupListeners()

                    //setting up the download invoice
                    with(downloadIn) {
                        movementMethod = LinkMovementMethod()
                        setDebounceClickListener {
                            analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_ViewInvoice_OrderSuccessScreen)
                            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.invoiceLink?.let {
                                webPdfViewerApi.openPdf(it)
                            }
                        }
                    }
                    //sending the events
                    sendSuccessViewVisibleEvent()
                    EventBus.getDefault().postSticky(RefreshUserGoldBalanceEvent())

                    //Setup Cross Promotion Banner
                    viewModel.fetchManualPaymentStatusResponse?.postOrderCrossSellCard?.let { crossPromotionData ->
                        if (crossPromotionData.getInfographicType() == BuyGoldCrossPromotionInfographicType.FULL_IMAGE) {
                            layoutCrossPromotionBanner.ivFullImageBanner.isVisible = true
                            Glide.with(requireContext()).load(crossPromotionData.infographicUrl).into(layoutCrossPromotionBanner.ivFullImageBanner)
                            layoutCrossPromotionBanner.ivFullImageBanner.setDebounceClickListener {
                                crossPromotionBannerClicked(deepLink = crossPromotionData.ctaDeeplink, cardType = crossPromotionData.cardType)
                            }
                        } else {
                            layoutCrossPromotionBanner.groupCard.isVisible = true
                            layoutCrossPromotionBanner.tvCrossBannerTitle.setHtmlText(crossPromotionData.primaryText.orEmpty())
                            layoutCrossPromotionBanner.tvCrossBannerDescription.isVisible = crossPromotionData.secondaryText != null
                            layoutCrossPromotionBanner.tvCrossBannerDescription.setHtmlText(crossPromotionData.secondaryText.orEmpty())
                            layoutCrossPromotionBanner.btnCta.setText(crossPromotionData.primaryCtaText.orEmpty())
                            crossPromotionData.ctaBackgroundColor?.takeIf { it.isNotEmpty() }?.let {
                                layoutCrossPromotionBanner.btnCta.setBackGroundColor(Color.parseColor(it))
                            }
                            crossPromotionData.backgroundColor?.takeIf { it.isNotEmpty() }?.let {
                                layoutCrossPromotionBanner.cvRoot.setCardBackgroundColor(Color.parseColor(it))
                            }
                            if (crossPromotionData.getInfographicType() == BuyGoldCrossPromotionInfographicType.IMAGE) {
                                Glide.with(requireContext()).load(crossPromotionData.infographicUrl).into(layoutCrossPromotionBanner.ivCrossBannerIcon)
                                layoutCrossPromotionBanner.ivCrossBannerIcon.isVisible = true
                            } else {
                                layoutCrossPromotionBanner.lottieCrossBanner.playLottieWithUrlAndExceptionHandling(requireContext(), crossPromotionData.infographicUrl)
                                layoutCrossPromotionBanner.lottieCrossBanner.isVisible = true
                            }
                            layoutCrossPromotionBanner.btnCta.setDebounceClickListener {
                                crossPromotionBannerClicked(deepLink = crossPromotionData.ctaDeeplink, cardType = crossPromotionData.cardType)
                            }
                        }
                        analyticsHandler.postEvent(
                            BuyGoldV2EventKey.Shown_PostOrder_CrossSellBanner,
                            mapOf(BuyGoldV2EventKey.BannerType to crossPromotionData.cardType)
                        )
                        layoutCrossPromotionBanner.root.isVisible = true
                    } ?: kotlin.run {
                        layoutCrossPromotionBanner.root.isVisible = false
                    }

                }
            }
        }
    }

    private fun crossPromotionBannerClicked(deepLink: String, cardType: String) {
        analyticsHandler.postEvent(
            BuyGoldV2EventKey.Clicked_PostOrder_CrossSellBanner,
            mapOf(BuyGoldV2EventKey.BannerType to cardType)
        )
        popBackStack()
        EventBus.getDefault().post(HandleDeepLinkEvent(deepLink, fromScreen = BuyGoldV2Constants.BUY_GOLD_CROSS_PROMOTION))
    }

    private fun sendSuccessViewVisibleEvent() {
        val goldAmt =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldAmt.orZero()
        val goldVolume =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldVolume ?: ""
        val isAuspicious =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.auspiciousTime.orFalse()
        val value: String =
            when (viewModel.fetchManualPaymentStatusResponse?.postPaymentRewardCardList?.size) {
                1 -> {
                    val rewardAtSecPos =
                        viewModel.fetchManualPaymentStatusResponse?.postPaymentRewardCardList?.get(0)?.animationType
                    when (rewardAtSecPos?.let { WinningsType.getWinningsType(it) }) {
                        WinningsType.MYSTERY_CARDS -> EventKey.AutopayBottomSheet.OnlMysteryCard
                        WinningsType.WEEKLY_MAGIC -> EventKey.AutopayBottomSheet.OnlyWeeklyMagic
                        WinningsType.WEEKLY_MAGIC_NEW -> EventKey.AutopayBottomSheet.OnlyWeeklyMagicNew
                        else -> EventKey.AutopayBottomSheet.NoMysteryCard
                    }
                }

                2 -> {
                    val rewardAtSecPos =
                        viewModel.fetchManualPaymentStatusResponse?.postPaymentRewardCardList?.get(1)?.animationType
                    when (rewardAtSecPos?.let { WinningsType.getWinningsType(it) }) {
                        WinningsType.MYSTERY_CARDS -> EventKey.AutopayBottomSheet.MysteryCard
                        WinningsType.WEEKLY_MAGIC -> EventKey.AutopayBottomSheet.WeeklymagicWon
                        WinningsType.WEEKLY_MAGIC_NEW -> EventKey.AutopayBottomSheet.WeeklyMagicNew
                        else -> ""
                    }
                }

                else -> "NOTHING"
            }
        analyticsHandler.postEvent(
            BuyGoldV2EventKey.Shown_OrderSuccessScreen,
            mapOf(
                BuyGoldV2EventKey.Amount to goldAmt,
                BuyGoldV2EventKey.Quantity to goldVolume,
                BuyGoldV2EventKey.IsAuspicious to isAuspicious,
                BuyGoldV2EventKey.Flow to orderStatusModel?.paymentFlowSource.orEmpty(),
                BuyGoldV2EventKey.State to value
            )
        )
    }

    private fun setOrderProcessingView(isGoldProcessing: Boolean = false) {
        with(binding) {
            if (nonSuccessScreen.isAlreadyInflated().not()) {
                val view = nonSuccessScreen.inflate()
                failureBinding = FeaturePendingFailureV2Binding.bind(view)
                failureBinding?.apply {
                    analyticsHandler.postEvent(
                        BuyGoldV2EventKey.Shown_OrderProcessingScreen,
                        mapOf(BuyGoldV2EventKey.Flow to orderStatusModel?.paymentFlowSource.orEmpty())
                    )
                    buttonRefreshOrTryAgain.setText(
                        getCustomString(MR.strings.feature_buy_gold_refresh)
                    )
                    tvContactUs.paint.isUnderlineText = true
                    itIsTakin.text = if (isGoldProcessing) {
                        getCustomString(MR.strings.feature_buy_gold_v2_processing_reason_sub_message)
                    } else {
                        getCustomString(MR.strings.feature_buy_gold_v2_processing_reason_sub_message)
                    }
                    successLottie.setImageResource(R.drawable.feature_buy_gold_v2_ic_payment_processing)
                    val amount =
                        viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.totalAmt
                            ?: viewModel.fetchManualPaymentStatusResponse?.amount.orZero()
                    val amountGramString = getCustomStringFormatted(
                        MR.strings.feature_buy_gold_v2_we_are_trying_message, amount.toFloat()
                    )
                    failureBinding?.weAreTryi?.text = amountGramString
                    setClickListenersForPendingSuccessScreen()
                }
            }
        }
    }

    private fun setClickListenersForPendingSuccessScreen() {
        failureBinding?.apply {
            btnGoToHome.setDebounceClickListener {
                handleBackPress()
            }

            buttonRefreshOrTryAgain.setDebounceClickListener {
                analyticsHandler.postEvent(
                    Clicked_Refresh_OrderProcessingScreen
                )
                getData()
            }

            contactSup.setDebounceClickListener {
                contactUs()
            }
        }
    }

    private fun setOrderFailureView() {
        with(binding) {
            if (nonSuccessScreen.isAlreadyInflated().not()) {
                val view = nonSuccessScreen.inflate()
                failureBinding = FeaturePendingFailureV2Binding.bind(view)
            }
            failureBinding?.apply {
                analyticsHandler.postEvent(
                    BuyGoldV2EventKey.Shown_OrderProcessingScreen,
                    mapOf(BuyGoldV2EventKey.Flow to orderStatusModel?.paymentFlowSource.orEmpty())
                )
                tvContactUs.paint.isUnderlineText = true
                itIsTakin.text =
                    getCustomString(MR.strings.feature_buy_gold_v2_failure_reason_sub_message)
                successLottie.setImageResource(R.drawable.feature_buy_gold_v2_ic_gold_payment_failed)
                viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.let { oneTimeInvestment ->
                    val amountGramString = getCustomStringFormatted(
                        MR.strings.feature_buy_gold_v2_failure_reason_main_message,
                        oneTimeInvestment.totalAmt,
                    )
                    failureBinding?.weAreTryi?.text = amountGramString
                }

                if (orderStatusModel?.isFromBuyGoldFlow().orFalse()) {
                    buttonRefreshOrTryAgain.setText(
                        getCustomString(MR.strings.try_again)
                    )
                } else {
                    buttonRefreshOrTryAgain.isVisible = false
                }

                val goldAmt =
                    viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldAmt.orZero()
                val goldVolume =
                    viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldVolume
                        ?: ""
                val isAuspicious =
                    viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.auspiciousTime.orFalse()

                analyticsHandler.postEvent(
                    BuyGoldV2EventKey.Shown_OrderFailureScreen,
                    mapOf(
                        BuyGoldV2EventKey.Amount to goldAmt,
                        BuyGoldV2EventKey.Quantity to goldVolume,
                        BuyGoldV2EventKey.IsAuspicious to isAuspicious,
                        BuyGoldV2EventKey.Flow to orderStatusModel?.paymentFlowSource.orEmpty()
                    )
                )
                EventBus.getDefault().postSticky(RefreshUserGoldBalanceEvent())
                setClickListenersForFailureSuccessScreen()
            }
        }
    }

    private fun setClickListenersForFailureSuccessScreen() {
        failureBinding?.apply {
            btnGoToHome.setDebounceClickListener {
                handleBackPress()
            }

            buttonRefreshOrTryAgain.setDebounceClickListener {
                analyticsHandler.postEvent(
                    Clicked_TryAgain_OrderProcessingScreen
                )
                viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.let { oneTimeInvestment ->
                    navigateTo(
                        "android-app://com.jar.app/buyGoldV2/${
                            encodeUrl(
                                serializer.encodeToString(
                                    BuyGoldInputData(
                                        prefilledAmountForFailedState = oneTimeInvestment.totalAmt.orZero()
                                            .toFloat()
                                    )
                                )
                            )
                        }/${orderStatusModel?.buyGoldFlowContext}",
                        popUpTo = R.id.orderStatusFragmentV2,
                        inclusive = true
                    )
                }
            }

            contactSup.setDebounceClickListener {
                contactUs()
            }
        }
    }

    private fun setupListeners() {
        successBinding?.apply {
            downloadIn.setDebounceClickListener {
                analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_ViewInvoice_OrderSuccessScreen)
                viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.invoiceLink?.let {
                    webPdfViewerApi.openPdf(it)
                }
            }
            dowloadIcon.setDebounceClickListener {
                analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_ViewInvoice_OrderSuccessScreen)
                viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.invoiceLink?.let {
                    webPdfViewerApi.openPdf(it)
                }
            }
            btnGoToHome.setDebounceClickListener {
                navigateTo(
                    uri = BaseConstants.InternalDeepLinks.HOME,
                    popUpTo = R.id.orderStatusFragmentV2,
                    inclusive = true
                )
            }
        }
    }

    private fun setUpRewardsCards() {
        successBinding?.apply {
            if (viewModel.fetchManualPaymentStatusResponse?.postPaymentRewardCardList.isNullOrEmpty()) {
                yourReward.visibility = View.GONE
                lineSeprator.visibility = View.GONE
            }
            viewModel.fetchManualPaymentStatusResponse?.postPaymentRewardCardList?.forEach { postPaymentCard ->
                val animationType = postPaymentCard.animationType
                when (animationType?.let { it1 -> WinningsType.getWinningsType(it1) }) {
                    WinningsType.SPINS -> {
                        val spins = SpinsAnimation(binding.root.context, true).apply {
                            setData(
                                postPaymentCard
                            )
                            setOnClickListener {
                                postPaymentCard.let {
                                    analyticsHandler.postEvent(
                                        EventKey.Clicked_dynamicCard,
                                        mapOf(
                                            DynamicCardEventKey.FeatureType to EventKey.AutopayBottomSheet.Spin_Count,
                                            DynamicCardEventKey.Data to it.toString()
                                        )
                                    )
                                }
                                popBackStack()
                                EventBus.getDefault().post(postPaymentCard.deepLink?.let { it1 ->
                                    HandleDeepLinkEvent(
                                        it1
                                    )
                                })
                            }
                        }
                        rewardLL.addView(spins)
                    }

                    WinningsType.MYSTERY_CARDS -> {
                        val mysteryCard = MysteryCardAnimation(binding.root.context, true).apply {
                            setData(postPaymentCard)
                            val marginParam = this.layoutParams as? ViewGroup.MarginLayoutParams
                            marginParam?.marginStart = 100
                            setOnClickListener {
                                postPaymentCard.let {
                                    analyticsHandler.postEvent(
                                        EventKey.Clicked_dynamicCard,
                                        mapOf(
                                            DynamicCardEventKey.FeatureType to it.animationType.toString(),
                                            DynamicCardEventKey.Data to it.toString()
                                        )
                                    )
                                }
                                popBackStack()
                                EventBus.getDefault().post(postPaymentCard.deepLink?.let { it1 ->
                                    HandleDeepLinkEvent(
                                        it1
                                    )
                                })
                            }
                        }
                        rewardLL.addView(mysteryCard)
                    }

                    WinningsType.WEEKLY_MAGIC -> {
                        val weeklyMagic = WeeklyMagicAnimation(binding.root.context, true).apply {
                            setData(postPaymentCard)

                            val marginParam = this.layoutParams as? ViewGroup.MarginLayoutParams
                            marginParam?.marginStart = 100
                            setOnClickListener {
                                postPaymentCard.let {
                                    analyticsHandler.postEvent(
                                        EventKey.Clicked_dynamicCard,
                                        mapOf(
                                            DynamicCardEventKey.FeatureType to it.animationType.toString(),
                                            DynamicCardEventKey.Data to it.toString()
                                        )
                                    )
                                }
                                popBackStack()
                                EventBus.getDefault().post(postPaymentCard.deepLink?.let { it1 ->
                                    HandleDeepLinkEvent(
                                        it1
                                    )
                                })
                            }
                        }
                        rewardLL.addView(weeklyMagic)
                    }

                    WinningsType.WEEKLY_MAGIC_NEW -> {
                        val weeklyMagicNew = WeeklyMagicNew(binding.root.context).apply {
                            setData(postPaymentCard, onButtonClicked = { deeplink ->
                                analyticsHandler.postEvent(
                                    RewardsEventKey.Clicked_WMPostOrderScreen,
                                    mapOf(
                                        button_type to postPaymentCard.ctaText.orEmpty(),
                                        RewardsEventKey.cards_left to ((postPaymentCard.targetCards
                                            ?: 0) - postPaymentCard.cardsWon.orZero()),
                                        RewardsEventKey.days_left to postPaymentCard.daysLeft.toString(),
                                        RewardsEventKey.target_cards to postPaymentCard.targetCards.orZero()
                                    )
                                )
                                EventBus.getDefault().post(HandleDeepLinkEvent(deeplink))
                                popBackStack()
                            })
                        }
                        postPaymentCard.cardsWon?.let { it ->
                            analyticsHandler.postEvent(
                                RewardsEventKey.Shown_WMPostOrderScreen,
                                mapOf(
                                    RewardsEventKey.cards_left to ((postPaymentCard.targetCards
                                        ?: 0) - it),
                                    RewardsEventKey.days_left to postPaymentCard.daysLeft.toString(),
                                    RewardsEventKey.target_cards to postPaymentCard.targetCards.orZero(),
                                    RewardsEventKey.card_copy_primary to postPaymentCard.title.orEmpty(),
                                    RewardsEventKey.card_copy_secondary to postPaymentCard.secondaryTitle.orEmpty(),
                                    RewardsEventKey.card_copy_tertiary to postPaymentCard.tertiaryTitle.orEmpty()
                                )
                            )
                        }


                        rewardLL.addView(weeklyMagicNew)

                    }

                    null -> Unit
                    WinningsType.MYSTERY_CARD_HERO -> Unit
                }
            }
        }
    }

    private fun contactUs() {
        viewModel.fetchManualPaymentStatusResponse?.transactionId?.let {
            when (viewModel.fetchManualPaymentStatusResponse?.getManualPaymentStatus()) {
                ManualPaymentStatus.SUCCESS -> {}
                ManualPaymentStatus.FAILURE -> {
                    analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_ContactUs_OrderFailureScreen)
                }

                ManualPaymentStatus.PENDING -> {
                    analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_ContactUs_OrderProcessingScreen)
                }

                else -> {}
            }
            val number = remoteConfigManager.getWhatsappNumber()
            requireContext().openWhatsapp(
                number,
                getCustomStringFormatted(
                    MR.strings.feature_buy_gold_v2_im_having_issues_buying_gold_for_x_transactionId,
                    it
                )
            )
        }
    }

    private fun getData() {
        viewModel.fetchManualPaymentStatus(
            orderStatusModel?.transactionId!!,
            orderStatusModel?.paymentProvider!!,
            orderStatusModel?.paymentFlowSource!!
        )
    }

    private fun handleBackPress() {
        viewModel.fetchManualPaymentStatusResponse?.let {
            when (it.getManualPaymentStatus()) {
                ManualPaymentStatus.SUCCESS -> {
                    analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_BackButton_OrderSuccessScreen)
                }

                ManualPaymentStatus.FAILURE -> {
                    analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_BackButton_OrderFailureScreen)
                }

                ManualPaymentStatus.PENDING -> {
                    analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_BackButton_OrderProcessingScreen)
                }
            }
        }
        if (orderStatusModel?.paymentFlowSource == BaseConstants.ManualPaymentFlowType.SinglePageHomeScreenFlow)
            EventBus.getDefault().post(GoToHomeEvent(OrderStatusFragmentV2::class.java.name))
        else
            popBackStack()
    }

    private fun checkForWeeklyChallenge(data: FetchManualPaymentStatusResponse) {
        data.weeklyChallengeResponse?.let {
            EventBus.getDefault().postSticky(RefreshWeeklyChallengeMetaEvent())
        }
    }
}
package com.jar.app.feature_buy_gold_v2.impl.ui.order_status

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.text.method.LinkMovementMethod
import android.transition.Fade
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.RefreshUserGoldBalanceEvent
import com.jar.app.base.data.event.RefreshWeeklyChallengeMetaEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.dynamic_cards.DynamicEpoxyController
import com.jar.app.core_ui.dynamic_cards.base.EpoxyBaseEdgeEffectFactory
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.extension.vibrate
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_utils.data.FileUtils
import com.jar.app.base.util.writeBitmap
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.FragmentOrderStatusBinding
import com.jar.app.weekly_magic_common.api.WeeklyChallengeCommonApi
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldInputData
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.greenrobot.eventbus.EventBus
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
internal class OrderStatusFragment : BaseFragment<FragmentOrderStatusBinding>() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var fileUtils: FileUtils

    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    @Inject
    lateinit var weeklyChallengeCommonApi: WeeklyChallengeCommonApi

    private val args by navArgs<OrderStatusFragmentArgs>()

    private var vibrator: Vibrator? = null

    private val mediaPlayer by lazy { MediaPlayer.create(requireContext(), com.jar.app.core_ui.R.raw.coins_rain_audio) }
    private var coinRainAnimationJob: Job? = null

    private var controller: DynamicEpoxyController? = null

    private var layoutManager: LinearLayoutManager? = null

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 10.dp, escapeEdges = false)

    private var markWeeklyChallengeCardWon = true
    private val edgeEffectFactory = EpoxyBaseEdgeEffectFactory()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOrderStatusBinding
        get() = FragmentOrderStatusBinding::inflate

    private val viewModelProvider: OrderStatusViewModelAndroid by viewModels()

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        getData()
        setupUI()
        setupListeners()
        observeLiveData()
    }

    override fun onPause() {
        markCardOrChallengeAsWon()
        super.onPause()
    }

    private fun markCardOrChallengeAsWon() {
        if(markWeeklyChallengeCardWon) {
            viewModel.weeklyChallengeMetaLiveData.value?.data?.data?.let {
                weeklyChallengeCommonApi.markCardOrChallengeAsWon(it)
            }
        }
    }

    private fun getData() {
        viewModel.fetchManualPaymentStatus(
            args.transactionId,
            args.paymentProvider,
            args.paymentFlowSource)
        viewModel.fetchWeeklyChallengeMetaData()
    }

    private fun setupUI() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else
            requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        binding.tvTitle.text = getCustomString(MR.strings.feature_buy_gold_v2_buy_order_status)
        binding.ivTitleImage.setImageResource(com.jar.app.core_ui.R.drawable.ic_hand_gold)
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchManualPaymentResponseLiveData.collect(
                    onSuccess = {
                        checkForWeeklyChallenge(it)
                        binding.tvPrice.isVisible =
                            (it.amount != null) || (it.amount != 0f)
                        binding.tvPrice.text =
                            it.amount?.let { it1 ->
                                getCustomStringFormatted(
                                    MR.strings.feature_buy_gold_v2_rupee_x_in_double,
                                    it1
                                )
                            }
                        if (it.oneTimeInvestment == false) {
                            binding.tvPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                            binding.tvGoldPurchaseStatus.setTextSize(
                                TypedValue.COMPLEX_UNIT_SP,
                                18f
                            )
                        }
                        viewModel.fetchManualPaymentStatusResponse = it
                        setViewsAccordingToStatusType(it)
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.dynamicCardsLiveData.collectLatest {
                    controller?.cards = it
                    binding.layoutSuccess.dynamicRecyclerView.invalidateItemDecorations()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.weeklyChallengeMetaLiveData.collectLatest {

                }
            }
        }
    }

    private fun checkForWeeklyChallenge(data: com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse) {
        data.weeklyChallengeResponse?.let {
            EventBus.getDefault().postSticky(RefreshWeeklyChallengeMetaEvent())
            binding.tvDescriptionWeeklyMagic.text = it.banner?.takeIf { it.isNotBlank() } ?:getCustomStringFormatted(
                com.jar.app.feature_weekly_magic_common.shared.MR.strings.feature_weekly_magic_common_collect_all_cards_and_win_up_in_gold,it.uptoRewardAmount.takeIf { it!= null && it != 0 }?.toString()?:"1000")
            TransitionManager.beginDelayedTransition(binding.containerWeeklyMagic, Fade(Fade.OUT).addTarget(binding.containerWeeklyMagic))
            binding.containerWeeklyMagic.isVisible = true
        }
    }

    @SuppressLint("Range")
    private fun setupDynamicCards() {
        binding.layoutSuccess.dynamicRecyclerView.isVisible = true
        layoutManager = LinearLayoutManager(context)
        controller = DynamicEpoxyController(
            uiScope = uiScope,
            onPrimaryCtaClick = { primaryActionData, eventData ->
                popBackStack()
                EventBus.getDefault().post(HandleDeepLinkEvent(primaryActionData.value))
                analyticsHandler.postEvent(
                    EventKey.Clicked_dynamicCard,
                    eventData.map
                )
            },
            onEndIconClick = { staticInfoData, eventData ->
                popBackStack()
                EventBus.getDefault().post(HandleDeepLinkEvent(staticInfoData.value))
                analyticsHandler.postEvent(
                    EventKey.Clicked_EndIcon_dynamicCard,
                    eventData.map
                )
            }
        )
        binding.layoutSuccess.dynamicRecyclerView.layoutManager = layoutManager
        binding.layoutSuccess.dynamicRecyclerView.setItemSpacingPx(0)
        binding.layoutSuccess.dynamicRecyclerView.addItemDecorationIfNoneAdded(
            spaceItemDecoration
        )
        binding.layoutSuccess.dynamicRecyclerView.edgeEffectFactory = edgeEffectFactory
        val visibilityTracker = EpoxyVisibilityTracker()
        visibilityTracker.partialImpressionThresholdPercentage = 50
        visibilityTracker.attach(binding.layoutSuccess.dynamicRecyclerView)
        binding.layoutSuccess.dynamicRecyclerView.setControllerAndBuildModels(controller!!)
    }

    private fun setViewsAccordingToStatusType(fetchManualPaymentStatusResponse: com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse) {
        when (fetchManualPaymentStatusResponse.getManualPaymentStatus()) {
            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.SUCCESS -> fetchManualPaymentStatusResponse.getManualPaymentGoldStatus()
                ?.let {
                    when (it) {
                        com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.SUCCESS -> setOrderSuccessView()
                        com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.FAILURE -> setOrderProcessingView()
                        com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.PENDING -> setOrderProcessingView()
                    }
                } ?: kotlin.run {
                setOrderSuccessView()
            }

            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.FAILURE -> setOrderFailureView()

            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.PENDING -> when (fetchManualPaymentStatusResponse.paymentProvider) {
                com.jar.app.core_base.domain.model.OneTimePaymentGateway.JUSPAY.name -> setOrderFailureView()
                com.jar.app.core_base.domain.model.OneTimePaymentGateway.PAYTM.name -> setOrderProcessingView()
                else -> setOrderProcessingView()
            }
        }
    }

    private fun setOrderSuccessView() {
        binding.btnContactUs.isVisible = false
        setupDynamicCards()
        viewModel.fetchOrderStatusDynamicCards(args.transactionId)
        binding.layoutSuccess.root.isVisible = true
        binding.layoutFailure.root.isVisible = false
        binding.layoutPending.root.isVisible = false
        binding.animView.isVisible = true
        binding.animView.playAnimation()
        binding.animView.setAnimation(R.raw.purchase_success)
        binding.animView.playAnimation()
        binding.animView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator) {
                binding.animView.vibrate(vibrator)
            }

            override fun onAnimationStart(p0: Animator) {
                coinRainAnimationJob?.cancel()
                coinRainAnimationJob = uiScope.launch {
                    if (isActive) {
                        mediaPlayer.start()
                        delay(3000)
                        if (isActive) {
                            mediaPlayer.stop()
                        }
                    }
                }
            }

            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
        })
        binding.tvGoldPurchaseStatus.setTextColor(
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_58DDC8)
        )

        binding.tvGoldBought.isVisible =
            args.isOneTimeInvestment && viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldVolume.isNullOrEmpty()
                .not()
        binding.tvGoldBought.text =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldVolume?.let {
                getCustomStringFormatted(
                    MR.strings.feature_buy_gold_v2_bought_x_24k_pure_gold,
                    it
                )
            }
        binding.tvGoldPurchaseStatus.text =
            if (args.isOneTimeInvestment)
                getCustomString(MR.strings.feature_buy_gold_v2_gold_purchase_successful)
            else
                getCustomString(MR.strings.feature_buy_gold_v2_payment_successful)

        binding.layoutSuccess.groupShare.isVisible =
            viewModel.fetchManualPaymentStatusResponse?.shareText.isNullOrEmpty().not()

        binding.layoutSuccess.groupGoldCredit.isVisible =
            viewModel.fetchManualPaymentStatusResponse?.shareText.isNullOrEmpty()

        binding.layoutSuccess.tvDownloadInvoice.isVisible = args.isOneTimeInvestment
        binding.layoutSuccess.tvDownloadInvoice.movementMethod = LinkMovementMethod()

        val goldAmt =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldAmt.orZero()
        val goldVolume =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldVolume ?: ""
        val isAuspicious =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.auspiciousTime.orFalse()

        analyticsHandler.postEvent(
            BuyGoldV2EventKey.Shown_OrderSuccessScreen,
            mapOf(
                BuyGoldV2EventKey.Amount to goldAmt,
                BuyGoldV2EventKey.Quantity to goldVolume,
                BuyGoldV2EventKey.IsAuspicious to isAuspicious,
                BuyGoldV2EventKey.Flow to args.paymentFlowSource
            )
        )
        EventBus.getDefault().postSticky(RefreshUserGoldBalanceEvent())
    }

    private fun setOrderFailureView() {
        binding.btnContactUs.isVisible = true
        binding.layoutSuccess.root.isVisible = false
        binding.layoutFailure.root.isVisible = true
        binding.layoutPending.root.isVisible = false
        val goldAmt =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldAmt.orZero()
        val goldVolume =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldVolume ?: ""
        val isAuspicious =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.auspiciousTime.orFalse()
        binding.tvGoldBought.isVisible =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldVolume.isNullOrEmpty()
                .not()
        binding.tvGoldBought.text =
            getCustomStringFormatted(MR.strings.feature_buy_gold_v2_quantity_x_24k_pure_gold, goldVolume)
        binding.animView.setAnimation(R.raw.purchase_failure)
        binding.animView.playAnimation()
        binding.animView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                uiScope.launch {
                    binding.animView.vibrate(vibrator)
                    delay(1000L)
                    binding.animView.vibrate(vibrator)
                    delay(1000L)
                    binding.animView.vibrate(vibrator)
                }
            }

            override fun onAnimationEnd(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
        })
        binding.tvGoldPurchaseStatus.setTextColor(
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_EB6A6E)
        )
        binding.layoutFailure.tvTransactionId.text =
            viewModel.fetchManualPaymentStatusResponse?.transactionId
        binding.tvGoldPurchaseStatus.text =
            if (args.isOneTimeInvestment)
                getCustomString(MR.strings.feature_buy_gold_v2_gold_purchase_failed)
            else
                getCustomString(MR.strings.feature_buy_gold_v2_payment_failed)

        analyticsHandler.postEvent(
            BuyGoldV2EventKey.Shown_OrderFailureScreen,
            mapOf(
                BuyGoldV2EventKey.Amount to goldAmt,
                BuyGoldV2EventKey.Quantity to goldVolume,
                BuyGoldV2EventKey.IsAuspicious to isAuspicious,
                BuyGoldV2EventKey.Flow to args.paymentFlowSource
            )
        )
    }

    private fun setOrderProcessingView() {
        binding.layoutSuccess.root.isVisible = false
        binding.layoutFailure.root.isVisible = false
        binding.layoutPending.root.isVisible = true
        binding.btnContactUs.isVisible = true
        val goldVolume =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldVolume ?: ""
        binding.tvGoldBought.isVisible =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldVolume.isNullOrEmpty()
                .not()
        binding.tvGoldBought.text =
            getCustomStringFormatted(MR.strings.feature_buy_gold_v2_quantity_x_24k_pure_gold, goldVolume)
        binding.animView.setAnimation(com.jar.app.core_ui.R.raw.purchase_processing)
        binding.animView.playAnimation()
        binding.animView.repeatCount = 3
        binding.tvGoldPurchaseStatus.setTextColor(
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_ebb46a)
        )
        binding.tvGoldPurchaseStatus.text =
            if (args.isOneTimeInvestment)
                getCustomString(MR.strings.feature_buy_gold_v2_gold_purchase_processing)
            else
                getCustomString(MR.strings.feature_buy_gold_v2_payment_in_progress)
        analyticsHandler.postEvent(
            BuyGoldV2EventKey.Shown_OrderProcessingScreen,
            mapOf(BuyGoldV2EventKey.Flow to args.paymentFlowSource)
        )
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            viewModel.fetchManualPaymentStatusResponse?.let {
                when (it.getManualPaymentStatus()) {
                    com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.SUCCESS -> {
                        analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_BackButton_OrderSuccessScreen)
                    }
                    com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.FAILURE -> {
                        analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_BackButton_OrderFailureScreen)
                    }
                    com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.PENDING -> {
                        analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_BackButton_OrderProcessingScreen)
                    }
                }
            }
            popBackStack()
        }
        binding.btnContactUs.setDebounceClickListener {
            viewModel.fetchManualPaymentStatusResponse?.transactionId?.let {
                when (viewModel.fetchManualPaymentStatusResponse?.getManualPaymentStatus()) {
                    com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.SUCCESS -> {}
                    com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.FAILURE -> {
                        analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_ContactUs_OrderFailureScreen)
                    }
                    com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.PENDING -> {
                        analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_ContactUs_OrderProcessingScreen)
                    }
                    else -> {}
                }
                val number = remoteConfigApi.getWhatsappNumber()
                requireContext().openWhatsapp(
                    number,
                    getCustomStringFormatted(
                        MR.strings.feature_buy_gold_v2_im_having_issues_buying_gold_for_x_transactionId,
                        it
                    )
                )
            }
        }

        binding.containerWeeklyMagic.setDebounceClickListener {
            analyticsHandler.postEvent(WeeklyMagicConstants.AnalyticsKeys.Clicked_WeeklyMagicBanner_BuyGoldPostOrderScreen)
            startWeeklyHomeScreenFlow()
        }
        setOrderSuccessClickListener()
        setOrderFailureClickListener()
        setOrderPendingClickListener()
    }

    private fun startWeeklyHomeScreenFlow() {
        viewModel.weeklyChallengeMetaLiveData.value?.data?.data?.takeIf { !it.challengeId.isNullOrBlank() }?.let { dataFromServer ->
            markWeeklyChallengeCardWon = false
            popBackStack()
            weeklyChallengeCommonApi.startWinAnimationAndWeeklyMagicHomeFlow(
                dataFromServer,
                WeeklyMagicConstants.AnalyticsKeys.Screens.Buy_Gold_Post_Order_Screen,
                false
            )
        } ?: kotlin.run {
            getCustomString(com.jar.app.feature_weekly_magic_common.shared.MR.strings.feature_weekly_magic_common_challenge_not_found).snackBar(binding.containerContent)
        }
    }

    private fun setOrderSuccessClickListener() {
        binding.layoutSuccess.tvDownloadInvoice.setOnClickListener {
            analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_ViewInvoice_OrderSuccessScreen)
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.invoiceLink?.let {
                webPdfViewerApi.openPdf(it)
            }
        }

        binding.layoutSuccess.btnShare.setDebounceClickListener {
            shareImage()
        }

        binding.layoutSuccess.btnGotIt.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun setOrderFailureClickListener() {
        binding.layoutFailure.ivCopy.setDebounceClickListener {
            viewModel.fetchManualPaymentStatusResponse?.transactionId?.let {
                requireContext().copyToClipboard(it, getCustomString(MR.strings.copied))
            }
        }
        binding.layoutFailure.btnTryAgain.setDebounceClickListener {
            analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_TryAgain_OrderFailureScreen)
            navigateTo(
                "android-app://com.jar.app/buyGoldV2/${serializer.encodeToString(BuyGoldInputData())}",
                true,
                R.id.orderStatusFragment,
                inclusive = true
            )
        }

    }

    private fun setOrderPendingClickListener() {
        binding.layoutPending.btnRefresh.setDebounceClickListener {
            analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_CheckStatus_OrderProcessingScreen)
            viewModel.fetchManualPaymentStatus(args.transactionId, args.paymentProvider,args.paymentFlowSource)
        }
    }

    private fun shareImage() {
        analyticsHandler.postEvent(BuyGoldV2EventKey.Clicked_ShareButton_OrderSuccessScreen)
        viewModel.fetchManualPaymentStatusResponse?.let { orderDetails ->
            orderDetails.shareImageUrl?.let {
                Glide
                    .with(this@OrderStatusFragment)
                    .asBitmap()
                    .load(it)
                    .placeholder(com.jar.app.core_ui.R.drawable.image_auspicious_share)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            uiScope.launch(Dispatchers.IO) {
                                val parent =
                                    File(
                                        requireContext().externalCacheDir,
                                        BaseConstants.CACHE_DIR_SHARED
                                    )
                                parent.mkdirs()
                                val file = File(parent, "order_success_share.png")
                                withContext(Dispatchers.Main) {
                                    file.writeBitmap(resource, Bitmap.CompressFormat.PNG, 100)
                                    fileUtils.shareImage(
                                        requireContext(), file,
                                        orderDetails.shareText ?: BaseConstants.PLAY_STORE_URL
                                    )
                                }
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
        }
    }

    override fun onDestroyView() {
        controller = null
        binding.layoutSuccess.dynamicRecyclerView.adapter = null
        layoutManager = null
        binding.layoutSuccess.dynamicRecyclerView.layoutManager = null
        super.onDestroyView()
    }
}
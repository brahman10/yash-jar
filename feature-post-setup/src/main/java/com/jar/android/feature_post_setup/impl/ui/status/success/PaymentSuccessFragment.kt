package com.jar.android.feature_post_setup.impl.ui.status.success

import android.animation.ObjectAnimator
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupFragmentPaymentSuccessBinding
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.winnings.customAnimations.MysteryCardAnimation
import com.jar.app.core_ui.winnings.customAnimations.SpinsAnimation
import com.jar.app.core_ui.winnings.customAnimations.WeeklyMagicAnimation
import com.jar.app.core_base.domain.model.WinningsType
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_post_setup.shared.PostSetupMR
import com.jar.app.feature_post_setup.util.PostSetupConstants
import com.jar.app.feature_post_setup.util.PostSetupEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Duration
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class PaymentSuccessFragment :
    BaseBottomSheetDialogFragment<FeaturePostSetupFragmentPaymentSuccessBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeaturePostSetupFragmentPaymentSuccessBinding
        get() = FeaturePostSetupFragmentPaymentSuccessBinding::inflate

    private var animation: ObjectAnimator? = null
    private val args: PaymentSuccessFragmentArgs by navArgs()
    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
            }
        }

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    private val viewModel: PaymentSuccessViewModel by viewModels()
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(shouldShowFullHeight = true)

    override fun setup() {
        viewModel.fetchManualPaymentStatus(
            transactionId = args.transactionId,
            paymentManager.getCurrentPaymentGateway().name
        )
        observeLiveData()
        setupListener()
        registerBackPressDispatcher()
    }

    private fun observeLiveData() {
        viewModel.fetchManualPaymentResponseLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                viewModel.fetchManualPaymentStatusResponse = it
                setupUI()
                setUpRewardsCards()
            },
            onError = {
                dismissProgressBar()
                setupUI()
            }
        )
    }

    private fun setupUI() {
        setLinearProgressWithAnimation()
        binding.animConfetti.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.CONFETTI_FROM_TOP
        )

        binding.successLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.SMALL_CHECK
        )
        binding.tvTitle.text =
            viewModel.fetchManualPaymentStatusResponse?.title
                ?: getCustomString(PostSetupMR.strings.feature_post_setup_gold_purchase_successful)

        viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldVolume?.let {
            binding.tvRsXAndXGm.text =
                getCustomStringFormatted(
                    PostSetupMR.strings.feature_post_setup_xrs_fgm,
                    viewModel.fetchManualPaymentStatusResponse?.amount.orZero().toInt(),
                    viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.goldVolume.orEmpty()
                )
        } ?: kotlin.run {
            binding.tvRsXAndXGm.text =
                getCustomStringFormatted(
                    PostSetupMR.strings.feature_post_setup_xrs,
                    viewModel.fetchManualPaymentStatusResponse?.amount.orZero().toInt()
                )
        }

        binding.invoiceGroup.isVisible =
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.invoiceLink.isNullOrEmpty()
                .not()
    }

    private fun setupListener() {
        binding.tvDownloadInvoice.movementMethod = LinkMovementMethod()
        binding.tvDownloadInvoice.setDebounceClickListener {
            analyticsApi.postEvent(PostSetupEventKey.PostSetupDS_ViewInvoiceClicked)
            viewModel.fetchManualPaymentStatusResponse?.oneTimeInvestOrderDetails?.invoiceLink?.let {
                webPdfViewerApi.openPdf(it)
            }
        }
    }

    private fun setLinearProgressWithAnimation() {
        val durationInMillis = Duration.ofSeconds(3).toMillis()
        animation = ObjectAnimator.ofInt(binding.lpiProgress, "progress", 0, 100)
        animation?.duration = durationInMillis
        animation?.interpolator = LinearInterpolator()
        animation?.doOnEnd {
            findNavController().currentBackStackEntry?.savedStateHandle?.set(
                PostSetupConstants.SUCCESSFUL_TRANSACTION_CALLBACK,
                true
            )
            popBackStack()
        }
        animation?.start()
    }

    private fun setUpRewardsCards() {
        if (viewModel.fetchManualPaymentStatusResponse?.postPaymentRewardCardList.isNullOrEmpty()) {
            binding.tvYourReward.isVisible = false
            binding.lineSeparator.isVisible = false
        }
        viewModel.fetchManualPaymentStatusResponse?.postPaymentRewardCardList?.forEach { postPaymentCard ->
            val animationType = postPaymentCard.animationType
            animationType?.let {
                when (WinningsType.getWinningsType(it)) {
                    WinningsType.SPINS -> {
                        val spins = SpinsAnimation(binding.root.context, true).apply {
                            setData(
                                postPaymentCard
                            )
                            setOnClickListener {
                                postPaymentCard.let {
                                    analyticsApi.postEvent(
                                        EventKey.Clicked_dynamicCard,
                                        mapOf(
                                            DynamicCardEventKey.FeatureType to EventKey.AutopayBottomSheet.Spin_Count,
                                            DynamicCardEventKey.Data to it.toString(),
                                            PostSetupEventKey.feature_type to PostSetupEventKey.post_setup_daily_savings
                                        )
                                    )
                                }
                                popBackStack()
                                postPaymentCard.deepLink?.let { link ->
                                    EventBus.getDefault().post(HandleDeepLinkEvent(link))
                                }
                            }
                        }
                        binding.rewardLL.addView(spins)
                    }

                    WinningsType.MYSTERY_CARDS -> {
                        val mysteryCard = MysteryCardAnimation(binding.root.context, true).apply {
                            setData(postPaymentCard)
                            val marginParam = this.layoutParams as? ViewGroup.MarginLayoutParams
                            marginParam?.marginStart = 100
                            setOnClickListener {
                                postPaymentCard.let {
                                    analyticsApi.postEvent(
                                        EventKey.Clicked_dynamicCard,
                                        mapOf(
                                            DynamicCardEventKey.FeatureType to it.animationType.toString(),
                                            DynamicCardEventKey.Data to it.toString(),
                                            PostSetupEventKey.feature_type to PostSetupEventKey.post_setup_daily_savings
                                        )
                                    )
                                }
                                popBackStack()
                                postPaymentCard.deepLink?.let { link ->
                                    EventBus.getDefault().post(HandleDeepLinkEvent(link))
                                }
                            }
                        }
                        binding.rewardLL.addView(mysteryCard)
                    }

                    WinningsType.WEEKLY_MAGIC -> {
                        val weeklyMagic = WeeklyMagicAnimation(binding.root.context, true).apply {
                            setData(postPaymentCard)

                            val marginParam = this.layoutParams as? ViewGroup.MarginLayoutParams
                            marginParam?.marginStart = 100
                            setOnClickListener {
                                postPaymentCard.let {
                                    analyticsApi.postEvent(
                                        EventKey.Clicked_dynamicCard,
                                        mapOf(
                                            DynamicCardEventKey.FeatureType to it.animationType.toString(),
                                            DynamicCardEventKey.Data to it.toString()
                                        )
                                    )
                                }
                                popBackStack()
                                postPaymentCard.deepLink?.let { link ->
                                    EventBus.getDefault().post(HandleDeepLinkEvent(link))
                                }
                            }
                        }
                        binding.rewardLL.addView(weeklyMagic)
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        animation?.cancel()
        super.onDestroyView()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }
}
package com.jar.app.feature_sell_gold.impl.ui.withdrawal_status

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.RefreshUserGoldBalanceEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.dynamic_cards.DynamicEpoxyController
import com.jar.app.core_ui.dynamic_cards.base.EpoxyBaseEdgeEffectFactory
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_sell_gold.R
import com.jar.app.feature_sell_gold.api.SellGoldApi
import com.jar.app.feature_sell_gold.databinding.FeatureSellGoldWithdrawalStatusFragmentBinding
import com.jar.app.feature_sell_gold.shared.MR
import com.jar.app.feature_sell_gold.shared.utils.SellGoldConstants
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.ButtonClicked
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.DownloadInvoice
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.GoldSaleStatus
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.PayoutStatus
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.Retry
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.SavingsWithdrawnAmount
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.SavingsWithdrawnVolume
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.Withdraw_PostSuccessScreen_Clicked
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.Withdraw_PostSuccessScreen_Shown
import com.jar.app.feature_sell_gold_common.shared.WithdrawalAcceptedResponse
import com.jar.app.feature_sell_gold_common.shared.WithdrawalResponseStatus
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Duration
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class WithdrawalStatusFragment :
    BaseFragment<FeatureSellGoldWithdrawalStatusFragmentBinding>() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var sellGoldApi: SellGoldApi

    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val weakReference: WeakReference<View> by lazy {
        WeakReference(binding.root)
    }

    private val args by navArgs<WithdrawalStatusFragmentArgs>()

    private val viewModelProvider by viewModels<WithdrawalStatusViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy { viewModelProvider.getInstance() }

    private var controller: DynamicEpoxyController? = null

    private var layoutManager: LinearLayoutManager? = null

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 10.dp, escapeEdges = false)

    private val edgeEffectFactory = EpoxyBaseEdgeEffectFactory()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureSellGoldWithdrawalStatusFragmentBinding
        get() = FeatureSellGoldWithdrawalStatusFragmentBinding::inflate

    private var invoiceLink: String? = null

    private var isWaitingTimerRunning = true

    private var waitingTimerJob: Job? = null
    private var fetchStatusJob: Job? = null

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!binding.clWithdrawalProcessing.isVisible) {
                    navigateTransactionsFragment()
                    analyticsHandler.postEvent(
                        Withdraw_PostSuccessScreen_Clicked,
                        ButtonClicked,
                        Retry
                    )
                }
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        setupToolbar()
        observeLiveData()
        initClickListeners()
        if (args.isRetryFlow.not()) {
            // args.withdrawRequest should be not null in this case
            binding.tvWithdrawalProcessingAmount.text =
                getCustomStringFormatted(
                    MR.strings.feature_sell_gold_currency_sign_x,
                    args.withdrawRequest?.amount?.toDouble().orZero()
                )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postWithdrawRequest()
    }

    private fun startDefaultWaitingTimer() {
        waitingTimerJob?.cancel()
        waitingTimerJob = uiScope.countDownTimer(
            Duration.ofSeconds(40).toMillis(),
            onInterval = {
                isWaitingTimerRunning = true
                binding.tvTimer.text = it.milliSecondsToCountDown()
            },
            onFinished = {
                isWaitingTimerRunning = false
            }
        )
    }

    private fun startFetchStatusJob() {
        fetchStatusJob?.cancel()
        fetchStatusJob = uiScope.doRepeatingTask(repeatInterval = 5000) {
            fetchWithdrawalStatus()
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
    }

    private fun initClickListeners() {
        binding.toolbar.btnBack.setDebounceClickListener {
            navigateTransactionsFragment()
        }

        binding.tvViewInvoice.setDebounceClickListener {
            invoiceLink?.let {
                webPdfViewerApi.openPdf(it)
                analyticsHandler.postEvent(
                    Withdraw_PostSuccessScreen_Clicked,
                    ButtonClicked,
                    DownloadInvoice
                )
            }
        }
    }

    private fun postWithdrawRequest() {
        if (args.isRetryFlow) {
            // args.orderId and vpa should be not null in this case
            viewModel.retryWithdrawal(args.orderId!!, args.vpa!!)
        } else {
            // args.withdrawRequest should be not null in this case
            viewModel.postWithdrawalRequest(args.withdrawRequest!!)
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.retryWithdrawalRequestLiveData.collect(
                    onLoading = {
                        showProcessingLayout()
                    },
                    onSuccess = {
                        startDefaultWaitingTimer()
                        startFetchStatusJob()
                    },
                    onSuccessWithNullData = {
                        hideProcessingLayout()
                        setupAPIFailureUI(isErrorFromThirdParty = false)
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(weakReference.get()!!)
                        hideProcessingLayout()
                        setupAPIFailureUI(errorMessage, isErrorFromThirdParty = false)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.postWithdrawalRequestLiveData.collect(
                    onLoading = {
                        showProcessingLayout()
                    },
                    onSuccess = {
                        if (it!!.responseStatus == WithdrawalResponseStatus.PENDING.name) {
                            startDefaultWaitingTimer()
                            startFetchStatusJob()
                        } else {
                            hideProcessingLayout()
                            setupUIForTransactionStatus(it)
                        }
                    },
                    onSuccessWithNullData = {
                        hideProcessingLayout()
                        setupAPIFailureUI(isErrorFromThirdParty = false)
                    },
                    onError = { message, errorCode ->
                        message.snackBar(weakReference.get()!!)
                        hideProcessingLayout()
                        setupAPIFailureUI(
                            message,
                            isErrorFromThirdParty = false,
                            isVpaIncorrect = errorCode == SellGoldConstants.WithdrawalErrorCodes.VPA_INCORRECT
                        )
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchWithdrawalStatusLiveData.collect(
                    onLoading = {
                        if (isWaitingTimerRunning.not()) showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (it?.responseStatus.orEmpty() == WithdrawalResponseStatus.PENDING.name) {
                            if (isWaitingTimerRunning.not()) {
                                hideProcessingLayout()
                                setupUIForTransactionStatus(it)
                                fetchStatusJob?.cancel()
                                waitingTimerJob?.cancel()
                            }
                        } else {
                            hideProcessingLayout()
                            setupUIForTransactionStatus(it)
                            fetchStatusJob?.cancel()
                            waitingTimerJob?.cancel()
                        }
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        setupAPIFailureUI(isErrorFromThirdParty = false)
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(weakReference.get()!!)
                        dismissProgressBar()
                        setupAPIFailureUI(errorMessage, isErrorFromThirdParty = false)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.withdrawalReasonsLiveData.collect(
                    onSuccess = {
                        setupReasonsRecycler(it?.withdrawReasons ?: emptyList())
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.updateWithdrawalReasonLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        binding.layoutReason.root.slideToRevealNew(
                            viewToReveal = binding.layoutSuccess.root,
                            onAnimationEnd = {
                                binding.layoutSuccess.animViewConfetti.playAnimation()
                            }
                        )
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        binding.layoutReason.root.slideToRevealNew(
                            viewToReveal = binding.layoutSuccess.root,
                            onAnimationEnd = {
                                binding.layoutSuccess.animViewConfetti.playAnimation()
                            }
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(weakReference.get()!!)
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.dynamicCardsLiveData.collect {
                    controller?.cards = it
                    binding.layoutSuccess.dynamicRecyclerView.invalidateItemDecorations()
                }
            }
        }
    }

    private fun setupUIForTransactionStatus(withdrawalAcceptedResponse: WithdrawalAcceptedResponse?) {
        withdrawalAcceptedResponse ?: return
        withdrawalAcceptedResponse.sellGoldResponse.invoiceLink?.let {
            invoiceLink = it
        }
        if (withdrawalAcceptedResponse.sellGoldResponse.txnStatus == WithdrawalResponseStatus.SUCCESS.name && withdrawalAcceptedResponse.responseStatus == WithdrawalResponseStatus.SUCCESS.name) {
            setupSuccessUI(withdrawalAcceptedResponse)
            initSuccessClickListeners()
        } else if (withdrawalAcceptedResponse.sellGoldResponse.txnStatus == WithdrawalResponseStatus.PENDING.name || withdrawalAcceptedResponse.responseStatus == WithdrawalResponseStatus.PENDING.name) {
            setupPendingUI(withdrawalAcceptedResponse)
            initPendingClickListeners(withdrawalAcceptedResponse)
        } else if (withdrawalAcceptedResponse.sellGoldResponse.txnStatus == WithdrawalResponseStatus.FAILURE.name) {
            setupFailureUI(withdrawalAcceptedResponse)
            initTransactionFailureClickListeners()
        } else if (withdrawalAcceptedResponse.sellGoldResponse.txnStatus == WithdrawalResponseStatus.SUCCESS.name && withdrawalAcceptedResponse.responseStatus == WithdrawalResponseStatus.FAILURE.name) {
            setupFailureUI(withdrawalAcceptedResponse)
            initPayoutFailureClickListeners(withdrawalAcceptedResponse)
        } else {
            setupAPIFailureUI()
        }
        binding.clContent.isVisible = true

        analyticsHandler.postEvent(
            event = Withdraw_PostSuccessScreen_Shown,
            values = mapOf(
                GoldSaleStatus to withdrawalAcceptedResponse.sellGoldResponse.txnStatus,
                PayoutStatus to withdrawalAcceptedResponse.responseStatus.orEmpty(),
                SavingsWithdrawnAmount to withdrawalAcceptedResponse.amount.orZero(),
                SavingsWithdrawnVolume to withdrawalAcceptedResponse.volume.orEmpty()
            )
        )
    }

    private fun setBackPressCallbackIsEnabled(isEnabled: Boolean) {
        backPressCallback.isEnabled = isEnabled
    }

    private fun navigateTransactionsFragment() {
        popBackStack(
            if (args.isRetryFlow) R.id.vpaSelectionFragment else R.id.amountEntryFragment,
            true
        )
    }

    private fun showProcessingLayout() {
        binding.animViewProcessing.clearAnimation()
        binding.animViewProcessing.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.PULSATING_SELL_GOLD_LOTTIE
        )
        binding.clWithdrawalProcessing.isVisible = true
        invoiceLink = null
    }

    private fun hideProcessingLayout() {
        EventBus.getDefault().postSticky(RefreshUserGoldBalanceEvent())
        binding.clWithdrawalProcessing.isVisible = false
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.feature_sell_gold_order_status)
        binding.toolbar.ivTitleImage.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_withdraw_cash)
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.separator.isVisible = true
    }

    private fun setupSuccessUI(withdrawalAcceptedResponse: WithdrawalAcceptedResponse) {
        binding.pwSellGoldStatus.isInvisible = false
        viewModel.fetchWithdrawalReasons()
        setupDynamicCards()
        viewModel.fetchOrderStatusDynamicCards()
        binding.animView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/SellGold/success.json"
        )
        binding.layoutSuccess.animViewConfetti.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/SellGold/thanks_for_feedback.json"
        )
        binding.tvWithdrawalStatus.text = withdrawalAcceptedResponse.header
        binding.tvWithdrawalStatus.setTextColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.color_58DDC8
            )
        )
        binding.tvWithdrawalPrice.text =
            getCustomStringFormatted(
                MR.strings.feature_sell_gold_currency_sign_x,
                withdrawalAcceptedResponse.sellGoldResponse.amount.orZero()
            )
        binding.tvOrderDescription.text = getCustomStringFormatted(
            MR.strings.feature_sell_gold_sold_x_balance_x,
            withdrawalAcceptedResponse.sellGoldResponse.goldVolume,
            withdrawalAcceptedResponse.sellGoldResponse.remainingVol
        )
        binding.layoutSuccess.tvMoneyCredited.text = withdrawalAcceptedResponse.info
        binding.layoutReason.tvMoneyCredited.text = withdrawalAcceptedResponse.info
        binding.layoutReason.root.isVisible = true
        binding.layoutSuccess.root.isVisible = false
        binding.layoutFailedOrPending.root.isVisible = false
        binding.tvViewInvoice.isVisible = !invoiceLink.isNullOrEmpty()
        analyticsHandler.postEvent(
            SellGoldEvent.Shown_Screen_SellGold, mapOf(
                SellGoldEvent.AMOUNT to (args.withdrawRequest?.amount ?: 0),
                SellGoldEvent.Volume to (args.withdrawRequest?.volume ?: 0),
                SellGoldEvent.Screen to SellGoldEvent.WithdrawalSuccess
            )
        )
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

    private fun setupReasonsRecycler(reasonsList: List<String>) {
        val reasonsAdapter = ReasonsAdapter {
            viewModel.updateWithdrawalReason(it)
            analyticsHandler.postEvent(
                SellGoldEvent.Clicked_WithdrawalSuccessScreen,
                mapOf(SellGoldEvent.SurveyReason to it)
            )
        }
        reasonsAdapter.submitList(reasonsList)
        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        layoutManager.alignItems = AlignItems.FLEX_START
        binding.layoutReason.rvReasons.adapter = reasonsAdapter
        binding.layoutReason.rvReasons.layoutManager = layoutManager
        binding.layoutReason.cvSelectReason.isVisible = true
    }

    private fun initSuccessClickListeners() {
        binding.layoutSuccess.btnDone.setDebounceClickListener {
            navigateTransactionsFragment()
        }
    }

    private fun setupPendingUI(withdrawalAcceptedResponse: WithdrawalAcceptedResponse) {
        binding.pwSellGoldStatus.isInvisible = true
        binding.animView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/SellGold/processing.json"
        )
        binding.tvWithdrawalStatus.text = withdrawalAcceptedResponse.header
        binding.tvWithdrawalStatus.setTextColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.color_EBB46A
            )
        )
        binding.tvWithdrawalPrice.text =
            getCustomStringFormatted(
                MR.strings.feature_sell_gold_currency_sign_x,
                withdrawalAcceptedResponse.sellGoldResponse.amount.orZero()
            )
        binding.tvOrderDescription.text = getCustomStringFormatted(
            MR.strings.feature_sell_gold_sold_x_balance_x,
            withdrawalAcceptedResponse.sellGoldResponse.goldVolume,
            withdrawalAcceptedResponse.sellGoldResponse.remainingVol
        )
        binding.layoutFailedOrPending.tvDescription.text = withdrawalAcceptedResponse.info
        binding.layoutFailedOrPending.btnAction.setText(
            getCustomString(MR.strings.feature_sell_gold_check_status)
        )
        binding.layoutFailedOrPending.root.isVisible = true
        binding.layoutSuccess.root.isVisible = false
        binding.layoutReason.root.isVisible = false
        binding.tvViewInvoice.isVisible = !invoiceLink.isNullOrEmpty()
        analyticsHandler.postEvent(
            SellGoldEvent.Shown_Screen_SellGold, mapOf(
                SellGoldEvent.Screen to SellGoldEvent.WithdrawalPending
            )
        )
    }

    private fun initPendingClickListeners(withdrawalAcceptedResponse: WithdrawalAcceptedResponse) {
        binding.layoutFailedOrPending.btnAction.setDebounceClickListener {
            fetchWithdrawalStatus()
            analyticsHandler.postEvent(
                SellGoldEvent.Clicked_WithdrawalPendingScreen, mapOf(
                    SellGoldEvent.ButtonClicked to SellGoldEvent.CheckStatus
                )
            )
        }

        binding.layoutFailedOrPending.chatWhatsapp.setDebounceClickListener {
            val number = remoteConfigApi.getWhatsappNumber()
            requireContext().openWhatsapp(
                number,
                getCustomStringFormatted(
                    MR.strings.feature_sell_gold_wa_description,
                    withdrawalAcceptedResponse.orderId.orEmpty()
                )
            )
            analyticsHandler.postEvent(
                SellGoldEvent.Clicked_WithdrawalPendingScreen, mapOf(
                    ButtonClicked to SellGoldEvent.ContactUs
                )
            )
        }
    }

    private fun fetchWithdrawalStatus() {
        if (args.isRetryFlow)
            viewModel.fetchWithdrawalStatus(viewModel.retryWithdrawalRequestLiveData.value?.data?.data?.withdrawalAcceptedResponse?.orderId.orEmpty())
        else
            viewModel.fetchWithdrawalStatus(viewModel.postWithdrawalRequestLiveData.value?.data?.data?.orderId.orEmpty())
    }

    private fun setupFailureUI(withdrawalAcceptedResponse: WithdrawalAcceptedResponse) {
        binding.pwSellGoldStatus.isInvisible = true
        binding.animView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/SellGold/failure.json"
        )
        binding.tvWithdrawalStatus.text = withdrawalAcceptedResponse.header
        binding.tvWithdrawalStatus.setTextColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.color_EB6A6E
            )
        )
        binding.tvWithdrawalPrice.text =
            getCustomStringFormatted(
                MR.strings.feature_sell_gold_currency_sign_x,
                withdrawalAcceptedResponse.sellGoldResponse.amount.orZero()
            )
        binding.tvOrderDescription.text = getCustomStringFormatted(
            MR.strings.feature_sell_gold_quantity_x_gm,
            withdrawalAcceptedResponse.sellGoldResponse.goldVolume
        )
        binding.layoutFailedOrPending.tvDescription.text = withdrawalAcceptedResponse.info
        binding.layoutFailedOrPending.btnAction.setText(
            getCustomString(MR.strings.feature_sell_gold_try_again)
        )
        binding.layoutFailedOrPending.root.isVisible = true
        binding.layoutFailedOrPending.chatWhatsapp.isVisible = false
        binding.layoutSuccess.root.isVisible = false
        binding.layoutReason.root.isVisible = false
        binding.tvViewInvoice.isVisible = false
        analyticsHandler.postEvent(
            SellGoldEvent.Shown_Screen_SellGold, mapOf(
                SellGoldEvent.Screen to SellGoldEvent.WithdrawalFailed
            )
        )
    }

    private fun initTransactionFailureClickListeners() {
        binding.layoutFailedOrPending.btnAction.setDebounceClickListener {
            postWithdrawRequest()
            analyticsHandler.postEvent(
                SellGoldEvent.Clicked_WithdrawalFailedScreen, mapOf(
                    SellGoldEvent.ButtonClicked to SellGoldEvent.Retry
                )
            )
        }

        binding.layoutFailedOrPending.chatWhatsapp.setDebounceClickListener {
            val number = remoteConfigApi.getWhatsappNumber()
            requireContext().openWhatsapp(
                number,
                getCustomString(
                    MR.strings.feature_sell_gold_wa_description_without_id,
                )
            )
            analyticsHandler.postEvent(
                SellGoldEvent.Clicked_WithdrawalFailedScreen, mapOf(
                    SellGoldEvent.ButtonClicked to SellGoldEvent.ContactUs
                )
            )
        }
    }

    private fun initPayoutFailureClickListeners(withdrawalAcceptedResponse: WithdrawalAcceptedResponse) {
        binding.layoutFailedOrPending.btnAction.setDebounceClickListener {

            sellGoldApi.openVpaSelectionFragment(
                isRetryFlow = true,
                withdrawalPrice = args.withdrawRequest?.amount?.amountToString(),
                orderId = withdrawalAcceptedResponse.orderId,
                popUpTo = if (args.isRetryFlow) R.id.vpaSelectionFragment else R.id.amountEntryFragment
            )

            analyticsHandler.postEvent(
                SellGoldEvent.Clicked_WithdrawalFailedScreen, mapOf(
                    SellGoldEvent.ButtonClicked to SellGoldEvent.Retry
                )
            )
        }

        binding.layoutFailedOrPending.chatWhatsapp.setDebounceClickListener {
            val number = remoteConfigApi.getWhatsappNumber()
            requireContext().openWhatsapp(
                number,
                getCustomString(
                    MR.strings.feature_sell_gold_wa_description_without_id,
                )
            )
            analyticsHandler.postEvent(
                SellGoldEvent.Clicked_WithdrawalFailedScreen, mapOf(
                    ButtonClicked to SellGoldEvent.ContactUs
                )
            )
        }
    }

    private fun setupAPIFailureUI(
        error: String = "",
        isErrorFromThirdParty: Boolean = true,
        isVpaIncorrect: Boolean = false
    ) {
        binding.pwSellGoldStatus.isInvisible = true
        binding.animView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/SellGold/failure.json"
        )
        binding.animView.playAnimation()
        binding.tvWithdrawalStatus.text =
            getCustomString(MR.strings.feature_sell_gold_withdrawal_failed)
        binding.tvWithdrawalStatus.setTextColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.color_EB6A6E
            )
        )
        if (args.isRetryFlow) {
            binding.tvWithdrawalPrice.isVisible = false
            binding.tvOrderDescription.isVisible = false
            val marginLayoutParams =
                binding.layoutFailedOrPending.chatWhatsapp.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.topMargin = 40.dp
        } else {
            binding.tvWithdrawalPrice.text =
                getCustomStringFormatted(
                    MR.strings.feature_sell_gold_currency_sign_x,
                    args.withdrawRequest?.amount?.toDouble().orZero()
                )
            binding.tvOrderDescription.text = getCustomStringFormatted(
                MR.strings.feature_sell_gold_quantity_x_gm,
                args.withdrawRequest?.volume.orZero()
            )
        }
        binding.layoutFailedOrPending.tvDescription.text = error
        binding.layoutFailedOrPending.btnAction.setText(
            getCustomString(MR.strings.feature_sell_gold_try_again)
        )
        binding.layoutFailedOrPending.root.isVisible = true
        binding.layoutSuccess.root.isVisible = false
        binding.clContent.isVisible = true
        binding.layoutReason.root.isVisible = false
        binding.tvViewInvoice.isVisible = false
        binding.layoutFailedOrPending.btnAction.isVisible =
            isErrorFromThirdParty.not()
        binding.layoutFailedOrPending.chatWhatsapp.isVisible =
            isErrorFromThirdParty
        if (isVpaIncorrect)
            binding.layoutFailedOrPending.btnAction.setDebounceClickListener {
                sellGoldApi.openVpaSelectionFragment(
                    isRetryFlow = false,
                    withdrawalPrice = args.withdrawRequest?.amount?.amountToString(),
                    orderId = null,
                    popUpTo = R.id.vpaSelectionFragment
                )

                analyticsHandler.postEvent(
                    SellGoldEvent.Clicked_WithdrawalFailedScreen, mapOf(
                        SellGoldEvent.ButtonClicked to SellGoldEvent.Retry
                    )
                )
            }
        else
            initTransactionFailureClickListeners()
        analyticsHandler.postEvent(
            SellGoldEvent.Shown_Screen_SellGold, mapOf(
                SellGoldEvent.Screen to SellGoldEvent.WithdrawalFailed
            )
        )
    }

    override fun onResume() {
        super.onResume()
        setBackPressCallbackIsEnabled(isEnabled = true)
    }

    override fun onDestroyView() {
        controller = null
        binding.layoutSuccess.dynamicRecyclerView.adapter = null
        layoutManager = null
        binding.layoutSuccess.dynamicRecyclerView.layoutManager = null
        super.onDestroyView()
    }
}
package com.jar.app.feature_payment.impl.ui.payment_option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.PaymentPageFragmentBackPressEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.extension.toast
import com.jar.app.core_utils.data.NetworkUtil
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.OneTimePaymentResult
import com.jar.app.feature_one_time_payments.shared.data.model.juspay.JuspayPaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.event.AvailableAppEvent
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateGetAvailableUpiAppWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateGetSavedCardsWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateUpiIntentWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.event.SavedCardsEvents
import com.jar.app.feature_one_time_payments.shared.domain.model.*
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.*
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.*
import com.jar.app.feature_one_time_payments.shared.util.OneTimePaymentConstants
import com.jar.app.feature_payment.R
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_payment.databinding.FragmentPaymentOptionPageBinding
import com.jar.app.feature_payment.impl.domain.BackPressedOnPaymentPageEvent
import com.jar.app.feature_payment.impl.domain.RetryManualPaymentEvent
import com.jar.app.feature_payment.impl.ui.payment_option.adapter_delegates.*
import com.jar.app.feature_one_time_payments.shared.util.OneTimePaymentEventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
internal class PaymentOptionPageFragment : BaseFragment<FragmentPaymentOptionPageBinding>() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var networkUtil: NetworkUtil

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var prefsApi: PrefsApi

    private var baseEdgeEffectFactory: BaseEdgeEffectFactory? = null

    private var layoutManager: LinearLayoutManager? = null

    private var upiIntentAdapterDelegate: UpiIntentAppsPaymentMethodAdapterDelegate? = null

    private var upiCollectAdapterDelegate: UpiCollectPaymentMethodSectionAdapterDelegate? = null

    private var recentlyUsedMethodAdapterDelegate: RecentlyUsedPaymentMethodAdapterDelegate? = null

    private var orderSummarySectionAdapterDelegate: OrderSummarySectionAdapterDelegate? = null

    private var savedCardSectionAdapterDelegate: SavedCardsSectionAdapterDelegate? = null

    private var addCardSectionAdapterDelegate: AddCardSectionAdapterDelegate? = null

    private var securePaymentSectionAdapterDelegate: SecurePaymentSectionAdapterDelegate? = null

    private var savedUpiIdAdapterDelegate: SavedUpiIdsAdapterDelegate? = null

    private var paymentSectionAdapter: PaymentSectionAdapter? = null

    private val viewModelProvider by viewModels<PaymentOptionPageFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val args by navArgs<PaymentOptionPageFragmentArgs>()

    private var initiatePaymentResponse: InitiatePaymentResponse? = null

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.getExitSurveyData()
            }
        }

    private val uuid by lazy {
        UUID.randomUUID().toString()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPaymentOptionPageBinding
        get() = FragmentPaymentOptionPageBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().postSticky(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initiatePaymentResponse = serializer.decodeFromString<InitiatePaymentResponse>(
            decodeUrl(args.initiatePaymentResponse)
        )
        getData()
        EventBus.getDefault().register(this)
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
    }

    private fun getData() {
        viewModel.fetchEnabledPaymentMethod(initiatePaymentResponse?.transactionType)
        viewModel.fetchRecentlyUsedPaymentMethods(
            isPackageInstalled = {
                context?.isPackageInstalled(it).orFalse()
            }
        )
        viewModel.fetchSavedUpiIds()
    }

    private fun setupUI() {
        analyticsHandler.postEvent(
            OneTimePaymentEventKey.Shown_Manual_PaymentScreen,
            mapOf(
                OneTimePaymentEventKey.Action to OneTimePaymentEventKey.Shown,
                OneTimePaymentEventKey.FlowSource to initiatePaymentResponse?.screenSource.orEmpty(),
                OneTimePaymentEventKey.PaymentGateway to paymentManager.getCurrentPaymentGateway()
            )
        )
        upiIntentAdapterDelegate = UpiIntentAppsPaymentMethodAdapterDelegate {
            analyticsHandler.postEvent(
                OneTimePaymentEventKey.UpiIntentMethodClicked,
                mapOf(
                    OneTimePaymentEventKey.Action to OneTimePaymentEventKey.Clicked,
                    OneTimePaymentEventKey.FlowSource to initiatePaymentResponse?.screenSource.orEmpty(),
                    OneTimePaymentEventKey.PaymentGateway to paymentManager.getCurrentPaymentGateway(),
                    OneTimePaymentEventKey.PaymentMethod to "UpiIntent",
                    OneTimePaymentEventKey.Amount to initiatePaymentResponse?.amount.orZero(),
                    OneTimePaymentEventKey.AppName to it.appName,
                    OneTimePaymentEventKey.PackageName to it.packageName,
                )
            )
            EventBus.getDefault().post(
                InitiateUpiIntentWithJuspay(
                    InitiateUpiIntent(
                        uuid,
                        initiatePaymentResponse?.juspay?.orderId!!,
                        it.packageName,
                        initiatePaymentResponse?.juspay?.clientAuthToken!!
                    )
                )
            )
        }

        upiCollectAdapterDelegate = UpiCollectPaymentMethodSectionAdapterDelegate(
            uiScope,
            onFocus = {
                uiScope.launch {
                    delay(500)
                    binding.rvPaymentSections.smoothScrollToPosition(it)
                }
            },
            onVerifyClick = {
                viewModel.verifyUpiAddress(it)
            }
        )

        recentlyUsedMethodAdapterDelegate = RecentlyUsedPaymentMethodAdapterDelegate(
            onCardClick = { paymentMethod ->
                analyticsHandler.postEvent(
                    OneTimePaymentEventKey.RecentlyUsedCardMethodClicked,
                    mapOf(
                        OneTimePaymentEventKey.Action to OneTimePaymentEventKey.Clicked,
                        OneTimePaymentEventKey.FlowSource to initiatePaymentResponse?.screenSource.orEmpty(),
                        OneTimePaymentEventKey.PaymentGateway to paymentManager.getCurrentPaymentGateway(),
                        OneTimePaymentEventKey.PaymentMethod to paymentMethod?.javaClass?.name.orEmpty(),
                        OneTimePaymentEventKey.Amount to initiatePaymentResponse?.amount.orZero(),
                    )
                )
            }
        ) {
            var paymentMethod = ""
            when (it) {
                is PaymentMethodCard -> {
                    paymentMethod = "PaymentMethodCard"
                    navigateTo(
                        PaymentOptionPageFragmentDirections.actionPaymentOptionPageFragmentToSavedCardFragment(
                            uuid,
                            initiatePaymentResponse?.amount!!,
                            initiatePaymentResponse?.juspay!!,
                            it.savedCard!!
                        )
                    )
                }

                is PaymentMethodNB -> {

                }

                is PaymentMethodUpiCollect -> {
                    paymentMethod = "PaymentMethodUpiCollect"
                    viewModel.initiateUpiCollectRequest(
                        InitiateUpiCollectRequest(
                            orderId = initiatePaymentResponse?.juspay?.orderId!!,
                            amount = initiatePaymentResponse!!.amount,
                            vpa = it.payerVpa,
                            paymentProvider = com.jar.app.core_base.domain.model.OneTimePaymentGateway.JUSPAY.name
                        )
                    )
                }

                is PaymentMethodUpiIntent -> {
                    paymentMethod = "PaymentMethodUpiIntent"
                    EventBus.getDefault().post(
                        InitiateUpiIntentWithJuspay(
                            InitiateUpiIntent(
                                uuid,
                                initiatePaymentResponse?.juspay?.orderId!!,
                                it.payerApp,
                                initiatePaymentResponse?.juspay?.clientAuthToken!!
                            )
                        )
                    )
                }
            }
            analyticsHandler.postEvent(
                OneTimePaymentEventKey.RecentlyUsedMethodClicked,
                mapOf(
                    OneTimePaymentEventKey.Action to OneTimePaymentEventKey.Clicked,
                    OneTimePaymentEventKey.FlowSource to initiatePaymentResponse?.screenSource.orEmpty(),
                    OneTimePaymentEventKey.PaymentGateway to paymentManager.getCurrentPaymentGateway(),
                    OneTimePaymentEventKey.PaymentMethod to paymentMethod,
                    OneTimePaymentEventKey.Amount to initiatePaymentResponse?.amount.orZero(),
                )
            )
        }

        orderSummarySectionAdapterDelegate = OrderSummarySectionAdapterDelegate()

        savedCardSectionAdapterDelegate = SavedCardsSectionAdapterDelegate {
            analyticsHandler.postEvent(
                OneTimePaymentEventKey.SavedCardMethodClicked,
                mapOf(
                    OneTimePaymentEventKey.Action to OneTimePaymentEventKey.Clicked,
                    OneTimePaymentEventKey.FlowSource to initiatePaymentResponse?.screenSource.orEmpty(),
                    OneTimePaymentEventKey.PaymentGateway to paymentManager.getCurrentPaymentGateway(),
                    OneTimePaymentEventKey.PaymentMethod to "SavedCards",
                    OneTimePaymentEventKey.Amount to initiatePaymentResponse?.amount.orZero(),
                )
            )
            navigateTo(
                PaymentOptionPageFragmentDirections.actionPaymentOptionPageFragmentToSavedCardFragment(
                    uuid,
                    initiatePaymentResponse?.amount!!,
                    initiatePaymentResponse?.juspay!!,
                    it
                )
            )
        }

        addCardSectionAdapterDelegate = AddCardSectionAdapterDelegate {
            analyticsHandler.postEvent(
                OneTimePaymentEventKey.AddCardMethodClicked,
                mapOf(
                    OneTimePaymentEventKey.Action to OneTimePaymentEventKey.Clicked,
                    OneTimePaymentEventKey.FlowSource to initiatePaymentResponse?.screenSource.orEmpty(),
                    OneTimePaymentEventKey.PaymentGateway to paymentManager.getCurrentPaymentGateway(),
                    OneTimePaymentEventKey.PaymentMethod to "AddCard",
                    OneTimePaymentEventKey.Amount to initiatePaymentResponse?.amount.orZero(),
                )
            )
            navigateTo(
                PaymentOptionPageFragmentDirections.actionPaymentOptionPageFragmentToAddCardFragment(
                    requestId = uuid,
                    initiatePaymentPayload = initiatePaymentResponse?.juspay!!
                )
            )
        }

        savedUpiIdAdapterDelegate = SavedUpiIdsAdapterDelegate {
            analyticsHandler.postEvent(
                OneTimePaymentEventKey.SavedUpiMethodClicked,
                mapOf(
                    OneTimePaymentEventKey.Action to OneTimePaymentEventKey.Clicked,
                    OneTimePaymentEventKey.FlowSource to initiatePaymentResponse?.screenSource.orEmpty(),
                    OneTimePaymentEventKey.PaymentGateway to paymentManager.getCurrentPaymentGateway(),
                    OneTimePaymentEventKey.PaymentMethod to "SavedUpiIds",
                    OneTimePaymentEventKey.Amount to initiatePaymentResponse?.amount.orZero(),
                )
            )
            viewModel.initiateUpiCollectRequest(
                InitiateUpiCollectRequest(
                    orderId = initiatePaymentResponse?.juspay?.orderId!!,
                    amount = initiatePaymentResponse!!.amount,
                    vpa = it,
                    paymentProvider = com.jar.app.core_base.domain.model.OneTimePaymentGateway.JUSPAY.name
                )
            )
        }

        securePaymentSectionAdapterDelegate = SecurePaymentSectionAdapterDelegate()

        paymentSectionAdapter = PaymentSectionAdapter(
            listOf(
                upiIntentAdapterDelegate!!,
                upiCollectAdapterDelegate!!,
                recentlyUsedMethodAdapterDelegate!!,
                orderSummarySectionAdapterDelegate!!,
                addCardSectionAdapterDelegate!!,
                savedCardSectionAdapterDelegate!!,
                savedUpiIdAdapterDelegate!!,
                securePaymentSectionAdapterDelegate!!
            )
        )

        paymentSectionAdapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT

        layoutManager = LinearLayoutManager(context)
        binding.rvPaymentSections.layoutManager = layoutManager
        baseEdgeEffectFactory = BaseEdgeEffectFactory()
        binding.rvPaymentSections.edgeEffectFactory = baseEdgeEffectFactory!!
        binding.rvPaymentSections.adapter = paymentSectionAdapter
    }

    private fun setupListeners() {
        binding.btnBack.setDebounceClickListener {
            if (prefsApi.isOnboardingComplete().not()) {
                popBackStack()
            } else {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun observeLiveData() {

        viewModel.amount = initiatePaymentResponse?.amount

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.enabledPaymentMethodsFlow.collectUnwrapped(
                    onSuccess = {
                        it.forEach {
                            when (it) {
                                OneTimePaymentMethodType.UPI_INTENT -> {
                                    EventBus.getDefault().post(
                                        InitiateGetAvailableUpiAppWithJuspay(
                                            GetAvailableUpiApps(
                                                requestId = uuid,
                                                orderId = initiatePaymentResponse?.juspay?.orderId!!
                                            )
                                        )
                                    )
                                }

                                OneTimePaymentMethodType.CARD -> {
                                    EventBus.getDefault().post(
                                        InitiateGetSavedCardsWithJuspay(
                                            GetSavedCardsPayload(
                                                uuid,
                                                initiatePaymentResponse?.juspay?.clientAuthToken!!
                                            )
                                        )
                                    )
                                }

                                else -> {

                                }
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        popBackStack()
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.recentlyUsedPaymentMethodFlow.collectUnwrapped(
                    onSuccess = {
                        viewModel.mergePaymentData(recentlyUsedPaymentMethods = it)
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.savedUpiAddressFlow.collect(
                    onSuccess = {
                        viewModel.mergePaymentData(savedUpiIdsResponse = it)
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.listFlow.collectLatest {
                    uiScope.launch {
                        paymentSectionAdapter?.items = it
                        binding.shimmerPlaceholder.stopShimmer()
                        binding.shimmerPlaceholder.isVisible = false
                        binding.rvPaymentSections.isVisible = true
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.verifyUpiFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            if (it.valid) {
                                viewModel.mergePaymentData(upiCollectErrorMessage = null)
                                viewModel.initiateUpiCollectRequest(
                                    InitiateUpiCollectRequest(
                                        orderId = initiatePaymentResponse?.juspay?.orderId!!,
                                        amount = initiatePaymentResponse!!.amount,
                                        vpa = it.vpa,
                                        paymentProvider = OneTimePaymentGateway.JUSPAY.name
                                    )
                                )
                            } else {
                                val message =
                                    getString(com.jar.app.core_ui.R.string.feature_payment_please_enter_a_valid_upi_id)
                                viewModel.mergePaymentData(upiCollectErrorMessage = message)
                                message.snackBar(
                                    binding.root,
                                    translationY = -4.dp.toFloat()
                                )
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.initiateUpiCollectFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        val oneTimePaymentResult = OneTimePaymentResult(
                            oneTimePaymentGateway = OneTimePaymentGateway.JUSPAY,
                            fetchCurrentGoldPriceResponse = initiatePaymentResponse?.fetchCurrentGoldPriceResponse!!,
                            amount = initiatePaymentResponse!!.amount,
                            orderId = it.orderId,
                            juspayPaymentResponse = JuspayPaymentResponse(
                                action = OneTimePaymentConstants.JuspayAction.UPI_TXN,
                                orderId = it.orderId,
                                status = null
                            )
                        )
                        navigateTo(
                            PaymentOptionPageFragmentDirections
                                .actionPaymentOptionPageFragmentToUpiCollectTimerFragment(
                                    oneTimePaymentResult,
                                    R.id.paymentOptionPageFragment
                                )
                        )
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.exitSurveyResponse.collectLatest {
                    it?.let { isShowExitSurvey ->
                        if (isShowExitSurvey) {
                            EventBus.getDefault().post(
                                BackPressedOnPaymentPageEvent()
                            )
                            EventBus.getDefault().post(
                                PaymentPageFragmentBackPressEvent(
                                    featureFlow = initiatePaymentResponse?.screenSource,
                                    whichBottomSheet = BaseConstants.LeftBottomSheet
                                )
                            )
                            EventBus.getDefault().post(
                                HandleDeepLinkEvent("${BaseConstants.EXIT_SURVEY_DEEPLINK}/${ExitSurveyRequestEnum.MANUAL_BUY_TRANSACTION_SCREEN.name}")
                            )
                        } else {
                            EventBus.getDefault().post(
                                BackPressedOnPaymentPageEvent()
                            )
                            EventBus.getDefault().post(
                                PaymentPageFragmentBackPressEvent(
                                    featureFlow = initiatePaymentResponse?.screenSource,
                                    whichBottomSheet = BaseConstants.LeftBottomSheet
                                )
                            )
                            popBackStack()
                        }
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAvailableAppsEvent(availableAppEvent: AvailableAppEvent) {
        if (availableAppEvent.upiApps.isEmpty() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
            getString(com.jar.app.core_ui.R.string.feature_payment_no_upi_apps_found).snackBar(
                binding.root
            )

        viewModel.setUpiApps(availableAppEvent.upiApps)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onSavedCardsEvent(savedCardsEvents: SavedCardsEvents) {
        EventBus.getDefault().removeStickyEvent(savedCardsEvents)
        uiScope.launch(Dispatchers.Default) {
            val finalList =
                serializer.decodeFromString<List<SavedCard>>(savedCardsEvents.cards.toString())
            viewModel.cards = finalList
            viewModel.mergePaymentData(cards = finalList)
        }
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        layoutManager = null
        paymentSectionAdapter = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRetryManualPaymentEvent(retryManualPaymentEvent: RetryManualPaymentEvent) {
        this.initiatePaymentResponse = retryManualPaymentEvent.initiatePaymentResponse
    }
}
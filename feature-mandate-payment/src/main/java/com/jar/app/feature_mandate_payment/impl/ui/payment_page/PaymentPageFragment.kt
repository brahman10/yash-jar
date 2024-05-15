package com.jar.app.feature_mandate_payment.impl.ui.payment_page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.PaymentPageFragmentBackPressEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.getPhonePeVersionCode
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_mandate_payment.NavigationMandatePaymentDirections
import com.jar.app.feature_mandate_payment.R
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentFragmentPaymentPageBinding
import com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate.CouponAdapterDelegate
import com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate.DescriptionAdapterDelegate
import com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate.MandateEducationAdapterDelegate
import com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate.PreferredBankAdapterDelegate
import com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate.SeparatorAdapterDelegate
import com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate.SpaceAdapterDelegate
import com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate.TitleAdapterDelegate
import com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate.UpiAppAdapterDelegate
import com.jar.app.feature_mandate_payment.impl.ui.payment_page.adapter_delegate.UpiCollectAdapterDelegate
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payment_common.impl.ui.PaymentPageFragmentViewModelAndroid
import com.jar.app.feature_mandate_payments_common.shared.MandatePaymentBuildKonfig
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.CouponCodeResponseForMandateScreenItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_upi.VerifyUpiAddressResponse
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.cachapa.expandablelayout.ExpandableLayout
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import com.jar.app.feature_mandate_payments_common.shared.MR


@AndroidEntryPoint
internal class PaymentPageFragment :
    BaseFragment<FeatureMandatePaymentFragmentPaymentPageBinding>() {

    @Inject
    lateinit var paymentApi: MandatePaymentApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val args by navArgs<PaymentPageFragmentArgs>()

    private val paymentPageHeaderDetail by lazy {
        val decoded = decodeUrl(args.paymentPageHeaderDetails)
        serializer.decodeFromString<PaymentPageHeaderDetail>(decoded)
    }

    private val backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.getExitSurveyData(initiateMandatePaymentRequest.subscriptionType)
                isEnabled = false
            }
        }

    private val initiateMandatePaymentRequest by lazy {
        val decoded = decodeUrl(args.initiateMandatePaymentRequest)
        serializer.decodeFromString<InitiateMandatePaymentRequest>(decoded)
    }

    private val upiAppAdapterDelegate: UpiAppAdapterDelegate = UpiAppAdapterDelegate(
        onItemSelected = { pos, upiApp, upiAppPaymentPageItem ->
            viewModel.updateSelectedState(upiAppPaymentPageItem)
            if (pos != -1) {
                uiScope.launch {
                    delay(500)
                    binding.rvPaymentPage.smoothScrollToPosition(pos)
                }
            }
            analyticsHandler.postEvent(
                MandatePaymentEventKey.Clicked_UPIApp_MandatePaymentScreen,
                mapOf(
                    MandatePaymentEventKey.Button to MandatePaymentEventKey.UpiApp,
                    MandatePaymentEventKey.Data to upiApp.appName,
                    MandatePaymentEventKey.UserLifecycle to paymentPageHeaderDetail.userLifecycle.orEmpty(),
                    MandatePaymentEventKey.AutopayMethod to initiateMandatePaymentRequest.authWorkflowType.name,
                    MandatePaymentEventKey.FeatureFlow to paymentPageHeaderDetail.featureFlow.orEmpty(),
                    MandatePaymentEventKey.Coupon to viewModel.couponCodeList?.isNotEmpty().orFalse(),
                    MandatePaymentEventKey.MandateAmount to initiateMandatePaymentRequest.mandateAmount.orZero(),
                    MandatePaymentEventKey.BestAmount to paymentPageHeaderDetail.bestAmount.orZero()
                )
            )
        },
        onPayClick = {
            viewModel.fetchInitiateMandatePaymentData(
                mandatePaymentGateway = paymentApi.getMandatePaymentGateway(),
                packageName = it.packageName,
                initiateMandatePaymentRequest = initiateMandatePaymentRequest.copy(couponCodeId = viewModel.couponCodeList?.getOrNull(0)?.couponCodeId),
                fetchPhonePeVersionCode = {
                    requireContext().getPhonePeVersionCode(MandatePaymentBuildKonfig.PHONEPE_PACKAGE)?.toString()
                }
            )
            analyticsHandler.postEvent(
                MandatePaymentEventKey.Clicked_UPIApp_MandatePaymentScreen,
                mapOf(
                    MandatePaymentEventKey.Button to MandatePaymentEventKey.Proceed,
                    MandatePaymentEventKey.Data to it.appName,
                    MandatePaymentEventKey.Coupon to viewModel.couponCodeList?.isNotEmpty().orFalse(),
                    MandatePaymentEventKey.UserLifecycle to paymentPageHeaderDetail.userLifecycle.orEmpty(),
                    MandatePaymentEventKey.AutopayMethod to initiateMandatePaymentRequest.authWorkflowType.name,
                    MandatePaymentEventKey.FeatureFlow to paymentPageHeaderDetail.featureFlow.orEmpty(),
                    MandatePaymentEventKey.MandateAmount to initiateMandatePaymentRequest.mandateAmount.orZero(),
                    MandatePaymentEventKey.BestAmount to paymentPageHeaderDetail.bestAmount.orZero()
                )
            )
        },
    )

    private val upiCollectAdapterDelegate: UpiCollectAdapterDelegate by lazy {
        UpiCollectAdapterDelegate(
            uiScope,
            onFocus = { pos, _ ->
                if (pos != -1) {
                    uiScope.launch {
                        delay(500)
                        binding.rvPaymentPage.smoothScrollToPosition(pos)
                    }
                }
            },
            onExpansionListener = { pos, state ->
                if (state == ExpandableLayout.State.EXPANDED) {
                    binding.rvPaymentPage.smoothScrollToPosition(pos)
                } else if (state == ExpandableLayout.State.COLLAPSED) {
                    binding.root.hideKeyboard()
                }
            },
            onItemSelected = { upiCollectPaymentPageItem ->
                viewModel.updateSelectedState(upiCollectPaymentPageItem )
             },
            onVerifyAndPayClick = { upiAddress ->
                viewModel.verifyUpiAddress(upiAddress)
            }
        )
    }

    private val mandateEducationAdapterDelegate: MandateEducationAdapterDelegate by lazy {
        MandateEducationAdapterDelegate(
            onVideoClicked = { videoUrl ->
                navigateTo(
                    NavigationMandatePaymentDirections.actionToMandateVideoBottomSheet(videoUrl)
                )
            }
        )
    }

    private val preferredBankAdapterDelegate: PreferredBankAdapterDelegate by lazy {
        PreferredBankAdapterDelegate(
            onPreferredBankClicked = {
                analyticsHandler.postEvent(
                    MandatePaymentEventKey.Clicked_UPIApp_MandatePaymentScreen,
                    mapOf(
                        MandatePaymentEventKey.Button to MandatePaymentEventKey.PreferredBank,
                        MandatePaymentEventKey.FeatureFlow to paymentPageHeaderDetail.featureFlow.orEmpty(),
                        MandatePaymentEventKey.UserLifecycle to paymentPageHeaderDetail.userLifecycle.orEmpty(),
                        MandatePaymentEventKey.BankName to it.bankName.orEmpty(),
                        MandatePaymentEventKey.Coupon to viewModel.couponCodeList?.isNotEmpty().orFalse(),
                        MandatePaymentEventKey.MandateAmount to initiateMandatePaymentRequest.mandateAmount.orZero(),
                        MandatePaymentEventKey.BestAmount to paymentPageHeaderDetail.bestAmount.orZero()
                    )
                )
            },
            onCardShown = {
                analyticsHandler.postEvent(
                    MandatePaymentEventKey.Shown_Preferred_Bank_Card,
                )
            }
        )
    }

    private val delegates by lazy {
        listOf(
            TitleAdapterDelegate(),
            CouponAdapterDelegate(uiScope = uiScope),
            DescriptionAdapterDelegate(),
            mandateEducationAdapterDelegate,
            SeparatorAdapterDelegate(),
            SpaceAdapterDelegate(),
            preferredBankAdapterDelegate,
            upiAppAdapterDelegate,
            upiCollectAdapterDelegate,
        )
    }

    private val adapter by lazy {
        PaymentPageAdapter(delegates)
    }

    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()

    private val viewModelProvider by viewModels<PaymentPageFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureMandatePaymentFragmentPaymentPageBinding
        get() = FeatureMandatePaymentFragmentPaymentPageBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(com.jar.app.core_ui.R.color.bgColor)
        viewModel.paymentPageHeaderDetail = paymentPageHeaderDetail
        viewModel.initiateMandatePaymentRequest = initiateMandatePaymentRequest
        viewModel.getData()
        analyticsHandler.postEvent(
            MandatePaymentEventKey.Clicked_UPIApp_MandatePaymentScreen_Shown,
            mapOf(
                MandatePaymentEventKey.FeatureFlow to paymentPageHeaderDetail.featureFlow.orEmpty(),
                MandatePaymentEventKey.UserLifecycle to paymentPageHeaderDetail.userLifecycle.orEmpty(),
                MandatePaymentEventKey.MandateAmount to initiateMandatePaymentRequest.mandateAmount.orZero(),
                MandatePaymentEventKey.BestAmount to paymentPageHeaderDetail.bestAmount.orZero()
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private fun setupUI() {
        binding.toolbar.tvTitle.text = paymentPageHeaderDetail.toolbarHeader
        paymentPageHeaderDetail.toolbarIcon?.let {
            binding.toolbar.ivTitleImage.setImageResource(it)
        }
        binding.toolbar.separator.isVisible = true
        binding.rvPaymentPage.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPaymentPage.adapter = adapter
        binding.rvPaymentPage.edgeEffectFactory = baseEdgeEffectFactory
    }

    private fun setupListeners() {
        binding.toolbar.btnBack.setDebounceClickListener {
            backPressCallback.handleOnBackPressed()
        }
        binding.toolbar.tvEnd.setDebounceClickListener {
            analyticsHandler.postEvent(
                MandatePaymentEventKey.Clicked_UPIApp_MandatePaymentScreen_Shown,
                mapOf(
                    MandatePaymentEventKey.Button to MandatePaymentEventKey.Skip,
                    MandatePaymentEventKey.UserLifecycle to paymentPageHeaderDetail.userLifecycle.orEmpty(),
                    MandatePaymentEventKey.FeatureFlow to paymentPageHeaderDetail.featureFlow.orEmpty(),
                    MandatePaymentEventKey.MandateAmount to paymentPageHeaderDetail.bestAmount.orZero()
                )
            )
            EventBus.getDefault().post(GoToHomeEvent("Onboarding Mandate Screen"))
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.mandatePaymentEducationLiveData.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.mergeApiResponse(
                            mandateEducationResp = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.paymentPageLiveData.collectLatest {
                    adapter.items = it
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.preferredBankLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.mergeApiResponse(
                            preferredBankPageItem = it
                        )
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                viewModel.verifyUpiAddressLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (isUpiAddressValidAndEligibleForMandates(it)) {
                            //navigate to payment screen
                        } else {
                            val errorMessage = getErrorMessage(it)
                            viewModel.updateVerifyUpiAddressErrorMessage(
                                errorMessage,
                                adapter.items
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.initiateMandatePaymentDataLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            initiateMandatePayment(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchEnabledPaymentMethodsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.mergeApiResponse(
                            enabledPaymentMethodResponse = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.couponCodesFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (!viewModel.couponCodeList.isNullOrEmpty()) {
                            viewModel.mergeApiResponse(
                                couponCodeResponseForMandateScreenItem = CouponCodeResponseForMandateScreenItem(
                                    couponCode = it[0]
                                )
                            )
                        } else {
                            viewModel.mergeApiResponse(
                                couponCodeResponseForMandateScreenItem = null
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.currentGoldBuyPriceFlow.collect(
                    onSuccess = {
                        applyCoupon()
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.applyCouponCodeFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            binding.animSuccessConfetti.isVisible = true
                            binding.animSuccessConfetti.playLottieWithUrlAndExceptionHandling(
                                requireContext(),
                                BaseConstants.LottieUrls.CONFETTI_FROM_TOP
                            )
                            viewModel.couponCodeList?.forEach { coupon ->
                                if (coupon.couponCode == it.couponCode) {
                                    return@forEach
                                }
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(
                            binding.root,
                            com.jar.app.core_ui.R.drawable.ic_filled_information_icon,
                            progressColor = com.jar.app.core_ui.R.color.color_016AE1,
                            duration = 2000,
                            translationY = 0f
                        )
                    },
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.exitSurveyResponse.collectLatest {
                it?.let {
                    if (it) {
                        findNavController().getBackStackEntry(R.id.paymentPageFragment)
                            .savedStateHandle[MandatePaymentCommonConstants.BACK_PRESSED_FROM_PAYMENT_SCREEN] =
                            true
                        analyticsHandler.postEvent(
                            MandatePaymentEventKey.Clicked_UPIApp_MandatePaymentScreen_Shown,
                            mapOf(
                                MandatePaymentEventKey.Button to MandatePaymentEventKey.Back,
                                MandatePaymentEventKey.FeatureFlow to paymentPageHeaderDetail.featureFlow.orEmpty(),
                                MandatePaymentEventKey.UserLifecycle to paymentPageHeaderDetail.userLifecycle.orEmpty(),
                                MandatePaymentEventKey.MandateAmount to paymentPageHeaderDetail.bestAmount.orZero()
                            )
                        )
                        val fromWhichScreen = if (initiateMandatePaymentRequest.subscriptionType == "DAILY_SAVINGS") {
                            ExitSurveyRequestEnum.DAILY_SAVINGS_TRANSACTION_SCREEN
                        } else {
                            ExitSurveyRequestEnum.ROUND_OFFS_TRANSACTION_SCREEN
                        }
                        EventBus.getDefault().post(
                            HandleDeepLinkEvent("${BaseConstants.EXIT_SURVEY_DEEPLINK}/${fromWhichScreen.name}")
                        )
                    } else {
                        findNavController().getBackStackEntry(R.id.paymentPageFragment)
                            .savedStateHandle[MandatePaymentCommonConstants.BACK_PRESSED_FROM_PAYMENT_SCREEN] =
                            true
                        analyticsHandler.postEvent(
                            MandatePaymentEventKey.Clicked_UPIApp_MandatePaymentScreen_Shown,
                            mapOf(
                                MandatePaymentEventKey.Button to MandatePaymentEventKey.Back,
                                MandatePaymentEventKey.FeatureFlow to paymentPageHeaderDetail.featureFlow.orEmpty(),
                                MandatePaymentEventKey.UserLifecycle to paymentPageHeaderDetail.userLifecycle.orEmpty(),
                                MandatePaymentEventKey.MandateAmount to paymentPageHeaderDetail.bestAmount.orZero()
                            )
                        )
                        popBackStack()
                        uiScope.launch {
                            EventBus.getDefault().post(
                                PaymentPageFragmentBackPressEvent(
                                    featureFlow = paymentPageHeaderDetail.userLifecycle,
                                    whichBottomSheet = BaseConstants.RightBottomSheet
                                )
                            )
                        }
                    }
                }
            }
        }

    }

    private fun applyCoupon() {
        if (!viewModel.couponCodeList.isNullOrEmpty()) {
            viewModel.applyManuallyEnteredCouponCode(
                viewModel.couponCodeList?.getOrNull(0)?.couponCode.toString(),
                initiateMandatePaymentRequest
            )
        }
    }


    private fun isUpiAddressValidAndEligibleForMandates(verifyUpiAddressResponse: VerifyUpiAddressResponse): Boolean {
        return verifyUpiAddressResponse.valid && verifyUpiAddressResponse.isEligibleForMandate
    }

    private fun getErrorMessage(verifyUpiAddressResponse: VerifyUpiAddressResponse): String? {
        return when {
            verifyUpiAddressResponse.valid.not() -> getCustomString(MR.strings.feature_mandate_payment_invalid_upi_id)
            verifyUpiAddressResponse.isEligibleForMandate.not() -> getCustomString(MR.strings.feature_mandate_payment_upi_id_doesnt_support_mandate)
            else -> null
        }
    }

    private fun initiateMandatePayment(
        initiateMandatePaymentApiResponse: InitiateMandatePaymentApiResponse,
    ) {
        findNavController().getBackStackEntry(R.id.paymentPageFragment)
            .savedStateHandle[MandatePaymentCommonConstants.MANDATE_PAYMENT_RESPONSE_FROM_SDK] =
            initiateMandatePaymentApiResponse
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }
    override fun onDestroy() {
        backPressCallback.isEnabled = false
        super.onDestroy()
    }
}
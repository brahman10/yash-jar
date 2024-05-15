package com.jar.app.feature_round_off.impl.ui.save_method

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.whenResumed
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.DispatcherProvider
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.dp
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffFragmentSelectRoundOffSaveMethodBinding
import com.jar.app.feature_round_off.shared.MR
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SelectRoundOffSaveMethodFragment :
    BaseFragment<FeatureRoundOffFragmentSelectRoundOffSaveMethodBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffFragmentSelectRoundOffSaveMethodBinding
        get() = FeatureRoundOffFragmentSelectRoundOffSaveMethodBinding::inflate

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var serializer: Serializer

    private var isAutomaticPaymentSelected = true

    private var automaticRoundOffStepsAdapter: RoundOffStepsAdapter? = null
    private var manualRoundOffStepsAdapter: RoundOffStepsAdapter? = null

    private var orderId: String? = null
    private var roundOffAmount: Float? = null
    private var mandateAmount: Float? = null
    private var isSpendsDetected: Boolean = false
    private var automaticSetupType = ""
    private var manualSetupType = ""
    private var job: Job? = null
    private val viewModel: SelectRoundOffSaveMethodViewModel by viewModels()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun setup(savedInstanceState: Bundle?) {
        getData()
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun getData() {
        viewModel.fetchInitialRoundOffsData()
        viewModel.fetchRoundOffStepsData()
    }

    private fun setupUI() {
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.feature_round_off_round_off_label)
        binding.toolbar.ivTitleImage.isVisible = true
        binding.toolbar.separator.isVisible = true
        binding.toolbar.ivTitleImage.setImageResource(R.drawable.feature_round_off_ic_round_off)
        analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Roundoff_PaymentMethod_Screen,
            mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown)
        )
    }

    private fun setupListener() {
        binding.clManualContainer.setDebounceClickListener {
            isAutomaticPaymentSelected = false
            toggleManualPaymentSelection(isSelected = true)
            toggleAutomaticPaymentSelection(isSelected = false)
        }

        binding.clAutomaticContainer.setDebounceClickListener {
            isAutomaticPaymentSelected = true
            toggleManualPaymentSelection(isSelected = false)
            toggleAutomaticPaymentSelection(isSelected = true)
        }

        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }

        binding.btnGoAhead.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Roundoff_PaymentMethod_Screen,
                mapOf(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.SaveNowClicked,
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.PaymentPreference to if (isAutomaticPaymentSelected) com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Autopay else com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Manual
                )
            )
            if (isAutomaticPaymentSelected) {
                navigateTo(
                    SelectRoundOffSaveMethodFragmentDirections.actionSelectRoundOffSaveMethodFragmentToPreRoundOffAutopaySetupFragment()
                )
            } else {
                if (isSpendsDetected)
                    navigateTo(
                        SelectRoundOffSaveMethodFragmentDirections.actionSelectRoundOffSaveMethodFragmentToManualRoundOffConfirmationBottomSheet(
                            roundOffAmount = if (roundOffAmount?.equals(0f).orFalse())
                                remoteConfigApi.getRoundOffAmount().toFloat().orZero()
                            else
                                roundOffAmount.orZero(),
                            orderId = orderId
                        )
                    )
                else {
                    val amount = if (roundOffAmount == 0f)
                        remoteConfigApi.getRoundOffAmount().toFloat()
                    else
                        roundOffAmount.orZero()
                    navigateTo(
                        SelectRoundOffSaveMethodFragmentDirections.actionSelectRoundOffSaveMethodFragmentToRoundOffActivatedBottomSheet(
                            roundOffAmount = amount,
                            isSpendsDetected = isSpendsDetected
                        )
                    )
                }
            }
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.initialRoundOffLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                it?.let {
                    orderId = it.orderId
                    roundOffAmount = it.transactionAmount?.orZero()
                    mandateAmount = it.mandateAmount?.orZero()
                    isSpendsDetected =
                        (it.transactionAmount?.orZero() != 0f) || (roundOffAmount != 0f)
                }
            }
        )

        viewModel.roundOffStepsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                binding.tvScreenTitle.text = it.roundOffStepsData.header
                it.roundOffStepsData.roundOffSteps.let {
                    if (it.size >= 2) {
                        binding.tvAutomaticTitle.text = it[0].title
                        binding.tvAutomaticDesc.text = it[0].description
                        binding.tvManualTitle.text = it[1].title
                        binding.tvManualDesc.text = it[1].description
                        setViewsAccordingToFlowType(it)
                        toggleAutomaticPaymentSelection(isSelected = true)
                        toggleManualPaymentSelection(isSelected = false)
                    }
                }
            },
            onSuccessWithNullData = {
                dismissProgressBar()
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    private fun setViewsAccordingToFlowType(list: List<com.jar.app.feature_round_off.shared.domain.model.RoundOffSteps>) {
        automaticSetupType = list[0].roundOffSetupFlowViewType
        when (automaticSetupType) {
            com.jar.app.feature_round_off.shared.util.RoundOffConstants.RoundOffSetupFlowViewType.TEXT_ONLY.name -> {
                binding.tvAutomaticStepsHeader.text = list[0].stepsHeader
                binding.tvStartIconAutomatic.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.feature_round_off_bg_radio_selector
                )
                binding.elAutomatic.setPadding(19.dp, 16.dp, 27.dp, 0.dp)
                binding.tvAutomaticStepsHeader.isVisible = true
                binding.rvRoundOffStepsAutomatic.isVisible = true
                list[0].stepsList?.let { setAutomaticStepsAdapter(it) }
            }
            com.jar.app.feature_round_off.shared.util.RoundOffConstants.RoundOffSetupFlowViewType.LOTTIE.name -> {
                binding.tvStartIconAutomatic.background = ContextCompat.getDrawable(
                    requireContext(), com.jar.app.core_ui.R.drawable.core_ui_bg_radio_selector
                )
                binding.lottieAutomatic.isVisible = true
                binding.lottieAutomatic.playLottieWithUrlAndExceptionHandling(
                    requireContext(), list[0].lottie.orEmpty()
                )
            }
        }
        manualSetupType = list[1].roundOffSetupFlowViewType
        when (manualSetupType) {
            com.jar.app.feature_round_off.shared.util.RoundOffConstants.RoundOffSetupFlowViewType.TEXT_ONLY.name -> {
                binding.tvManualStepsHeader.text = list[1].stepsHeader
                binding.elManual.setPadding(19.dp, 16.dp, 27.dp, 0.dp)
                binding.tvStartIconManual.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.feature_round_off_bg_radio_selector
                )
                binding.tvManualStepsHeader.isVisible = true
                binding.rvRoundOffStepsManual.isVisible = true
                list[1].stepsList?.let { setManualStepsAdapter(it) }
            }
            com.jar.app.feature_round_off.shared.util.RoundOffConstants.RoundOffSetupFlowViewType.LOTTIE.name -> {
                binding.tvStartIconManual.background = ContextCompat.getDrawable(
                    requireContext(), com.jar.app.core_ui.R.drawable.core_ui_bg_radio_selector
                )
                binding.lottieManual.isVisible = true
                binding.lottieManual.playLottieWithUrlAndExceptionHandling(
                    requireContext(), list[1].lottie.orEmpty()
                )
            }
        }
    }

    private fun setAutomaticStepsAdapter(list: List<String>) {
        automaticRoundOffStepsAdapter = RoundOffStepsAdapter()
        binding.rvRoundOffStepsAutomatic.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRoundOffStepsAutomatic.adapter = automaticRoundOffStepsAdapter
        automaticRoundOffStepsAdapter?.submitList(list)
    }

    private fun setManualStepsAdapter(list: List<String>) {
        manualRoundOffStepsAdapter = RoundOffStepsAdapter()
        binding.rvRoundOffStepsManual.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRoundOffStepsManual.adapter = manualRoundOffStepsAdapter
        manualRoundOffStepsAdapter?.submitList(list)
    }

    private fun toggleAutomaticPaymentSelection(isSelected: Boolean) {
        when (automaticSetupType) {
            com.jar.app.feature_round_off.shared.util.RoundOffConstants.RoundOffSetupFlowViewType.TEXT_ONLY.name -> {
                binding.tvAutomaticDesc.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (isSelected) com.jar.app.core_ui.R.color.color_EEEAFF else com.jar.app.core_ui.R.color.color_ACA1D3
                    )
                )
                binding.llAutomatic.background = if (isSelected) ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.feature_round_off_bg_7a3cdf_12dp
                ) else null
                binding.clAutomaticContainer.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        if (isSelected) R.drawable.feature_round_off_bg_rounded_gradient_12dp else com.jar.app.core_ui.R.drawable.rounded_bg_3c3357_12dp
                    )
            }
            com.jar.app.feature_round_off.shared.util.RoundOffConstants.RoundOffSetupFlowViewType.LOTTIE.name -> {
                binding.clAutomaticContainer.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.feature_round_off_bg_7a3cdf_12dp
                )
                binding.clAutomaticContainer.backgroundTintList =
                    ContextCompat.getColorStateList(
                        requireContext(),
                        if (isSelected) com.jar.app.core_ui.R.color.color_3C3357 else com.jar.app.core_ui.R.color.lightBgColor
                    )
            }
        }
        binding.elAutomatic.isExpanded = isSelected
        binding.tvStartIconAutomatic.isSelected = isSelected
    }

    private fun toggleManualPaymentSelection(isSelected: Boolean) {
        when (manualSetupType) {
            com.jar.app.feature_round_off.shared.util.RoundOffConstants.RoundOffSetupFlowViewType.TEXT_ONLY.name -> {
                binding.tvManualDesc.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (isSelected) com.jar.app.core_ui.R.color.color_EEEAFF else com.jar.app.core_ui.R.color.color_ACA1D3
                    )
                )
                binding.llManual.background = if (isSelected) ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.feature_round_off_bg_7a3cdf_12dp
                ) else null
                binding.clManualContainer.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        if (isSelected) R.drawable.feature_round_off_bg_rounded_gradient_12dp else com.jar.app.core_ui.R.drawable.rounded_bg_3c3357_12dp
                    )
            }
            com.jar.app.feature_round_off.shared.util.RoundOffConstants.RoundOffSetupFlowViewType.LOTTIE.name -> {
                binding.clManualContainer.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.feature_round_off_bg_7a3cdf_12dp
                )
                binding.clManualContainer.backgroundTintList =
                    ContextCompat.getColorStateList(
                        requireContext(),
                        if (isSelected) com.jar.app.core_ui.R.color.color_3C3357 else com.jar.app.core_ui.R.color.lightBgColor
                    )
            }
        }
        binding.elManual.isExpanded = isSelected
        binding.tvStartIconManual.isSelected = isSelected
    }

    private fun setupManualRoundOffPayment(initiatePaymentResponse: InitiatePaymentResponse) {
        job?.cancel()
        job = appScope.launch(dispatcherProvider.main) {
            initiatePaymentResponse.screenSource = BaseConstants.ManualPaymentFlowType.RoundOffFlow
            paymentManager.initiateOneTimePayment(initiatePaymentResponse)
                .collectUnwrapped(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        if (it.getManualPaymentStatus() == com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.SUCCESS) {
                            viewModel.enableManualRoundOff()
                            val data = encodeUrl(serializer.encodeToString(it))
                            navigateTo(
                                "android-app://com.jar.app/roundOffPaymentSuccess/$data",
                                popUpTo = R.id.selectRoundOffSaveMethodFragment,
                                inclusive = true
                            )
                        } else {
                            val data = encodeUrl(serializer.encodeToString(it))
                            navigateTo(
                                "android-app://com.jar.app/roundOffPaymentPendingOrFailure/$data",
                                popUpTo = R.id.selectRoundOffSaveMethodFragment,
                                inclusive = true
                            )
                        }
                    },
                    onError = { message, errorCode ->
                        uiScope.launch {
                            whenResumed {
                                dismissProgressBar()
                                viewModel.fetchInitialRoundOffsData()
                                navigateTo(
                                    SelectRoundOffSaveMethodFragmentDirections.actionSelectRoundOffSaveMethodFragmentToRoundOffActivatedBottomSheet(
                                        roundOffAmount = initiatePaymentResponse.amount,
                                        isSpendsDetected = isSpendsDetected
                                    )
                                )
                            }
                        }
                    }
                )
        }
    }

    override fun onDestroyView() {
        binding.lottieManual.cancelAnimation()
        binding.lottieAutomatic.cancelAnimation()
        automaticRoundOffStepsAdapter = null
        manualRoundOffStepsAdapter = null
        super.onDestroyView()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onManualRoundOffPaymentEvent(manualRoundOffPaymentEvent: com.jar.app.feature_round_off.shared.domain.event.ManualRoundOffPaymentEvent) {
        setupManualRoundOffPayment(manualRoundOffPaymentEvent.initiatePaymentResponse)
    }

    override fun onDestroy() {
        job?.cancel()
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}
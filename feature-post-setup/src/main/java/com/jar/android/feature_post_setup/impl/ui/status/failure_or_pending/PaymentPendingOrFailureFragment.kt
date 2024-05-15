package com.jar.android.feature_post_setup.impl.ui.status.failure_or_pending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.whenResumed
import androidx.navigation.fragment.navArgs
import com.jar.android.feature_post_setup.R
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupFragmentPaymentPendingOrFailureBinding
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_post_setup.shared.PostSetupMR
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class PaymentPendingOrFailureFragment :
    BaseFragment<FeaturePostSetupFragmentPaymentPendingOrFailureBinding>(), BaseResources {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeaturePostSetupFragmentPaymentPendingOrFailureBinding
        get() = FeaturePostSetupFragmentPaymentPendingOrFailureBinding::inflate

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    private var job: Job? = null

    private val failureOrPendingData by lazy {
        serializer.decodeFromString<FailureOrPendingData>(decodeUrl(args.failureOrPendingData))
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateTo(
                    "android-app://com.jar.app/postSetupDetails",
                    popUpTo = R.id.paymentPendingOrFailureFragment,
                    inclusive = true
                )
            }
        }

    private val args: PaymentPendingOrFailureFragmentArgs by navArgs()
    private val viewModel: PaymentPendingOrFailureViewModel by viewModels()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        observeLiveData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        binding.tvGoldMessageTitle.text =
            failureOrPendingData.title ?: if (failureOrPendingData.isPendingFlow)
                getCustomStringFormatted(
                    PostSetupMR.strings.feature_post_setup_we_are_trying_to_buy_gold_worth_x,
                    failureOrPendingData.amount
                ) else getCustomStringFormatted(
                PostSetupMR.strings.feature_post_setup_we_are_unable_to_buy_gold_worth_x,
                failureOrPendingData.amount
            )
        binding.tvReason.text =
            failureOrPendingData.description ?: if (failureOrPendingData.isPendingFlow)
                getCustomString(
                    PostSetupMR.strings.feature_post_setup_dont_worry_we_will_notify_you
                ) else getCustomString(
                PostSetupMR.strings.feature_post_setup_if_money_was_debited
            )
        setupViewAccToFlow(failureOrPendingData.isPendingFlow)
    }

    private fun setupViewAccToFlow(isPendingFlow: Boolean) {
        binding.ivStatus.setImageResource(if (isPendingFlow) R.drawable.feature_post_setup_ic_payment_pending else R.drawable.feature_post_setup_ic_payment_failure)
        binding.btnRetryOrRefresh.setText(
            if (isPendingFlow) getCustomString(PostSetupMR.strings.feature_post_setup_refresh) else getCustomString(
                PostSetupMR.strings.feature_post_setup_retry
            )
        )
    }

    private fun setupListener() {
        binding.btnRetryOrRefresh.setDebounceClickListener {
            if (failureOrPendingData.isPendingFlow)
                viewModel.fetchManualPaymentStatus(
                    failureOrPendingData.transactionId.orEmpty(),
                    paymentManager.getCurrentPaymentGateway().name
                )
            else
                viewModel.initiateFailedPayment(
                    failureOrPendingData.amount,
                    paymentManager.getCurrentPaymentGateway().name,
                    failureOrPendingData.roundOffIds
                )
        }

        binding.btnGoToBackToMainScreen.setDebounceClickListener {
            navigateTo(
                "android-app://com.jar.app/postSetupDetails",
                popUpTo = R.id.paymentPendingOrFailureFragment,
                inclusive = true
            )
        }

        binding.tvContactSupport.setDebounceClickListener {
            val number = remoteConfigManager.getWhatsappNumber()
            val message = getCustomStringFormatted(
                PostSetupMR.strings.feature_post_setup_i_have_a_query_transaction_s_and_amount_s,
                failureOrPendingData.transactionId.orEmpty(),
                failureOrPendingData.amount.toString()
            )
            requireContext().openWhatsapp(number, message)
        }
    }

    private fun observeLiveData() {
        val weekReference: WeakReference<View> = WeakReference(binding.root)
        viewModel.fetchManualPaymentResponseLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weekReference,
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
            },
            onError = { dismissProgressBar() }
        )
        viewModel.failedPaymentLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weekReference,
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                performFailedTransactionPaymentEvent(it)
            },
            onError = { dismissProgressBar() }
        )
    }


    private fun performFailedTransactionPaymentEvent(initiatePaymentResponse: InitiatePaymentResponse) {
        job?.cancel()
        job = appScope.launch(dispatcherProvider.main) {
            initiatePaymentResponse.screenSource = BaseConstants.ManualPaymentFlowType.PostSetupFlow
            paymentManager.initiateOneTimePayment(initiatePaymentResponse)
                .collectUnwrapped(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        if (it.getManualPaymentStatus() == ManualPaymentStatus.SUCCESS) {
                            navigateTo(
                                "android-app://com.jar.app/postSetupSuccessStatus/${it.transactionId.orEmpty()}",
                                popUpTo = R.id.paymentPendingOrFailureFragment,
                                inclusive = true
                            )
                        } else {
                            val data = encodeUrl(
                                serializer.encodeToString(
                                    FailureOrPendingData(
                                        title = it.title,
                                        description = it.description,
                                        isPendingFlow = it.getManualPaymentStatus() == ManualPaymentStatus.PENDING,
                                        transactionId = it.transactionId,
                                        amount = it.amount.orZero(),
                                        roundOffIds = failureOrPendingData.roundOffIds,
                                    )
                                )
                            )
                            navigateTo(
                                "android-app://com.jar.app/postSetupStatus/$data",
                                popUpTo = R.id.postSetupDetailsFragment,
                                inclusive = true
                            )
                        }
                    },
                    onError = { message, errorCode ->
                        uiScope.launch {
                            whenResumed {
                                dismissProgressBar()
                                message.snackBar(binding.root)
                            }
                        }
                    }
                )
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }
}
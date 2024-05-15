package com.jar.app.feature_payment.impl.ui.upi_collect_timer

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.core.text.bold
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.dp
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_payment.PaymentNavigationDirections
import com.jar.app.feature_payment.R
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus
import com.jar.app.feature_payment.databinding.FragmentUpiCollectTimerBinding
import com.jar.app.feature_one_time_payments.shared.util.OneTimePaymentConstants
import com.jar.app.feature_payment.impl.domain.ManualPaymentStatusFetchedEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Duration
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class UpiCollectTimerFragment : BaseFragment<FragmentUpiCollectTimerBinding>() {

    private val viewModel by viewModels<UpiCollectTimerFragmentViewModel> { defaultViewModelProviderFactory }

    private val args by navArgs<UpiCollectTimerFragmentArgs>()

    private var fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse? = null

    private var isTransactionCancelled = false

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToConfirmationDialog()
            }
        }

    private var countDownTimerJob: Job? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUpiCollectTimerBinding
        get() = FragmentUpiCollectTimerBinding::inflate

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        registerBackPressDispatcher()
        setupListeners()
        observeLiveData()
    }

    private fun setupUI() {
        val interpolator = LinearInterpolator()
        val totalTime = Duration.ofMinutes(5).toMillis()

        binding.progressBarTimer.setProgressWithAnimation(
            progress = 0f,
            interpolator = interpolator,
            duration = totalTime
        )

        uiScope.countDownTimer(
            totalMillis = totalTime,
            onInterval = {
                val spannable = SpannableStringBuilder()
                    .append(getString(R.string.feature_payment_approve_payment_within))
                    .append(" ")
                    .bold { append(it.milliSecondsToCountDown()) }
                    .append(" ")
                    .append(getString(R.string.feature_payment_minutes))
                binding.tvTimer.text = spannable
            }
        )
    }

    private fun setupListeners() {
        countDownTimerJob?.cancel()
        countDownTimerJob = uiScope.countDownTimer(
            Duration.ofMinutes(5).toMillis(),
            intervalInMillis = Duration.ofSeconds(5).toMillis(),
            onInterval = {
                viewModel.fetchManualPaymentStatus(args.oneTimePaymentResult)
            },
            onFinished = {
                navigateToFailureBottomSheet(this.fetchManualPaymentStatusResponse!!)
            }
        )

        binding.btnBack.setDebounceClickListener {
            navigateToConfirmationDialog()
        }

        binding.btnCancel.setDebounceClickListener {
            navigateToConfirmationDialog()
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private fun observeLiveData() {
        viewModel.fetchManualPaymentStatusLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                this.fetchManualPaymentStatusResponse = it
                when {
                    isTransactionCancelled -> navigateToFailureBottomSheet(it)

                    it.getManualPaymentStatus() == ManualPaymentStatus.SUCCESS -> {
//                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
//                            Constants.PAYMENT_RESPONSE_KEY,
//                            it
//                        )
                        EventBus.getDefault().post(ManualPaymentStatusFetchedEvent(it))
                    }

                    it.getManualPaymentStatus() == ManualPaymentStatus.FAILURE -> navigateToFailureBottomSheet(
                        it
                    )

                    else -> {
                        //Do Nothing.. This is pending case
                    }
                }
            },
            onError = {
                popBackStack(args.paymentPageFragmentId, true)
            },
            translationY = -4.dp.toFloat()
        )

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Int>(OneTimePaymentConstants.CANCEL_TRANSACTION_ACTION)
            ?.observe(viewLifecycleOwner) {
                when (it) {
                    OneTimePaymentConstants.CancelTransactionAction.CONTINUE_PAYMENT -> {
                        //Do Nothing in this case
                    }

                    OneTimePaymentConstants.CancelTransactionAction.CANCEL_PAYMENT -> {
                        isTransactionCancelled = true
                        viewModel.fetchManualPaymentStatus(args.oneTimePaymentResult)
                    }
                }
            }
    }

    private fun navigateToConfirmationDialog() {
        navigateTo(
            PaymentNavigationDirections.actionToCancelTransactionDialog(
                args.oneTimePaymentResult.orderId
            )
        )
    }

    private fun navigateToFailureBottomSheet(it: FetchManualPaymentStatusResponse) {
        countDownTimerJob?.cancel()
        navigateTo(
            PaymentNavigationDirections.actionToTransactionFailedBottomSheet(
                args.oneTimePaymentResult,
                it,
                args.paymentPageFragmentId
            ),
            popUpTo = args.paymentPageFragmentId
        )
    }

    override fun onDestroyView() {
        countDownTimerJob?.cancel()
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }

}
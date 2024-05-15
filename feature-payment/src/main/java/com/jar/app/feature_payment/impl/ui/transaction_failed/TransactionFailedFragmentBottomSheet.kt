package com.jar.app.feature_payment.impl.ui.transaction_failed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_payment.PaymentNavigationDirections
import com.jar.app.feature_payment.R
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_payment.databinding.FragmentTransactionFailedBinding
import com.jar.app.feature_one_time_payments.shared.util.OneTimePaymentConstants
import com.jar.app.feature_payment.impl.domain.ManualPaymentStatusFetchedEvent
import com.jar.app.feature_payment.impl.domain.RetryManualPaymentEvent
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class TransactionFailedFragmentBottomSheet :
    BaseBottomSheetDialogFragment<FragmentTransactionFailedBinding>() {

    private val args by navArgs<TransactionFailedFragmentBottomSheetArgs>()

    private val viewModel by viewModels<TransactionFailedFragmentViewModel> { defaultViewModelProviderFactory }

    private var initiatePaymentResponse: InitiatePaymentResponse? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTransactionFailedBinding
        get() = FragmentTransactionFailedBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isHideable = false,
            skipCollapsed = true,
            isCancellable = false,
            isDraggable = false
        )

    override fun setup() {
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun setupUI() {
        binding.tvAmount.text =
            getString(R.string.feature_payment_amount_n, args.oneTimePaymentResult.amount)
    }

    private fun setupListeners() {
        binding.btnCancel.setDebounceClickListener {
            dismissAllowingStateLoss()
            /** [com.jar.app.feature_payment.impl.data.juspay.JuspayPaymentGatewayService] observe liveData from currentBackstackEntry's [SavedStateHandle]
             *  which is [com.jar.app.feature_payment.impl.ui.payment_option.PaymentOptionPageFragment]
             *  So make sure you set the data in PaymentOptionPageFragment's [SavedStateHandle]
             *  **/
            EventBus.getDefault().post(ManualPaymentStatusFetchedEvent(args.fetchManualPaymentStatusResponse))
        }

        binding.btnTryOtherMethods.setDebounceClickListener {
            viewModel.retryPayment(
                args.oneTimePaymentResult.orderId,
                args.oneTimePaymentResult.amount
            )
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(getRootView())
        viewModel.retryPaymentLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                this.initiatePaymentResponse = it

                if (args.oneTimePaymentResult.fetchCurrentGoldPriceResponse!!.price == it.fetchCurrentGoldPriceResponse?.price) {
                    //If price is same then redirect to payment page
                    dismissAllowingStateLoss()
                    popBackStack(args.paymentPageFragmentId, false)
                    EventBus.getDefault().post(RetryManualPaymentEvent(it))
                } else {
                    //Open Gold Price Changed Dialog
                    navigateTo(
                        PaymentNavigationDirections.actionToGoldPriceChangedDialog(
                            args.oneTimePaymentResult.amount.toFloat(),
                            args.oneTimePaymentResult.fetchCurrentGoldPriceResponse!!,
                            it.fetchCurrentGoldPriceResponse!!
                        )
                    )
                }
            },
            onError = {
                dismissProgressBar()
            }
        )

        if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            findNavController().currentBackStackEntry?.savedStateHandle
                ?.getLiveData<Int>(OneTimePaymentConstants.GOLD_PRICE_CHANGED_ACTION)
                ?.observe(viewLifecycleOwner) {
                    when (it) {
                        OneTimePaymentConstants.GoldPriceChangedAction.CANCEL_PAYMENT -> {
                            EventBus.getDefault().post(ManualPaymentStatusFetchedEvent(args.fetchManualPaymentStatusResponse))
                        }
                        OneTimePaymentConstants.GoldPriceChangedAction.CONTINUE_PAYMENT -> {
                            popBackStack(args.paymentPageFragmentId, false)
                            EventBus.getDefault().post(RetryManualPaymentEvent(initiatePaymentResponse!!))
                        }
                    }
                }
        }
    }
}
package com.jar.app.feature_payment.impl.ui.cancel_transction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.dp
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_payment.R
import com.jar.app.feature_payment.databinding.DialogCancelTransactionBinding
import com.jar.app.feature_one_time_payments.shared.util.OneTimePaymentConstants
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class CancelTransactionDialog :
    BaseBottomSheetDialogFragment<DialogCancelTransactionBinding>() {

    private val viewModel by viewModels<CancelTransactionDialogViewModel> { defaultViewModelProviderFactory }

    private val args by navArgs<CancelTransactionDialogArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DialogCancelTransactionBinding
        get() = DialogCancelTransactionBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isHideable = false,
            skipCollapsed = true,
            isCancellable = false,
            isDraggable = false
        )

    override fun setup() {
        setupListeners()
        observeLiveData()
    }

    private fun setupListeners() {
        binding.btnNotNow.setDebounceClickListener {
            dismissAllowingStateLoss()
            findNavController().getBackStackEntry(R.id.upiCollectTimerFragment)
                .savedStateHandle
                .set(
                    OneTimePaymentConstants.CANCEL_TRANSACTION_ACTION,
                    OneTimePaymentConstants.CancelTransactionAction.CONTINUE_PAYMENT
                )
        }

        binding.btnCancel.setDebounceClickListener {
            viewModel.cancelPayment(args.orderId)
        }
    }

    private fun observeLiveData() {
        viewModel.cancelPaymentLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(getRootView()),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                markPaymentCancelled()
            },
            onSuccessWithNullData = {
                markPaymentCancelled()
            },
            onError = {
                dismissProgressBar()
            },
            translationY = -4.dp.toFloat()
        )
    }

    private fun markPaymentCancelled() {
        dismissProgressBar()
        dismissAllowingStateLoss()
        findNavController().getBackStackEntry(R.id.upiCollectTimerFragment)
            .savedStateHandle
            .set(
                OneTimePaymentConstants.CANCEL_TRANSACTION_ACTION,
                OneTimePaymentConstants.CancelTransactionAction.CANCEL_PAYMENT
            )
    }
}
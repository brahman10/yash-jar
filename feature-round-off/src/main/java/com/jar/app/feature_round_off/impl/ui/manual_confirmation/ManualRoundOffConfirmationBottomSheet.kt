package com.jar.app.feature_round_off.impl.ui.manual_confirmation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffManualConfirmationBottomSheetBinding
import com.jar.app.feature_round_off.shared.domain.event.ManualRoundOffPaymentEvent
import com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest
import com.jar.app.feature_round_off.shared.util.RoundOffConstants
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class ManualRoundOffConfirmationBottomSheet :
    BaseBottomSheetDialogFragment<FeatureRoundOffManualConfirmationBottomSheetBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffManualConfirmationBottomSheetBinding
        get() = FeatureRoundOffManualConfirmationBottomSheetBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    @Inject
    lateinit var paymentManager: PaymentManager

    private val args: ManualRoundOffConfirmationBottomSheetArgs by navArgs()
    private val viewModel: ManualRoundOffConfirmationViewModel by viewModels()

    override fun setup() {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
        binding.tvRsValue.text = getString(com.jar.app.core_ui.R.string.core_ui_rs_x_float, args.roundOffAmount)
    }

    private fun setupListener() {
        binding.btnSaveNow.setDebounceClickListener {
            viewModel.initiateDetectedSpendPayment(
                com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest(
                    txnAmt = args.roundOffAmount.orZero(),
                    orderId = args.orderId.orEmpty()
                ),
                paymentManager.getCurrentPaymentGateway()
            )
        }

        binding.btnClose.setDebounceClickListener {
            dismiss()
        }
    }

    private fun observeLiveData() {
        viewModel.initiateDetectedSpendPaymentLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                it?.let {
                    EventBus.getDefault().post(
                        com.jar.app.feature_round_off.shared.domain.event.ManualRoundOffPaymentEvent(
                            it
                        )
                    )
                }
                dismiss()
            },
            onSuccessWithNullData = { dismissProgressBar() },
            onError = { dismissProgressBar() }
        )
    }
}
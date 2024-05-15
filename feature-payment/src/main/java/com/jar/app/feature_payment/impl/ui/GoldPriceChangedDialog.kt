package com.jar.app.feature_payment.impl.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addPercentage
import com.jar.app.core_base.util.roundDown
import com.jar.app.core_base.util.roundUp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_payment.R
import com.jar.app.feature_payment.databinding.FragmentGoldPriceChangedBinding
import com.jar.app.feature_one_time_payments.shared.util.OneTimePaymentConstants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class GoldPriceChangedDialog :
    BaseBottomSheetDialogFragment<FragmentGoldPriceChangedBinding>() {

    private val args by navArgs<GoldPriceChangedDialogArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldPriceChangedBinding
        get() = FragmentGoldPriceChangedBinding::inflate

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
    }

    private fun setupUI() {
        val hasPriceIncreased = args.newPriceResponse.price > args.oldPriceResponse.price

        binding.icIcon.setImageResource(
            if (hasPriceIncreased)
                R.drawable.feature_payment_ic_gold_price_increased
            else
                R.drawable.feature_payment_ic_gold_price_decreased
        )

        binding.tvHeaderGoldPrice.text = getString(
            if (hasPriceIncreased)
                R.string.feature_payment_gold_price_on_rise
            else
                R.string.feature_payment_gold_price_gone_down
        )

        binding.tvDesc.text = getString(
            R.string.feature_payment_for_amount_n_you_will_get_m_gm_of_gold,
            args.amount,
            calculateVolumeFromAmount(args.amount, args.newPriceResponse)
        )

        binding.tvUpdatedPrice.text = getString(
            R.string.feature_payment_updated_buy_price_n_per_gm,
            args.newPriceResponse.price
        )
    }

    private fun setupListeners() {
        binding.btnContinue.setDebounceClickListener {
            findNavController().getBackStackEntry(R.id.transactionFailedBottomSheet)
                .savedStateHandle.set(
                    OneTimePaymentConstants.GOLD_PRICE_CHANGED_ACTION,
                    OneTimePaymentConstants.GoldPriceChangedAction.CONTINUE_PAYMENT
                )
        }

        binding.btnMaybeLater.setDebounceClickListener {
            findNavController().getBackStackEntry(R.id.transactionFailedBottomSheet)
                .savedStateHandle.set(
                    OneTimePaymentConstants.GOLD_PRICE_CHANGED_ACTION,
                    OneTimePaymentConstants.GoldPriceChangedAction.CANCEL_PAYMENT
                )
        }
    }

    //TODO: Use this function from buy gold use case once that module is separated
    private fun calculateVolumeFromAmount(
        amount: Float,
        fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse
    ): Float {
        val currentPriceWithTax =
            fetchCurrentGoldPriceResponse.price
                .addPercentage(fetchCurrentGoldPriceResponse.applicableTax!!)
                .roundUp(2)

        return (amount / currentPriceWithTax).roundDown(4)
    }
}
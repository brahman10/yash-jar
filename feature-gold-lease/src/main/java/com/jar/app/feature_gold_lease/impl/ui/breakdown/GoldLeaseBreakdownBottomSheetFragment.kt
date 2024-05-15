package com.jar.app.feature_gold_lease.impl.ui.breakdown

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.getFormattedTextForOneFloatValueUptoOnePlace
import com.jar.app.base.util.roundOffDecimal
import com.jar.app.core_base.util.roundUp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeaseBreakdownBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class GoldLeaseBreakdownBottomSheetFragment : BaseBottomSheetDialogFragment<FragmentGoldLeaseBreakdownBottomSheetBinding>(){

    private val args by navArgs<GoldLeaseBreakdownBottomSheetFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeaseBreakdownBottomSheetBinding
        get() = FragmentGoldLeaseBreakdownBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun setup() {
        setupUI()
        setupListeners()
    }

    private fun setupListeners() {
        binding.ivClose.setDebounceClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun setupUI() {
        val totalGoldAmountWithoutTax = args.goldVolume * args.goldPrice
        binding.tvAmountHeader.text = getString(R.string.feature_gold_lease_balance_payable_for_x_gm, args.goldVolume)
        binding.tvAmount.text = getString(R.string.feature_gold_lease_rupee_prefix_string, totalGoldAmountWithoutTax.toDouble().roundOffDecimal().getFormattedAmount())
        binding.tvTotalAmount.text = getString(R.string.feature_gold_lease_rupee_prefix_string, args.totalPayableAmount.toDouble().roundOffDecimal().getFormattedAmount())
        binding.tvGSTAmount.text = getString(R.string.feature_gold_lease_rupee_prefix_string, (args.totalPayableAmount-totalGoldAmountWithoutTax).toDouble().roundOffDecimal().getFormattedAmount())
        binding.tvGSTAmountHeader.text = requireContext().getFormattedTextForOneFloatValueUptoOnePlace(
            R.string.feature_gold_lease_gst_x_percent,
            args.gstPercent
        )
    }
}
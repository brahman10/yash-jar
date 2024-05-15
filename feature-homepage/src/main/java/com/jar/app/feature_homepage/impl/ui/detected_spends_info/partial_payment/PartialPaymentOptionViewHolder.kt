package com.jar.app.feature_homepage.impl.ui.detected_spends_info.partial_payment

import android.graphics.Paint
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_user_api.domain.model.PartPaymentOption
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellPartialPaymentBinding

internal class PartialPaymentOptionViewHolder(
    private val binding: FeatureHomepageCellPartialPaymentBinding,
    private val onClick: (partPaymentOption: PartPaymentOption) -> Unit
) : BaseViewHolder(binding.root) {

    init {
        binding.root.setDebounceClickListener {
            item?.let(onClick)
        }
    }

    private var item: PartPaymentOption? = null

    fun setOption(totalAmount: Float, partPaymentOption: PartPaymentOption) {
        this.item = partPaymentOption
        binding.tvInvestPercent.text = context.getString(
            R.string.feature_homepage_invest_n_percent,
            partPaymentOption.percentage
        )
        binding.tvOriginalAmount.text =
            context.getString(R.string.feature_homepage_rupee_x_in_double, totalAmount)
        binding.tvReducedAmount.text = context.getString(
            R.string.feature_homepage_rupee_x_in_double_strike,
            partPaymentOption.amount
        )
        binding.tvOriginalAmount.paintFlags =
            binding.tvOriginalAmount.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }
}
package com.jar.app.feature_round_off.impl.ui.initial_round_off

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_round_off.databinding.FeatureRoundOffCellDetectedRoundOffBinding
import com.jar.app.feature_round_off.shared.MR

internal class InitialRoundOffViewHolder(private val binding: FeatureRoundOffCellDetectedRoundOffBinding) :
    BaseViewHolder(binding.root) {

    fun setTransactionBreakup(transaction: com.jar.app.feature_round_off.shared.domain.model.Transaction) {
        binding.tvPaidAmount.text = getCustomStringFormatted(
            MR.strings.feature_round_off_paid_x,
            transaction.amount.orZero()
        )
        binding.tvRoundOffAmount.text = context.getString(
            com.jar.app.core_ui.R.string.core_ui_rs_x_int,
            transaction.roundOffAmount?.toInt()
        )
        binding.tvRoundOffAmount.text = context.getString(
            com.jar.app.core_ui.R.string.core_ui_rs_x_int,
            transaction.roundOffAmount?.toInt()
        )
        binding.tvDate.text = transaction.timestamp
        binding.tvRoundedOffToo.text =
            getCustomStringFormatted(
                MR.strings.feature_round_off_rounded_off_to_x,
                transaction.roundedOffTo.orZero()
            )

        binding.tvPaidTo.isVisible = transaction.merchant != null
        binding.tvPaidTo.text = transaction.merchant

        binding.clCategoryContainer.isVisible =
            (transaction.categoryInfo != null) && (transaction.merchant != null)

        transaction.categoryInfo?.let {
            binding.clCategoryContainer.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(it.bgColor))
            binding.tvCategory.setTextColor(ColorStateList.valueOf(Color.parseColor(it.textColor)))
            binding.tvCategory.text = it.categoryName
            binding.ivCategoryIcon.isVisible = it.iconUrl.isNullOrEmpty().not()
            it.iconUrl?.let {
                Glide.with(binding.root.context).load(it).into(binding.ivCategoryIcon)
            }
        }
    }
}
package com.jar.app.feature_mandate_payment.impl.ui.payment_page.view_holder.description

import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentCellMandateEducationItemBinding
import com.jar.app.feature_mandate_payments_common.shared.domain.model.mandate_help.MandateEducationItem

internal class EducationItemViewHolder(private val binding: FeatureMandatePaymentCellMandateEducationItemBinding) :
    BaseViewHolder(binding.root) {

    fun setMandateEducationItem(mandateEducationItem: MandateEducationItem, listSize: Int) {
        binding.tvTitle.text = mandateEducationItem.title
        Glide
            .with(binding.root.context)
            .load(mandateEducationItem.imageUrl)
            .into(binding.ivIcon)

        binding.topLine.isVisible = (bindingAdapterPosition != 0)
        binding.bottomLine.isVisible = (bindingAdapterPosition != listSize - 1)
    }
}
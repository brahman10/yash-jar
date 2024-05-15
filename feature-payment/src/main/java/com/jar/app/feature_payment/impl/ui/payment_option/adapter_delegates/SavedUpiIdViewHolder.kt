package com.jar.app.feature_payment.impl.ui.payment_option.adapter_delegates

import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_payment.R
import com.jar.app.feature_payment.databinding.CellSavedUpiIdBinding

internal class SavedUpiIdViewHolder(
    private val binding: CellSavedUpiIdBinding,
    private val onClick: (position: Int) -> Unit,
    private val onPayClick: (upiId: String) -> Unit,
) :
    BaseViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onClick.invoke(bindingAdapterPosition)
        }

        binding.btnPay.setDebounceClickListener {
            upiId?.let(onPayClick)
        }
    }

    private var upiId: String? = null

    fun bind(upiId: String) {
        this.upiId = upiId
        binding.tvUpiAddress.text = upiId
    }

    fun toggle() {
        binding.expandableLayout.toggle(true)
        if (binding.expandableLayout.isExpanded)
            binding.ivSelected.setImageResource(R.drawable.feature_payment_ic_tick_green)
        else
            binding.ivSelected.setImageResource(R.drawable.feature_payment_bg_circle_gray_border_only)
    }

    fun collapse() {
        binding.expandableLayout.collapse(true)
        binding.ivSelected.setImageResource(R.drawable.feature_payment_bg_circle_gray_border_only)
    }
}
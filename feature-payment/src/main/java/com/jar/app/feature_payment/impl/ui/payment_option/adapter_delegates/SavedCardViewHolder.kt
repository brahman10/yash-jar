package com.jar.app.feature_payment.impl.ui.payment_option.adapter_delegates

import com.bumptech.glide.Glide
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_payment.R
import com.jar.app.feature_payment.databinding.CellSavedCardBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.SavedCard

internal class SavedCardViewHolder(
    private val binding: CellSavedCardBinding,
    private val onClick: (position: Int) -> Unit,
    private val onPayClick: (savedCard: SavedCard) -> Unit,
) :
    BaseViewHolder(binding.root) {

    private var savedCard: SavedCard? = null

    init {
        binding.root.setOnClickListener {
            onClick.invoke(bindingAdapterPosition)
        }

        binding.btnPay.setDebounceClickListener {
            savedCard?.let(onPayClick)
        }
    }

    fun setCard(savedCard: SavedCard) {
        this.savedCard = savedCard
        binding.tvCardNumber.text = savedCard.getFormattedCardNumber()

        Glide.with(itemView)
            .load(savedCard.getCardBrandImageUrl())
            .into(binding.ivCardBrand)
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
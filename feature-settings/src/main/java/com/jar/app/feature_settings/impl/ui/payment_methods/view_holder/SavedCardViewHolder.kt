package com.jar.app.feature_settings.impl.ui.payment_methods.view_holder

import com.bumptech.glide.Glide
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_settings.databinding.CellSavedCardMethodBinding
import com.jar.app.feature_settings.domain.model.SavedCard

internal class SavedCardViewHolder(
    private val binding: CellSavedCardMethodBinding,
    private val onDeleteClick: (savedCard: SavedCard, position: Int) -> Unit
) :
    BaseViewHolder(binding.root) {

    private var savedCard: SavedCard? = null

    init {
        binding.ivDelete.setOnClickListener {
            savedCard?.let { onDeleteClick.invoke(it, bindingAdapterPosition) }
        }
    }

    fun setCard(savedCard: SavedCard) {
        this.savedCard = savedCard
        binding.tvCardNumber.text = savedCard.getFormattedCardNumber()

        Glide.with(itemView)
            .load(savedCard.getCardBrandImageUrl())
            .into(binding.ivCardBrand)
    }


}
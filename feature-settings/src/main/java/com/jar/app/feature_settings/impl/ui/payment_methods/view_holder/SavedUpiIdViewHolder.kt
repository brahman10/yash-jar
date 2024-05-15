package com.jar.app.feature_settings.impl.ui.payment_methods.view_holder

import com.bumptech.glide.Glide
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_settings.databinding.CellSavedUpiIdMethodBinding
import com.jar.app.feature_user_api.domain.model.SavedVPA

internal class SavedUpiIdViewHolder(
    private val binding: CellSavedUpiIdMethodBinding,
    private val onDeleteClick: (savedVPA: SavedVPA, position: Int) -> Unit
) :
    BaseViewHolder(binding.root) {

    private var savedVPA: SavedVPA? = null

    init {
        binding.ivDelete.setOnClickListener {
            savedVPA?.let { onDeleteClick.invoke(it, bindingAdapterPosition) }
        }

    }

    fun setCard(savedVPA: SavedVPA) {
        this.savedVPA = savedVPA
        binding.tvUpiId.text = savedVPA.vpaHandle

        Glide.with(itemView)
            .load(com.jar.app.core_ui.R.drawable.core_upi_ic_upi)
            .into(binding.ivUpiIcon)
    }


}
package com.jar.app.feature_settings.impl.ui.payment_methods.adapter_delegate.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_settings.databinding.CellSavedCardMethodBinding
import com.jar.app.feature_settings.domain.model.SavedCard
import com.jar.app.feature_settings.impl.ui.payment_methods.view_holder.SavedCardViewHolder

internal class SavedCardAdapter(
    private val onDeleteClick: (savedCard: SavedCard, position: Int) -> Unit
) : ListAdapter<SavedCard, SavedCardViewHolder>(DIFF_UTIL) {
    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<SavedCard>() {
            override fun areItemsTheSame(oldItem: SavedCard, newItem: SavedCard): Boolean {
                return oldItem.cardNumber == newItem.cardNumber
            }

            override fun areContentsTheSame(oldItem: SavedCard, newItem: SavedCard): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedCardViewHolder {
        val binding = CellSavedCardMethodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedCardViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: SavedCardViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setCard(it)
        }
    }
}
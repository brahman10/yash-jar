package com.jar.app.feature_payment.impl.ui.payment_option.adapter_delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_payment.databinding.CellSavedCardBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.SavedCard

internal class SavedCardAdapter(
    private val onClick: (position: Int) -> Unit,
    private val onPayClick: (savedCard: SavedCard) -> Unit,
) : ListAdapter<SavedCard, SavedCardViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<SavedCard>() {
            override fun areItemsTheSame(oldItem: SavedCard, newItem: SavedCard): Boolean {
                return oldItem.cardFingerprint == newItem.cardFingerprint
            }

            override fun areContentsTheSame(oldItem: SavedCard, newItem: SavedCard): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedCardViewHolder {
        val binding = CellSavedCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedCardViewHolder(binding, onClick, onPayClick)
    }

    override fun onBindViewHolder(holder: SavedCardViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setCard(it)
        }
    }
}
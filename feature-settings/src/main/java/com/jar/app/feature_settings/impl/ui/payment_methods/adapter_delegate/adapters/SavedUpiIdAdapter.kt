package com.jar.app.feature_settings.impl.ui.payment_methods.adapter_delegate.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_settings.databinding.CellSavedUpiIdMethodBinding
import com.jar.app.feature_settings.impl.ui.payment_methods.view_holder.SavedUpiIdViewHolder
import com.jar.app.feature_user_api.domain.model.SavedVPA

internal class SavedUpiIdAdapter(
    private val onDeleteClick: (savedVpa: SavedVPA, position: Int) -> Unit
) : ListAdapter<SavedVPA, SavedUpiIdViewHolder>(DIFF_UTIL) {
    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<SavedVPA>() {
            override fun areItemsTheSame(oldItem: SavedVPA, newItem: SavedVPA): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SavedVPA, newItem: SavedVPA): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedUpiIdViewHolder {
        val binding = CellSavedUpiIdMethodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedUpiIdViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: SavedUpiIdViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setCard(it)
        }
    }
}
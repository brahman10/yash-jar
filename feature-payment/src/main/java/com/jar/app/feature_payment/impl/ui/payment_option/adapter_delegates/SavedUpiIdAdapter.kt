package com.jar.app.feature_payment.impl.ui.payment_option.adapter_delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_payment.databinding.CellSavedUpiIdBinding

internal class SavedUpiIdAdapter(
    private val onClick: (position: Int) -> Unit,
    private val onPayClick: (upiId: String) -> Unit
) : ListAdapter<String, SavedUpiIdViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {

            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedUpiIdViewHolder {
        val binding = CellSavedUpiIdBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedUpiIdViewHolder(binding,onClick, onPayClick)
    }

    override fun onBindViewHolder(holder: SavedUpiIdViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}
package com.jar.app.feature_transaction.impl.ui.retry

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellVpaChipBinding

class VpaChipAdapter(
    private val onClick: (string: String) -> Unit
) : ListAdapter<String, VpaChipViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VpaChipViewHolder(
        FeatureTransactionCellVpaChipBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClick
    )

    override fun onBindViewHolder(holder: VpaChipViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setVpaChip(it)
        }
    }
}
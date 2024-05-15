package com.jar.app.feature_transaction.impl.ui.retry

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellSelectUpiAddressBinding
import com.jar.app.feature_user_api.domain.model.SavedVPA

class SelectUpiAddressAdapter(
    private val onClick: (savedVPA: SavedVPA) -> Unit
) : ListAdapter<SavedVPA, SelectUpiAddressAdapter.SelectUpiAddressViewHolder>(DIFF_UTIL) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SelectUpiAddressViewHolder(
        FeatureTransactionCellSelectUpiAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: SelectUpiAddressViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class SelectUpiAddressViewHolder(
        private val binding: FeatureTransactionCellSelectUpiAddressBinding
    ) : BaseViewHolder(binding.root) {
        fun bindData(savedVPA: SavedVPA) {
            binding.checkVpa.text = savedVPA.vpaHandle
            binding.checkVpa.isChecked = savedVPA.isSelected.orFalse()
            binding.root.setDebounceClickListener {
                onClick(savedVPA)
            }
        }
    }
}
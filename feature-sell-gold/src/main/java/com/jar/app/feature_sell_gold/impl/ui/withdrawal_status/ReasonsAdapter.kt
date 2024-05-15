package com.jar.app.feature_sell_gold.impl.ui.withdrawal_status

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_sell_gold.databinding.FeatureSellGoldCellReasonBinding

internal class ReasonsAdapter(
    private val onClick: (string: String) -> Unit
) : ListAdapter<String,ReasonsAdapter.ReasonsViewHolder>(DIFF_UTIL) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ReasonsViewHolder(
        FeatureSellGoldCellReasonBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClick
    )

    override fun onBindViewHolder(holder: ReasonsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setReasonChip(it)
        }
    }

    inner class ReasonsViewHolder(
        private val binding: FeatureSellGoldCellReasonBinding,
        private val onClick: (string: String) -> Unit
    ) : BaseViewHolder(binding.root) {

        private lateinit var selectedReason: String

        init {
            binding.tvReason.setDebounceClickListener {
                if (::selectedReason.isInitialized)
                    onClick.invoke(selectedReason)
            }
        }

        fun setReasonChip(reason: String) {
            selectedReason = reason
            binding.tvReason.text = reason
        }
    }
}
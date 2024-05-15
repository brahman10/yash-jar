package com.jar.app.feature_sell_gold.impl.ui.bottomsheet

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_sell_gold.databinding.FeatureSellGoldBottomsheetRvViewBinding
import com.jar.app.feature_sell_gold.shared.domain.models.Steps

class WithdrawBottomSheetAdapter(
) :
    ListAdapter<Steps, WithdrawBottomSheetViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Steps>() {
            override fun areItemsTheSame(oldItem: Steps, newItem: Steps): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Steps, newItem: Steps): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WithdrawBottomSheetViewHolder {
        val binding = FeatureSellGoldBottomsheetRvViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WithdrawBottomSheetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WithdrawBottomSheetViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

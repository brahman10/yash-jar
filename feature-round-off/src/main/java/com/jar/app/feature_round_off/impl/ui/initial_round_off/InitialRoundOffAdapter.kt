package com.jar.app.feature_round_off.impl.ui.initial_round_off

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_round_off.databinding.FeatureRoundOffCellDetectedRoundOffBinding
import com.jar.app.feature_round_off.shared.domain.model.Transaction

internal class InitialRoundOffAdapter :
    ListAdapter<com.jar.app.feature_round_off.shared.domain.model.Transaction, InitialRoundOffViewHolder>(ITEM_CALLBACK) {

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<com.jar.app.feature_round_off.shared.domain.model.Transaction>() {
            override fun areItemsTheSame(oldItem: com.jar.app.feature_round_off.shared.domain.model.Transaction, newItem: com.jar.app.feature_round_off.shared.domain.model.Transaction): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: com.jar.app.feature_round_off.shared.domain.model.Transaction, newItem: com.jar.app.feature_round_off.shared.domain.model.Transaction): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InitialRoundOffViewHolder {
        val binding =
            FeatureRoundOffCellDetectedRoundOffBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        return InitialRoundOffViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InitialRoundOffViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setTransactionBreakup(it)
        }
    }
}
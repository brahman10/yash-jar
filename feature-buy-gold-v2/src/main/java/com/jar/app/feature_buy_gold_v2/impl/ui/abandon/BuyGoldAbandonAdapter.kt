package com.jar.app.feature_buy_gold_v2.impl.ui.abandon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_buy_gold_v2.databinding.FeatureBuyGoldAbandonStepCellBinding
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldAbandonSteps


internal class BuyGoldAbandonAdapter:
    ListAdapter<BuyGoldAbandonSteps, BuyGoldAbandonViewHolder>(DIFF_CALLBACK){

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BuyGoldAbandonSteps>() {
            override fun areItemsTheSame(oldItem: BuyGoldAbandonSteps, newItem: BuyGoldAbandonSteps): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: BuyGoldAbandonSteps, newItem: BuyGoldAbandonSteps): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BuyGoldAbandonViewHolder(FeatureBuyGoldAbandonStepCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: BuyGoldAbandonViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}
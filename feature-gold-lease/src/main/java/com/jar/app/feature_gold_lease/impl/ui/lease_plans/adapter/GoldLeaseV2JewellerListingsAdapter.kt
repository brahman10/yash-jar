package com.jar.app.feature_gold_lease.impl.ui.lease_plans.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_lease.databinding.CellGoldLeaseV2JewellerListingsBinding

internal class GoldLeaseV2JewellerListingsAdapter : ListAdapter<String, GoldLeaseV2JewellerListingsAdapter.GoldLeaseV2JewellerListingsViewHolder>(
    DIFF_UTIL
){

    companion object{
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return  oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class GoldLeaseV2JewellerListingsViewHolder (
        private val binding: CellGoldLeaseV2JewellerListingsBinding
    ): BaseViewHolder(binding.root) {
        fun bind(data: String) {
            Glide.with(context).load(data).into(binding.ivJewellerIcon)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoldLeaseV2JewellerListingsViewHolder {
        val binding = CellGoldLeaseV2JewellerListingsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoldLeaseV2JewellerListingsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoldLeaseV2JewellerListingsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}
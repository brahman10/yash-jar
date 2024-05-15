package com.jar.app.feature_gold_lease.impl.ui.post_order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_lease.databinding.CellGoldLeasePostOrderDetailsBinding
import com.jar.app.feature_gold_lease.shared.domain.model.LeasePostOrderDetailsItemList

internal class GoldLeaseOrderDetailsAdapter :
    ListAdapter<LeasePostOrderDetailsItemList, GoldLeaseOrderDetailsViewHolder>(
        DIFF_UTIL
    ) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<LeasePostOrderDetailsItemList>() {
            override fun areItemsTheSame(
                oldItem: LeasePostOrderDetailsItemList,
                newItem: LeasePostOrderDetailsItemList
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: LeasePostOrderDetailsItemList,
                newItem: LeasePostOrderDetailsItemList
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoldLeaseOrderDetailsViewHolder {
        val binding = CellGoldLeasePostOrderDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return GoldLeaseOrderDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoldLeaseOrderDetailsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}
package com.jar.app.feature_gold_lease.impl.ui.new_lease_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_lease.databinding.CellGoldLeaseComparisonBinding
import com.jar.app.feature_gold_lease.shared.domain.model.LeaseComparisonTableRowsList

internal class GoldLeaseComparisonAdapter :
    ListAdapter<LeaseComparisonTableRowsList, GoldLeaseComparisonViewHolder>(
        DIFF_UTIL
    ) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<LeaseComparisonTableRowsList>() {
            override fun areItemsTheSame(
                oldItem: LeaseComparisonTableRowsList,
                newItem: LeaseComparisonTableRowsList
            ): Boolean {
                return newItem.rowTitle.orEmpty() == oldItem.rowTitle.orEmpty()
            }

            override fun areContentsTheSame(
                oldItem: LeaseComparisonTableRowsList,
                newItem: LeaseComparisonTableRowsList
            ): Boolean {
                return newItem.rowTitle.orEmpty() == oldItem.rowTitle.orEmpty()
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoldLeaseComparisonViewHolder {
        val binding = CellGoldLeaseComparisonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoldLeaseComparisonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoldLeaseComparisonViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}
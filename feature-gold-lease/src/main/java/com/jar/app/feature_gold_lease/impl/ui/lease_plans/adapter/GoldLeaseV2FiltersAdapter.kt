package com.jar.app.feature_gold_lease.impl.ui.lease_plans.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_lease.databinding.CellGoldLeaseFilterBinding
import com.jar.app.feature_gold_lease.shared.domain.model.LeasePlanFilterInfoList

internal class GoldLeaseV2FiltersAdapter(
    private val onFilterClicked: (leasePlanFilterInfoList: LeasePlanFilterInfoList) -> Unit
): ListAdapter<LeasePlanFilterInfoList, GoldLeaseV2FiltersViewHolder>(
    DIFF_UTIL
) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<LeasePlanFilterInfoList>() {
            override fun areItemsTheSame(
                oldItem: LeasePlanFilterInfoList,
                newItem: LeasePlanFilterInfoList
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: LeasePlanFilterInfoList,
                newItem: LeasePlanFilterInfoList
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoldLeaseV2FiltersViewHolder {
        val binding = CellGoldLeaseFilterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoldLeaseV2FiltersViewHolder(binding, onFilterClicked)
    }

    override fun onBindViewHolder(holder: GoldLeaseV2FiltersViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}
package com.jar.app.feature_gold_lease.impl.ui.lease_plans.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.jar.app.feature_gold_lease.databinding.CellGoldLeasePlanBinding
import com.jar.app.feature_gold_lease.shared.domain.model.LeasePlanList

internal class GoldLeaseV2PlansAdapter(
    private val onInfoClicked: (leasePlanList: LeasePlanList) -> Unit,
    private val onSelectClicked: (leasePlanList: LeasePlanList) -> Unit,
    private val onRandomElementClicked: (elementName: String, data: String) -> Unit
): PagingDataAdapter<LeasePlanList, GoldLeaseV2PlansViewHolder>(
    DIFF_UTIL
) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<LeasePlanList>() {
            override fun areItemsTheSame(oldItem: LeasePlanList, newItem: LeasePlanList): Boolean {
                return oldItem.jewellerId == newItem.jewellerId
            }

            override fun areContentsTheSame(
                oldItem: LeasePlanList,
                newItem: LeasePlanList
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoldLeaseV2PlansViewHolder {
        val binding = CellGoldLeasePlanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoldLeaseV2PlansViewHolder(binding, onInfoClicked, onSelectClicked, onRandomElementClicked)
    }

    override fun onBindViewHolder(holder: GoldLeaseV2PlansViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}
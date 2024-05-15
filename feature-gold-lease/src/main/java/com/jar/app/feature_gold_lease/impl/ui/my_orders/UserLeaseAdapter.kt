package com.jar.app.feature_gold_lease.impl.ui.my_orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.jar.app.feature_gold_lease.databinding.CellUserLeaseBinding
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2UserLeaseItem

internal class UserLeaseAdapter(
    private val onLeaseClicked: (userLease: GoldLeaseV2UserLeaseItem) -> Unit
): PagingDataAdapter<GoldLeaseV2UserLeaseItem, UserLeaseViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<GoldLeaseV2UserLeaseItem>() {
            override fun areItemsTheSame(
                oldItem: GoldLeaseV2UserLeaseItem,
                newItem: GoldLeaseV2UserLeaseItem
            ): Boolean {
                return oldItem.leaseId == newItem.leaseId
            }

            override fun areContentsTheSame(
                oldItem: GoldLeaseV2UserLeaseItem,
                newItem: GoldLeaseV2UserLeaseItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(holder: UserLeaseViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserLeaseViewHolder {
        val binding = CellUserLeaseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return UserLeaseViewHolder(binding, onLeaseClicked)
    }

}
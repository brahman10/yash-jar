package com.jar.app.feature_gold_lease.impl.ui.jeweller_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_lease.databinding.CellGoldLeaseTitleValuePairBinding
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2TitleValuePair

internal class GoldLeaseV2TitleValuePairAdapter(
    private val onClickedCopyTransactionId : (goldLeaseV2TitleValuePair: GoldLeaseV2TitleValuePair) -> Unit,
    private val onWebsiteClicked : (link: String) -> Unit
) :
    ListAdapter<GoldLeaseV2TitleValuePair, GoldLeaseV2TitleValuePairViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<GoldLeaseV2TitleValuePair>() {
            override fun areItemsTheSame(
                oldItem: GoldLeaseV2TitleValuePair,
                newItem: GoldLeaseV2TitleValuePair
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: GoldLeaseV2TitleValuePair,
                newItem: GoldLeaseV2TitleValuePair
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoldLeaseV2TitleValuePairViewHolder {
        val binding = CellGoldLeaseTitleValuePairBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoldLeaseV2TitleValuePairViewHolder(binding, onClickedCopyTransactionId, onWebsiteClicked)
    }

    override fun onBindViewHolder(holder: GoldLeaseV2TitleValuePairViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}
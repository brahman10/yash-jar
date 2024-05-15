package com.jar.app.core_ui.expandable_rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.core_base.domain.model.ExpandableDataItem
import com.jar.app.core_base.domain.model.ExpandableRVViewTypes
import com.jar.app.core_ui.databinding.CardContainerExpandableLayoutBinding
import com.jar.app.core_ui.databinding.ExpandableFaqRvLayoutBinding
import com.jar.app.core_ui.databinding.LeftIconWithSeperatorExpandableLayoutBinding

class DailySavingsV2ExpandableFaqAdapter(
    private val onItemClick: (Int) -> Unit
) : ListAdapter<ExpandableDataItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ExpandableDataItem>() {
            override fun areItemsTheSame(oldItem: ExpandableDataItem, newItem: ExpandableDataItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ExpandableDataItem, newItem: ExpandableDataItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType.ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (ExpandableRVViewTypes.values()[viewType]) {
            ExpandableRVViewTypes.DEFAULT_BANNER_BG -> {
                val binding = ExpandableFaqRvLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DefaultBannerBgViewHolder(binding, onItemClick)
            }
            ExpandableRVViewTypes.CARD_HEADER -> {
                val binding = CardContainerExpandableLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CardExpandableFaqViewHolder(binding, onItemClick)
            }
            ExpandableRVViewTypes.LEFT_ICON_WITH_SEPERATOR -> {
                val binding = LeftIconWithSeperatorExpandableLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LeftIconExpandableFaqViewHolder(binding, onItemClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        val viewType =  getItemViewType(position)
        when (ExpandableRVViewTypes.values()[viewType]) {
            ExpandableRVViewTypes.DEFAULT_BANNER_BG -> {
                (holder as DefaultBannerBgViewHolder).bind(data as ExpandableDataItem.DefaultBannerWithBGIsExpandedDataType)
            }
            ExpandableRVViewTypes.CARD_HEADER -> {
                (holder as CardExpandableFaqViewHolder).bind(data as ExpandableDataItem.CardHeaderIsExpandedDataType)
            }
            ExpandableRVViewTypes.LEFT_ICON_WITH_SEPERATOR -> {
                (holder as LeftIconExpandableFaqViewHolder).bind(data as ExpandableDataItem.LeftIconIsExpandedDataType)
            }
        }
    }
}
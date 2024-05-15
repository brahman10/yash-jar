package com.jar.app.feature_homepage.impl.ui.viba

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellVibaEndItemBinding
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellVibaPrimeryItemBinding
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellVibaSecondaryItemBinding
import com.jar.app.feature_homepage.shared.domain.model.viba.VibaCardType
import com.jar.app.feature_homepage.shared.domain.model.viba.VibaHorizontalCardData
import kotlinx.coroutines.CoroutineScope

internal class VibaCardRecyclerAdapter(
    private val uiScope: CoroutineScope,
    private val onItemClick: (VibaHorizontalCardData) -> Unit
) : ListAdapter<VibaHorizontalCardData, RecyclerView.ViewHolder>(DIFF_UTIL) {
    companion object {
        const val FIRST = 1
        const val MIDDLE = 2
        const val END = 3
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<VibaHorizontalCardData>() {
            override fun areItemsTheSame(oldItem: VibaHorizontalCardData, newItem: VibaHorizontalCardData): Boolean {
                return oldItem.icon == newItem.icon
            }

            override fun areContentsTheSame(oldItem: VibaHorizontalCardData, newItem: VibaHorizontalCardData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FIRST -> FirstCardViewHolder(
                FeatureHomepageCellVibaPrimeryItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                uiScope,
                onItemClick
            )
            MIDDLE -> MiddleCardViewHolder(
                FeatureHomepageCellVibaSecondaryItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onItemClick
            )
            END -> EndCardViewHolder(
                FeatureHomepageCellVibaEndItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onItemClick
            )
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FirstCardViewHolder -> {
                holder.binding(getItem(position))
            }

            is MiddleCardViewHolder -> {
                holder.binding(getItem(position))
            }

            is EndCardViewHolder -> {
                holder.binding(getItem(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).cardType) {
            VibaCardType.FIRST.name -> FIRST
            VibaCardType.MIDDLE.name -> MIDDLE
            VibaCardType.END.name -> END
            else -> VibaCardType.MIDDLE.ordinal
        }
    }
}
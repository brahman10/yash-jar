package com.jar.app.feature_jar_duo.impl.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_jar_duo.databinding.FeatureDuoPendingDuoHeaderBinding
import com.jar.app.feature_jar_duo.shared.domain.model.v2.DuoHeaderData
import com.jar.app.feature_jar_duo.impl.ui.duo_list.DuoHeaderViewHolder

internal class DuoHeaderAdapter(
    private val showViewAll: Boolean,
    private val onViewAllClicked: () -> Unit = { }
) : ListAdapter<com.jar.app.feature_jar_duo.shared.domain.model.v2.DuoHeaderData, DuoHeaderViewHolder>(ITEM_CALLBACK) {

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<com.jar.app.feature_jar_duo.shared.domain.model.v2.DuoHeaderData>() {
            override fun areItemsTheSame(oldItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.DuoHeaderData, newItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.DuoHeaderData): Boolean {
                return oldItem.itemCount == newItem.itemCount
            }

            override fun areContentsTheSame(
                oldItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.DuoHeaderData,
                newItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.DuoHeaderData
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuoHeaderViewHolder {
        val binding =
            FeatureDuoPendingDuoHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return DuoHeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DuoHeaderViewHolder, position: Int) {
        getItem(position)?.let {
            holder.onBind(it, onViewAllClicked, showViewAll = showViewAll)
        }
    }
}
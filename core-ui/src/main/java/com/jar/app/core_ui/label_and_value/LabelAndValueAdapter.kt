package com.jar.app.core_ui.label_and_value

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.databinding.CoreUiCellLabelAndValueBinding

class LabelAndValueAdapter (
    private val onValueIconCLick: ((x: Any?) -> Unit)? = null
        ) : ListAdapter<LabelAndValue, LabelAndValueViewHolder>(DIFF_UTIL) {
    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<LabelAndValue>() {
            override fun areItemsTheSame(
                oldItem: LabelAndValue, newItem: LabelAndValue
            ): Boolean {
                return oldItem.label == newItem.label
            }

            override fun areContentsTheSame(
                oldItem: LabelAndValue, newItem: LabelAndValue
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LabelAndValueViewHolder(
        CoreUiCellLabelAndValueBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: LabelAndValueViewHolder, position: Int) {
        getItem(position)?.let { holder.setLabelAndValues(it, onValueIconCLick) }
    }
}
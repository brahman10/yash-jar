package com.jar.app.core_ui.info_dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.databinding.CoreUiCellInfoItemBinding
import com.jar.app.core_base.domain.model.InfoItem

class InfoItemAdapter : ListAdapter<InfoItem, InfoItemViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<InfoItem>() {
            override fun areItemsTheSame(oldItem: InfoItem, newItem: InfoItem): Boolean {
                return oldItem.icon == newItem.icon
            }

            override fun areContentsTheSame(oldItem: InfoItem, newItem: InfoItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoItemViewHolder {
        val binding = CoreUiCellInfoItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return InfoItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InfoItemViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setInfoItem(it)
        }
    }
}
package com.jar.app.feature_settings.impl.ui.payment_methods.dialog_add_upi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_settings.databinding.CellVpaChipItemBinding

internal class VpaChipsAdapter(
private val onClick: (string: String) -> Unit
) : ListAdapter<String, VpaChipsAdapter.VpaChipViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VpaChipViewHolder(
        CellVpaChipItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClick
    )

    override fun onBindViewHolder(holder: VpaChipViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setVpaChip(it)
        }
    }

    inner class VpaChipViewHolder(
        private val binding: CellVpaChipItemBinding,
        private val onClick: (string: String) -> Unit
    ) : BaseViewHolder(binding.root) {

        private lateinit var vpaName: String

        init {
            binding.tvVpaName.setDebounceClickListener {
                if (::vpaName.isInitialized)
                    onClick.invoke(vpaName)
            }
        }

        fun setVpaChip(vpa: String) {
            vpaName = vpa
            binding.tvVpaName.text = vpa
        }
    }
}
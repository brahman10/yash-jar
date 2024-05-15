package com.jar.app.feature_lending.impl.ui.reason

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.databinding.FeatureLendingCellReasonBinding
import com.jar.app.feature_lending.shared.domain.model.ReasonData

internal class ReasonsAdapter(
    private val onClick: (data: ReasonData) -> Unit
) : ListAdapter<ReasonData, ReasonsAdapter.ReasonsViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<ReasonData>() {
            override fun areItemsTheSame(oldItem: ReasonData, newItem: ReasonData): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ReasonData, newItem: ReasonData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ReasonsViewHolder(
        FeatureLendingCellReasonBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClick
    )

    override fun onBindViewHolder(holder: ReasonsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setReasonChip(it)
        }
    }

    inner class ReasonsViewHolder(
        private val binding: FeatureLendingCellReasonBinding,
        private val onClick: (data: ReasonData) -> Unit
    ) : BaseViewHolder(binding.root) {

        var selectedReason: ReasonData? = null

        init {
            binding.llRoot.setDebounceClickListener {
                selectedReason?.let {
                    onClick.invoke(it)
                }
            }
        }

        fun setReasonChip(reason: ReasonData) {
            selectedReason = reason
            binding.tvReason.text = reason.title
            Glide.with(context).load(reason.icon).into(binding.ivReasonIcon)
            binding.llRoot.isSelected = reason.isSelected
        }
    }
}
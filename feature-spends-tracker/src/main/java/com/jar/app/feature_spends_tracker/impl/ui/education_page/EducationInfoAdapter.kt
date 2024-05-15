package com.jar.app.feature_spends_tracker.impl.ui.education_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.jar.app.base.util.dp
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_spends_tracker.databinding.FeatureEducationCellSpendsTrackerBinding
import com.jar.app.feature_spends_tracker.shared.domain.model.spends_education.SpendsTrackerEducationInfo


internal class EducationInfoAdapter :
    ListAdapter<SpendsTrackerEducationInfo, EducationInfoAdapter.SpendsEducationViewHolder>(
        DIFF_UTIL
    ) {
    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<SpendsTrackerEducationInfo>() {
            override fun areItemsTheSame(
                oldItem: SpendsTrackerEducationInfo,
                newItem: SpendsTrackerEducationInfo
            ): Boolean {
                return oldItem.infoText == newItem.infoText
            }

            override fun areContentsTheSame(
                oldItem: SpendsTrackerEducationInfo,
                newItem: SpendsTrackerEducationInfo
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpendsEducationViewHolder =
        SpendsEducationViewHolder(
            FeatureEducationCellSpendsTrackerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SpendsEducationViewHolder, position: Int) {
        getItem(position)?.let {
            holder.onBind(it)
        }
    }

    inner class SpendsEducationViewHolder(
        private val binding: FeatureEducationCellSpendsTrackerBinding,
    ) :
        BaseViewHolder(binding.root) {
        fun onBind(educationInfo: SpendsTrackerEducationInfo) {

            Glide.with(binding.root.context)
                .load(educationInfo.infoIcon)
                .override(40.dp)
                .into(binding.ivMoneyIcon)
            binding.tvInfoText.text = educationInfo.infoText

        }
    }
}
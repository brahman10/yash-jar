package com.jar.app.core_ui.onboarding_stories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.databinding.CellIndicatorViewBinding
import com.jar.app.core_ui.onboarding_stories.data.OnboardingStoryIndicatorData

class IndicatorViewAdapter :
    ListAdapter<OnboardingStoryIndicatorData, IndicatorViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<OnboardingStoryIndicatorData>() {
            override fun areItemsTheSame(
                oldItem: OnboardingStoryIndicatorData,
                newItem: OnboardingStoryIndicatorData
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: OnboardingStoryIndicatorData,
                newItem: OnboardingStoryIndicatorData
            ): Boolean {
                return oldItem.id == newItem.id && oldItem.isSelected == newItem.isSelected
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = IndicatorViewHolder(
        CellIndicatorViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: IndicatorViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}
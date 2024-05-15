package com.jar.app.feature.onboarding.ui.onboarding_story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.databinding.CellOnboardingStoryVariant1Binding

class OnboardingStoriesV1Adapter(
    private val onResourceReady: (Long?, Int?) -> Unit
) :
    ListAdapter<com.jar.app.feature_onboarding.shared.domain.model.Stories, OnboardingStoryV1VH>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<com.jar.app.feature_onboarding.shared.domain.model.Stories>() {
            override fun areItemsTheSame(oldItem: com.jar.app.feature_onboarding.shared.domain.model.Stories, newItem: com.jar.app.feature_onboarding.shared.domain.model.Stories): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: com.jar.app.feature_onboarding.shared.domain.model.Stories, newItem: com.jar.app.feature_onboarding.shared.domain.model.Stories): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OnboardingStoryV1VH(
        CellOnboardingStoryVariant1Binding.inflate(LayoutInflater.from(parent.context), parent, false),
        onResourceReadyVH = { time,position ->
            onResourceReady.invoke(time,position)
        }
    )

    override fun onBindViewHolder(holder: OnboardingStoryV1VH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }
}
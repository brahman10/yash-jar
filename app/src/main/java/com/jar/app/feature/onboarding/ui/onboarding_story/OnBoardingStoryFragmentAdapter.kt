package com.jar.app.feature.onboarding.ui.onboarding_story

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jar.app.feature_onboarding.shared.domain.model.OnboardingStory

class OnBoardingStoryFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val onboardingStories: List<com.jar.app.feature_onboarding.shared.domain.model.OnboardingStory>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments: List<StoryAnimationFragment>
        get() {
            val list = mutableListOf<StoryAnimationFragment>()
            onboardingStories.forEach {
                list.add(StoryAnimationFragment.newInstance(it))
            }
            return list
        }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
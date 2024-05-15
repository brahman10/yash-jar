package com.jar.app.feature_jar_duo.impl.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jar.app.feature_jar_duo.shared.domain.model.OnboardingData

internal class DuoAdapterOnboarding(
    data: List<com.jar.app.feature_jar_duo.shared.domain.model.OnboardingData>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    private val fragmentList = listOf(
        DuoFragmentOnboardingOne.newInstance(data[0]),
        DuoFragmentOnboardingTwo.newInstance(data[1]),
        DuoFragmentOnboardingTwo.newInstance(data[2])
    )

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}
package com.jar.app.feature.account

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jar.app.feature_profile.api.ProfileApi
import com.jar.app.feature_settings.api.SettingsApi

internal class AccountPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    settingsApi: SettingsApi,
    profileApi: ProfileApi
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    companion object {
        const val POSITION_PROFILE = 0
        const val POSITION_SETTINGS = 1
    }

    private val fragments = listOf(
        profileApi.openProfileFlow(),
        settingsApi.openSettingFragment()
    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
package com.jar.app.feature_profile.api

import androidx.fragment.app.Fragment
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.feature_profile.impl.ui.profile.ProfileFragment
import javax.inject.Inject

internal class ProfileApiImpl @Inject constructor() : ProfileApi, BaseNavigation {

    override fun openProfileFlow(): Fragment {
        return ProfileFragment.newInstance()
    }
}
package com.jar.app.feature_goal_based_saving.impl.ui.defaultLoadingScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.feature_goal_based_saving.databinding.DefaultLoadingScreenBinding
import org.greenrobot.eventbus.EventBus

internal class DefaultLoadingScreen: BaseFragment<DefaultLoadingScreenBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DefaultLoadingScreenBinding
        get() = DefaultLoadingScreenBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        binding.loadingShimmer.startShimmer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
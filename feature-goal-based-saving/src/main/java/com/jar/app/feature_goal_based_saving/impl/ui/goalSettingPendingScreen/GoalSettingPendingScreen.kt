package com.jar.app.feature_goal_based_saving.impl.ui.goalSettingPendingScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.feature_goal_based_saving.databinding.LayoutGoalPendingScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class GoalSettingPendingScreen: BaseFragment<LayoutGoalPendingScreenBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> LayoutGoalPendingScreenBinding
        get() = LayoutGoalPendingScreenBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
    }
}
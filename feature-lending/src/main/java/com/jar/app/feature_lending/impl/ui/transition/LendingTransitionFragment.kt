package com.jar.app.feature_lending.impl.ui.transition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.feature_lending.databinding.FeatureLendingFragmentTransitionBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class LendingTransitionFragment:BaseFragment<FeatureLendingFragmentTransitionBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingFragmentTransitionBinding
        get() = FeatureLendingFragmentTransitionBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUi()
        initClickListeners()
        observeFlow()
        registerBackPressDispatcher()
    }

    private fun setupUi() {

    }

    private fun initClickListeners() {

    }

    private fun observeFlow() {

    }

    private fun registerBackPressDispatcher() {

    }
}
package com.jar.app.feature_settings.impl.ui.battery_optimization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_settings.databinding.FragmentBatteryOptimizationBinding
import com.jar.app.feature_settings.domain.SettingsEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.judemanutd.autostarter.AutoStartPermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class BatteryOptimizationFragment : BaseFragment<FragmentBatteryOptimizationBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBatteryOptimizationBinding
        get() = FragmentBatteryOptimizationBinding::inflate

    var appStartTime: Long = 0L

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.btnBack.setDebounceClickListener {
            popBackStack()
        }

        binding.btnOptimize.setDebounceClickListener {
            analyticsHandler.postEvent(SettingsEventKey.Clicked_OptimizeNow_BatteryOptimizationScreen)
            AutoStartPermissionHelper.getInstance().getAutoStartPermission(requireContext())
        }
    }

    override fun onResume() {
        super.onResume()
        appStartTime = System.currentTimeMillis()
        analyticsHandler.postEvent(SettingsEventKey.Shown_BatteryOptimizationScreen)
    }

    override fun onPause() {
        super.onPause()
        analyticsHandler.postEvent(
            SettingsEventKey.Exit_BatteryOptimization_SettingsTab, mapOf(
                SettingsEventKey.timeSpent to System.currentTimeMillis() - appStartTime
            )
        )
    }
}
package com.jar.app.feature_settings.impl.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.orFalse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.util.observeNetworkResponseUnwrapped
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_settings.databinding.FragmentNotificationSettingsBinding
import com.jar.app.feature_settings.domain.SettingsEventKey
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class NotificationSettingsFragment : BaseFragment<FragmentNotificationSettingsBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    var appStartTime: Long = 0L

    private val viewModel by viewModels<NotificationSettingsViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNotificationSettingsBinding
        get() = FragmentNotificationSettingsBinding::inflate

    private var adapter: NotificationSettingsAdapter? = null

    private lateinit var activity: WeakReference<FragmentActivity>

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        activity = WeakReference(requireActivity())
        getData()
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun getData() {
        viewModel.fetchManageNotificationsList(activity)
    }

    private fun setupUI() {
        binding.rvManageNotification.layoutManager = LinearLayoutManager(context)
        adapter = NotificationSettingsAdapter(
            onClick = { switch, settingIdentifier ->
                viewModel.toggleSwitchAccordingToIdentifier(
                    settingIdentifier,
                    switch.isChecked.not().orFalse()
                )
            }
        )
        binding.rvManageNotification.adapter = adapter
//        val customDividerItemDecorator = CustomDividerItemDecorator(
//            ContextCompat.getDrawable(
//                requireContext(), R.drawable.feature_settings_notification_settings_seperator
//            )
//        )
//        binding.rvManageNotification.addItemDecorationIfNoneAdded(
//            customDividerItemDecorator
//        )
    }

    private fun setupListeners() {
        binding.btnBack.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.settingListLiveData.observeNetworkResponseUnwrapped(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                if (adapter?.itemCount != 0) {
                    showProgressBar()
                }
            },
            onSuccess = {
                dismissProgressBar()
                if (!it.isNullOrEmpty()) {
                    binding.shimmerPlaceholder.stopShimmer()
                    binding.shimmerPlaceholder.isVisible = false
                    binding.rvManageNotification.isVisible = true
                    adapter?.submitList(it)
                    analyticsHandler.postEvent(
                        EventKey.Shown_Pricedropalert,
                        mapOf(EventKey.Screen to "PriceDropSettingsScreen")
                    )
                }
            }
        )

        viewModel.updateUserSettingsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                viewModel.fetchManageNotificationsList(activity)
                navigateTo(
                    NotificationSettingsFragmentDirections.actionNotificationSettingsFragmentToPostNotificationSettingActionDialog(
                        viewModel.isEnabled, getCustomString(viewModel.description)
                    )
                )
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    override fun onResume() {
        super.onResume()
        appStartTime = System.currentTimeMillis()
        analyticsHandler.postEvent(SettingsEventKey.Shown_NotificationSettings_SettingsTab)
    }

    override fun onPause() {
        super.onPause()
        analyticsHandler.postEvent(
            SettingsEventKey.Exit_NotificationSettings_SettingsTab, mapOf(
                SettingsEventKey.timeSpent to System.currentTimeMillis() - appStartTime
            )
        )
    }
}
package com.jar.app.feature.logout.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.databinding.FragmentLogoutConfirmationBinding
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_analytics.EventKey.LogoutConfirmationDialog
import com.jar.app.core_network.event.LogoutEvent
import com.jar.app.core_ui.extension.setDebounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class LogoutConfirmationFragment : BaseBottomSheetDialogFragment<FragmentLogoutConfirmationBinding>() {

    override val bottomSheetConfig = DEFAULT_CONFIG

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLogoutConfirmationBinding
        get() = FragmentLogoutConfirmationBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setup() {
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.btnNo.setDebounceClickListener {
            dismiss()
        }

        binding.btnLogout.setDebounceClickListener {
            analyticsHandler.postEvent(EventKey.CLICKED_YES_LOGOUT)
            dismiss()
            EventBus.getDefault().post(LogoutEvent(flowContext = LogoutConfirmationDialog))
        }
    }
}
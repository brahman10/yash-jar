package com.jar.app.feature.unusual_activity_detected

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.R
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_analytics.EventKey.LogoutUnusualActivity
import com.jar.app.core_network.event.LogoutEvent
import com.jar.app.databinding.FragmentUnusualActivityDetectedBinding
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class UnusualActivityDetectedFragment :
    BaseBottomSheetDialogFragment<FragmentUnusualActivityDetectedBinding>() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    override val bottomSheetConfig = BottomSheetConfig(
        isHideable = false,
        shouldShowFullHeight = true,
        isCancellable = false
    )

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUnusualActivityDetectedBinding
        get() = FragmentUnusualActivityDetectedBinding::inflate

    override fun setup() {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvDescription.text = getString(
            R.string.we_limit_to_a_maximum_of_n_unique_mobile_numbers_on_one_device,
            remoteConfigApi.getMaxDeviceLimit()
        )
    }

    private fun setupListeners() {
        binding.btnContactSupport.setDebounceClickListener {
            val number = remoteConfigApi.getWhatsappNumber()
            requireContext().openWhatsapp(number)
        }

        binding.btnLoginWithPreviousNumber.setDebounceClickListener {
            dismiss()
            EventBus.getDefault().post(LogoutEvent(flowContext = LogoutUnusualActivity))
        }
    }
}
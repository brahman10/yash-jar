package com.jar.app.feature_lending.impl.ui.loan_status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLoanFailureBinding
import com.jar.app.feature_lending.impl.ui.common.LendingViewModel
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class LoanFailureFragment : BaseFragment<FragmentLoanFailureBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val lendingViewModel by activityViewModels<LendingViewModel> { defaultViewModelProviderFactory }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.btnAction.performClick()
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoanFailureBinding
        get() = FragmentLoanFailureBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
        EventBus.getDefault()
            .post(LendingToolbarVisibilityEventV2(shouldHide = true))
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        setupListeners()
        analyticsHandler.postEvent(LendingEventKey.Shown_MandateFailed_Screen)
    }

    private fun setupListeners() {
        binding.btnAction.setDebounceClickListener {
            popBackStack()
        }

        binding.tvSupport.setDebounceClickListener {
            prefs.getUserStringSync()?.let {
                val user = serializer.decodeFromString<User?>(it)
                val message = getCustomStringFormatted(
                    MR.strings.feature_lending_loan_failure_error_wa_message,
                    user?.getFullName().orEmpty(),
                    user?.phoneNumber.orEmpty(),
                    lendingViewModel.getLoanId()
                )
                requireContext().openWhatsapp(remoteConfigApi.getWhatsappNumber(), message)
            }
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}
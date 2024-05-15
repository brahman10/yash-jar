package com.jar.app.feature.active_session_detected.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.RecreateAppEvent
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.databinding.FragmentActiveSessionsDetectedBinding
import com.jar.app.feature_onboarding.shared.domain.event.LogoutFromOtherDevicesEvent
import com.jar.app.feature_onboarding.shared.domain.event.LogoutFromOtherDevicesWhileOnboarding
import com.jar.app.feature_onboarding.shared.domain.event.NavigateBackEvent
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class ActiveSessionDetectedFragment :
    BaseBottomSheetDialogFragment<FragmentActiveSessionsDetectedBinding>() {

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var prefs: PrefsApi

    private val args by navArgs<ActiveSessionDetectedFragmentArgs>()

    private val viewModel by viewModels<ActiveSessionDetectedFragmentViewModel> { defaultViewModelProviderFactory }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentActiveSessionsDetectedBinding
        get() = FragmentActiveSessionsDetectedBinding::inflate

    override val bottomSheetConfig = BottomSheetConfig(
        isHideable = false,
        shouldShowFullHeight = true,
        isCancellable = false,
        isDraggable = false
    )

    override fun setup() {
        init()
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun init() {
        args.userResponse?.let {
            //Means user if logging in for the first time
            viewModel.saveUserData(it)
        }
    }

    private fun setupUI() {
        binding.tvNumber.text = args.userResponse?.user?.phoneNumber
    }

    private fun setupListeners() {
        binding.btnLoginWithNewNumber.setDebounceClickListener {
            dismiss()
            EventBus.getDefault().post(com.jar.app.feature_onboarding.shared.domain.event.NavigateBackEvent())
        }

        binding.btnLogoutFromOtherDevices.setDebounceClickListener {
            dismiss()
            EventBus.getDefault().postSticky(com.jar.app.feature_onboarding.shared.domain.event.LogoutFromOtherDevicesEvent())
        }
    }

    private fun observeLiveData() {
        userLiveData.observe(viewLifecycleOwner) {
            it?.phoneNumber?.let {
                binding.tvNumber.text = it
            }
        }

        viewModel.logoutLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismiss()
                dismissProgressBar()
                EventBus.getDefault().postSticky(com.jar.app.feature_onboarding.shared.domain.event.LogoutFromOtherDevicesWhileOnboarding())
                EventBus.getDefault().post(RecreateAppEvent())
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

}
package com.jar.app.feature_lending_kyc.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentDummyScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

@AndroidEntryPoint
internal class DummyScreenFragment : BaseFragment<FeatureLendingKycFragmentDummyScreenBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentDummyScreenBinding
        get() = FeatureLendingKycFragmentDummyScreenBinding::inflate

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                EventBus.getDefault().post(
                    GoToHomeEvent(
                        "DummyScreen",
                        BaseConstants.HomeBottomNavigationScreen.PROFILE
                    )
                )
            }
        }

    override fun setupAppBar() {
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
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
package com.jar.app.feature_lending_kyc.impl.ui.selfie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentSelfieEdgeCaseBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class SelfieEdgeCaseFragment :
    BaseFragment<FeatureLendingKycFragmentSelfieEdgeCaseBinding>() {
    private val args by navArgs<SelfieEdgeCaseFragmentArgs>()
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentSelfieEdgeCaseBinding
        get() = FeatureLendingKycFragmentSelfieEdgeCaseBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(ToolbarNone)
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUi()
        setClickListener()
    }

    private fun setClickListener() {
        binding.btnRetakeSelfie.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun setupUi() {
        binding.tvTitle.text = args.title
        Glide.with(requireContext())
            .load(args.edgeCaseUrl)
            .into(binding.ivIllustration)
        EventBus.getDefault().post(
            ToolbarStepsVisibilityEvent(
                shouldShowSteps = false,
                Step.SELFIE
            )
        )
    }
}
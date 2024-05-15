package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.verified

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentVerifiedAadhaarBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.impl.domain.model.KYCScreenArgs
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class VerifiedAadhaarFragment :
    BaseFragment<FeatureLendingKycFragmentVerifiedAadhaarBinding>() {
    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<VerifiedAadhaarFragmentArgs>()
    private val lendingKycStepsViewModel: LendingKycStepsViewModel by activityViewModels()
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentVerifiedAadhaarBinding
        get() = FeatureLendingKycFragmentVerifiedAadhaarBinding::inflate

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                lendingKycStepsViewModel.viewOnlyNavigationRedirectTo(
                    LendingKycFlowType.AADHAAR,
                    false,
                    WeakReference(requireActivity())
                )
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setClickListener()
        registerBackPressDispatcher()
    }

    private fun setClickListener() {
        binding.btnNext.setOnClickListener {
            val encoded = encodeUrl(serializer.encodeToString(KYCScreenArgs()))
            navigateTo(FeatureLendingKycStepsNavigationDirections.actionToSelfieCheckFragment(encoded))
        }
    }

    private fun setupUI() {
        EventBus.getDefault().post(
            ToolbarStepsVisibilityEvent(
                shouldShowSteps = true,
                Step.AADHAAR
            )
        )
        binding.icvAadhaar.setIdentityHeading(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_aadhaar_number))
        binding.icvAadhaar.setIdentity(args.aadhaarDetail.maskAadhaarNumber())
        binding.icvAadhaar.setName(args.aadhaarDetail.name.orEmpty())
        binding.icvAadhaar.setDob(args.aadhaarDetail.dob.orEmpty())
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
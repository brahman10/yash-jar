package com.jar.app.feature_lending.impl.ui.view_only.kyc_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.core_base.domain.model.KycProgressResponse
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.getMaskedString
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLendingKycViewOnlyBinding
import com.jar.app.feature_lending.impl.domain.event.UpdateLendingStepsToolbarEvent
import com.jar.app.feature_lending.impl.ui.common.LendingViewModel
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep
import com.jar.app.feature_lending.shared.util.LendingFlowType
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class LendingKycViewOnlyFragment : BaseFragment<FragmentLendingKycViewOnlyBinding>(){

    private val args by navArgs<LendingKycViewOnlyFragmentArgs>()

    private val lendingViewModel by activityViewModels<LendingViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingKycViewOnlyBinding
        get() = FragmentLendingKycViewOnlyBinding::inflate

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                lendingViewModel.toolbarBackNavigation(
                    findNavController().currentBackStackEntry,
                    contextRef = WeakReference(requireActivity()),
                    flowType = args.flowType
                )
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateLendingStepsToolbarEvent(shouldShowSteps = true, LendingStep.CHOOSE_AMOUNT)
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI(lendingViewModel.loanApplications?.kycJourney)
        initClickListeners()
        registerBackPressDispatcher()
    }

    private fun initClickListeners() {
        binding.btnNext.setDebounceClickListener {
            lendingViewModel.viewOnlyNavigationRedirectTo(
                flowType = LendingFlowType.KYC,
                isGoToNextStepFlow = true,
                contextRef = WeakReference(requireActivity()),
                currentDestination = R.id.lendingKycViewOnlyFragment,
                fromFlow = args.flowType
            )
        }
    }

    private fun setupUI(kycJourney: KycProgressResponse?) {
        binding.tvName.text = kycJourney?.kycProgress?.AADHAAR?.name.orEmpty()
        binding.tvDob.text = kycJourney?.kycProgress?.AADHAAR?.dob ?: kycJourney?.kycProgress?.PAN?.dob ?: ""
        binding.tvEmail.text = kycJourney?.kycProgress?.EMAIL?.email.orEmpty()
        binding.tvPan.text = kycJourney?.kycProgress?.PAN?.panNo.orEmpty().getMaskedString(1,8) // I********H
        binding.tvAadhaar.text = kycJourney?.kycProgress?.AADHAAR?.aadhaarNo.orEmpty().getMaskedString(0,7) // ********3281
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}
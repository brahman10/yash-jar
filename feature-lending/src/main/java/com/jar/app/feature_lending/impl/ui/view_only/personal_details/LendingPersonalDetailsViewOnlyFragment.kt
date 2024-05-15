package com.jar.app.feature_lending.impl.ui.view_only.personal_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.core_base.domain.model.User
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLendingPersonalDetailsViewOnlyBinding
import com.jar.app.feature_lending.impl.domain.event.UpdateLendingStepsToolbarEvent
import com.jar.app.feature_lending.shared.domain.model.temp.EmploymentType
import com.jar.app.feature_lending.impl.ui.common.LendingViewModel
import com.jar.app.feature_lending.shared.util.LendingFlowType
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep

@AndroidEntryPoint
internal class LendingPersonalDetailsViewOnlyFragment :
    BaseFragment<FragmentLendingPersonalDetailsViewOnlyBinding>() {

    private val lendingViewModel by activityViewModels<LendingViewModel> { defaultViewModelProviderFactory }

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<LendingPersonalDetailsViewOnlyFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingPersonalDetailsViewOnlyBinding
        get() = FragmentLendingPersonalDetailsViewOnlyBinding::inflate

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
            UpdateLendingStepsToolbarEvent(shouldShowSteps = true, LendingStep.KYC)
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        initClickListeners()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        prefs.getUserStringSync()?.let {
            val user = serializer.decodeFromString<User?>(it)
            binding.tvIntroWithName.text = getString(
                com.jar.app.feature_lending.shared.MR.strings.feature_lending_x_these_are_your_saved_details.resourceId,
                user?.firstName.orEmpty()
            )
            binding.tvAnnualIncome.text = getString(
                com.jar.app.feature_lending.shared.MR.strings.feature_lending_rupee_prefix_int.resourceId,
                args.loanApplicationDetail?.EMPLOYMENT_DETAILS?.getMonthlyIncome().orZero()
            )
            if (args.loanApplicationDetail?.EMPLOYMENT_DETAILS?.employmentType.orEmpty() == EmploymentType.SALARIED.name) {
                binding.groupCompanyName.isVisible = true
                binding.tvEmploymentType.text = getCustomString(MR.strings.feature_lending_salaried)
                binding.tvCompanyName.text =
                    args.loanApplicationDetail?.EMPLOYMENT_DETAILS?.companyName.orEmpty()
            } else {
                binding.tvEmploymentType.text = getCustomString(MR.strings.feature_lending_self_employed)
                binding.groupCompanyName.isVisible = false
            }
            binding.tvAddress.text = args.loanApplicationDetail?.ADDRESS?.address.orEmpty()
        }
    }

    private fun initClickListeners() {
        binding.btnNext.setDebounceClickListener {
            lendingViewModel.viewOnlyNavigationRedirectTo(
                flowType = LendingFlowType.PERSONAL_DETAILS,
                isGoToNextStepFlow = true,
                contextRef = WeakReference(requireActivity()),
                currentDestination = R.id.lendingPersonalDetailsViewOnlyFragment,
                fromFlow = args.flowType
            )
        }
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
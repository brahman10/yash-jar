package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.ckyc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentFetchCkycBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.impl.ui.aadhaar.confirmation.AadhaarConfirmationFragment
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaar
import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaarRequest
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.IllustrationUrls.AADHAAR_PLACEHOLDER_URL
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class CKycFetchFragment : BaseFragment<FeatureLendingKycFragmentFetchCkycBinding>() {

    private val viewModelProvider: CkycFetchViewModelAndroid by viewModels()
    private val viewModel by lazy { viewModelProvider.getInstance() }


    private val lendingKycStepsViewModel: LendingKycStepsViewModel by activityViewModels()

    companion object {
        private const val DELAY_IN_REDIRECTION = 1000L
        private const val CKYC_Record_Not_Found_Screen = "CKYC Record Not Found Screen"
        private const val Aadhaar_Not_Found_In_CKYC_Record_Screen =
            "Aadhaar Not Found In CKYC Record Screen"
        const val FETCH_CKYC_SCREEN = "Fetch CKYC Screen"

    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentFetchCkycBinding
        get() = FeatureLendingKycFragmentFetchCkycBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(ToolbarNone)
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUi()
        observerFlow()
        getData()
    }

    private fun setupUi() {
        binding.ssvSearchingRecord.isVisible = true
        binding.ssvCkycAadhar.isVisible = false
        binding.ssvManualFlow.isVisible = false
        EventBus.getDefault().post(
            ToolbarStepsVisibilityEvent(
                shouldShowSteps = false,
                Step.AADHAAR
            )
        )
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + AADHAAR_PLACEHOLDER_URL)
            .placeholder(com.jar.app.core_ui.R.drawable.ic_placeholder)
            .error(com.jar.app.core_ui.R.drawable.core_ui_ic_error)
            .into(binding.ivFetchIllustration)
    }

    private fun observerFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.ckycRecordSearchFlow.collect {
                    if (it) {
                        binding.ssvSearchingRecord.updateViewState(CKycSearchStateView.State.SUCCESSFUL)
                        binding.ssvCkycAadhar.isVisible = true
                        viewModel.searchAadharInCkyc()
                    } else {
                        binding.ssvSearchingRecord.updateViewState(CKycSearchStateView.State.NOT_FOUND)
                        binding.ssvManualFlow.isVisible = true
                        openManualFlow(CKYC_Record_Not_Found_Screen)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.aadharInCkycFlow.collect(
                    onLoading = {},
                    onSuccess = {
//                        if (it.success) {
                        binding.ssvCkycAadhar.updateViewState(CKycSearchStateView.State.SUCCESSFUL)
//                    openAadhaarConfirmation(it.data)
                        redirectToSuccessStepFragment()
//                        } else {
//                            binding.ssvCkycAadhar.updateViewState(CKycSearchStateView.State.NOT_FOUND)
//                            binding.ssvManualFlow.isVisible = true
//                            openManualFlow(Aadhaar_Not_Found_In_CKYC_Record_Screen)
//                        }
                    },
                    onError = { _, _ ->
                        binding.ssvCkycAadhar.updateViewState(CKycSearchStateView.State.NOT_FOUND)
                        binding.ssvManualFlow.isVisible = true
                        openManualFlow(Aadhaar_Not_Found_In_CKYC_Record_Screen)
                    }
                )
            }
        }
    }

    private fun getData() {
        lendingKycStepsViewModel.kycProgressResponse?.kycProgress?.let {
            viewModel.searchKycRecord(
                KycAadhaarRequest(
                    it.PAN?.panNo.orEmpty(),
                    it.PAN?.dob.orEmpty()
                )
            )
        }

    }

    private fun openAadhaarConfirmation(aadhaar: KycAadhaar) {
        navigateTo(
            CKycFetchFragmentDirections.actionToAadhaarConfirmationFragment(
                aadhaarDetail = aadhaar,
                lenderName = "",
                flowType = AadhaarConfirmationFragment.FLOW_CKYC
            )
        )
    }

    private fun openManualFlow(from: String) {
        lifecycleScope.launch {
            delay(DELAY_IN_REDIRECTION)
            navigateTo(
                CKycFetchFragmentDirections.actionToAadhaarManualEntryConsentPromptFragment(from)
            )
        }
    }

    private fun redirectToSuccessStepFragment() {
        navigateTo(
            FeatureLendingKycStepsNavigationDirections.actionToSuccessStepDialog(
                flowType = LendingKycFlowType.AADHAAR,
                fromScreen = FETCH_CKYC_SCREEN,
                lenderName = null,
                kycFeatureFlowType = KycFeatureFlowType.LENDING.name
            )
        )
    }
}
package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.consent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentAadhaarManualEntryConsentPromptBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class AadhaarManualEntryConsentPromptFragment :
    BaseFragment<FeatureLendingKycFragmentAadhaarManualEntryConsentPromptBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val lendingKycStepsViewModel: LendingKycStepsViewModel by activityViewModels()

    private val args by navArgs<AadhaarManualEntryConsentPromptFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentAadhaarManualEntryConsentPromptBinding
        get() = FeatureLendingKycFragmentAadhaarManualEntryConsentPromptBinding::inflate

    companion object {
        private const val CONSENT_TEXT_CHECKBOX = "Consent text checkbox"
        private const val CONFIRM = "Confirm"
        private const val HELP_ICON = "Help icon"
    }

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
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(ToolbarNone)
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUi()
        setClickListener()
        observeLiveData()
    }

    private fun observeLiveData() {
        lendingKycStepsViewModel.toolbarInteractionLiveData.observe(viewLifecycleOwner) {
            sendEventForClickButton(it)
        }
    }

    private fun setClickListener() {
        binding.cbUserConsent.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.btnConfirm.setDisabled(!isChecked)
            if (isChecked) {
                sendEventForClickButton(CONSENT_TEXT_CHECKBOX)
            }
        }
        binding.tvUserConsent.setDebounceClickListener {
            binding.cbUserConsent.isChecked = !binding.cbUserConsent.isChecked
            binding.btnConfirm.setDisabled(!binding.cbUserConsent.isChecked)
            if (binding.cbUserConsent.isChecked) {
                sendEventForClickButton(CONSENT_TEXT_CHECKBOX)
            }
        }
        binding.btnConfirm.setDebounceClickListener {
            sendEventForClickButton(CONFIRM)
            findNavController().navigate(
                AadhaarManualEntryConsentPromptFragmentDirections.actionToAadhaarManualEntryFragment(),
                getNavOptions(true)
            )
        }
        binding.ivHelp.setDebounceClickListener {
            sendEventForClickButton(HELP_ICON)
            navigateTo(
                FeatureLendingKycStepsNavigationDirections.actionToLendingKycFaqBottomSheet()
            )
        }
    }

    private fun sendEventForClickButton(optionChosen: String) {
        analyticsHandler.postEvent(
            LendingKycEventKey.Clicked_Button_EnterAadhaarManuallyScreen,
            mapOf(LendingKycEventKey.optionChosen to optionChosen)
        )
    }

    private fun setupUi() {
        binding.btnConfirm.setDisabled(true)
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.AADHAAR_PLACEHOLDER_URL)
            .placeholder(com.jar.app.core_ui.R.drawable.ic_placeholder)
            .error(com.jar.app.core_ui.R.drawable.core_ui_ic_error)
            .into(binding.ivIllustration)
        EventBus.getDefault().post(
            ToolbarStepsVisibilityEvent(
                shouldShowSteps = true,
                Step.AADHAAR
            )
        )
        registerBackPressDispatcher()
        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_EnterAadhaarManuallyScreen,
            mapOf(LendingKycEventKey.fromScreen to args.fromScreen)
        )
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
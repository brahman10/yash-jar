package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.action_prompt

import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.data.dto.isFromP2POrLending
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentActionPromptBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaar
import com.jar.app.feature_lending_kyc.shared.ui.aadhaar.action_prompt.ActionPromptViewModel.Companion.DOCUMENT_AADHAAR
import com.jar.app.feature_lending_kyc.shared.ui.aadhaar.action_prompt.ActionPromptViewModel.Companion.DOCUMENT_PAN
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class ActionPromptFragment : BaseFragment<FeatureLendingKycFragmentActionPromptBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val args by navArgs<ActionPromptFragmentArgs>()

    private val viewModelProvider by viewModels<ActionPromptViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy { viewModelProvider.getInstance() }

    private var editActionType: Int = DOCUMENT_AADHAAR

    private val lendingViewModel: LendingKycStepsViewModel by activityViewModels()

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (args.screenArgs.kycFeatureFlowType.isFromP2POrLending()) {
                    popBackStack()
                } else if (args.screenArgs.isPanAadhaarMismatch)
                    lendingViewModel.viewOnlyNavigationRedirectTo(
                        LendingKycFlowType.EMAIL,
                        false,
                        WeakReference(requireActivity())
                    )
                else
                    popBackStack()
            }
        }

    companion object {
        private const val RE_ENTER_AADHAAR_NUMBER = "Re-enter Aadhaar Number"
        private const val CONTACT_SUPPORT = "Contact Support"
        private const val RE_ENTER_AADHAAR_DETAILS = "Re-enter Aadhaar details"
        private const val RE_ENTER_PAN_DETAILS = "Re-enter PAN details"
        private const val BACK_TO_HOME = "Back to home"
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentActionPromptBinding
        get() = FeatureLendingKycFragmentActionPromptBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUi()
        setClickListener()
        observeFlow()
        registerBackPressDispatcher()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.editDetailFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            navigateToEditDetailScreen(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun setClickListener() {
        binding.ivBack.setDebounceClickListener {
            popBackStack()
        }
        binding.btnPrimaryAction.setDebounceClickListener {
            when (args.screenArgs.primaryButtonAction) {
                AadhaarErrorScreenPrimaryButtonAction.GO_HOME -> {
                    sendEventForClickButton(BACK_TO_HOME)
                    EventBus.getDefault().post(
                        GoToHomeEvent(
                            args.screenArgs.titleText,
                            BaseConstants.HomeBottomNavigationScreen.HOME
                        )
                    )
                }
                AadhaarErrorScreenPrimaryButtonAction.GO_BACK -> {
                    sendEventForClickButton(RE_ENTER_AADHAAR_NUMBER)
                    popBackStack()
                }
                AadhaarErrorScreenPrimaryButtonAction.EDIT_AADHAAR -> {
                    editActionType = DOCUMENT_AADHAAR
                    sendEventForClickButton(RE_ENTER_AADHAAR_DETAILS)
                    popBackStack(R.id.aadhaarManualEntryFragment, false)
                }
            }
        }

        binding.btnSecondaryAction.setDebounceClickListener {
            when (args.screenArgs.secondaryButtonAction) {
                AadhaarErrorScreenSecondaryButtonAction.CONTACT_SUPPORT -> {
                    sendEventForClickButton(CONTACT_SUPPORT)
                    val sendTo = remoteConfigApi.getWhatsappNumber()
                    val number = prefs.getUserPhoneNumber()
                    val name = prefs.getUserName()
                    val message = getCustomStringFormatted(
                        com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support_s_s_s,
                        args.screenArgs.contactMessage.orEmpty(),
                        name.orEmpty(),
                        number.orEmpty()
                    )
                    requireContext().openWhatsapp(sendTo, message)
                }
                AadhaarErrorScreenSecondaryButtonAction.GO_BACK -> {
                    popBackStack()
                }
                AadhaarErrorScreenSecondaryButtonAction.EDIT_PAN -> {
                    editActionType = DOCUMENT_PAN
                    lendingViewModel.kycProgressResponse?.let {
                        viewModel.extractEditDetail(it, editActionType)
                    }
                    sendEventForClickButton(RE_ENTER_PAN_DETAILS)
                }
                AadhaarErrorScreenSecondaryButtonAction.NONE -> {

                }
            }
        }
    }

    private fun sendEventForClickButton(optionChosen: String) {
        val eventName = when (args.screenArgs.titleText) {
            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_no_mobile_linked_to_aadhaar) -> {
                LendingKycEventKey.Clicked_Button_NoMobileLinkedToAadhaarScreen
            }
            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation) -> {
                LendingKycEventKey.Clicked_Button_AadhaarPANMismatchScreen
            }
            else -> ""
        }
        analyticsHandler.postEvent(
            eventName,
            mapOf(LendingKycEventKey.optionChosen to optionChosen)
        )
    }

    private fun navigateToEditDetailScreen(detail: KycAadhaar) {
        navigateTo(
            ActionPromptFragmentDirections.actionToEditPanAadhaarDetailsFragment(
                detail, editActionType
            )
        )
    }

    private fun setupUi() {
        EventBus.getDefault()
            .post(ToolbarStepsVisibilityEvent(shouldShowSteps = false, Step.AADHAAR))
        binding.btnPrimaryAction.setText(SpannableString(args.screenArgs.primaryActionText))
        binding.btnSecondaryAction.text = args.screenArgs.secondaryActionText
        binding.btnSecondaryAction.isGone =
            args.screenArgs.secondaryButtonAction == AadhaarErrorScreenSecondaryButtonAction.NONE
        binding.tvTitle.text = args.screenArgs.titleText
        binding.tvSubTitle.text = args.screenArgs.subtitleText
        if (args.screenArgs.assetsUrl.isNotEmpty()) {
            if (args.screenArgs.isIllustrationUrl) {
                binding.ivIllustration.isVisible = true
                binding.lottie.isVisible = false
                Glide.with(requireContext())
                    .load(args.screenArgs.assetsUrl)
                    .placeholder(com.jar.app.core_ui.R.drawable.ic_placeholder)
                    .into(binding.ivIllustration)
            } else {
                binding.lottie.isVisible = true
                binding.ivIllustration.isVisible = false
                binding.lottie.playLottieWithUrlAndExceptionHandling(
                    requireContext(),
                    args.screenArgs.assetsUrl
                )
            }
        }
        binding.btnSecondaryAction.paintFlags =
            binding.btnSecondaryAction.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.ivBack.isGone = args.screenArgs.kycFeatureFlowType.isFromP2POrLending()


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
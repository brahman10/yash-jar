package com.jar.app.feature_lending.impl.ui.kyc.confirm_kyc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarTitleEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.getMaskedString
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingFragmentConfirmKycBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.v2.ApplicationDetails
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class ConfirmLendingKycFragment : BaseFragment<FeatureLendingFragmentConfirmKycBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private val parentViewModelProvider: LendingHostViewModelAndroid by activityViewModels { defaultViewModelProviderFactory }
    private val parentViewModel by lazy {
        parentViewModelProvider.getInstance()
    }

    private val viewModelProvider: ConfirmKycViewModelAndroid by viewModels { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private val arguments by navArgs<ConfirmLendingKycFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    private val isReadOnly = false

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingFragmentConfirmKycBinding
        get() = FeatureLendingFragmentConfirmKycBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchKycInfo(args.loanId.orEmpty())
        parentViewModel.fetchStaticContent(
            contentType = LendingConstants.StaticContentType.CONFIRM_KYC,
            loanId = args.loanId.orEmpty()
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeFlow()
        initClickListener()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        if (isReadOnly) {
            binding.tvHeaderTitle.text =
                getCustomString(MR.strings.feature_lending_your_kyc_details_are_verified)
            binding.tvDescription.isVisible = false
        }
        showConsentOrHide(null)
        analyticsHandler.postEvent(
            LendingEventKeyV2.Lending_eKYCRecordsScreenShown,
            mapOf(LendingEventKeyV2.lender to args.lender.orEmpty())
        )
        EventBus.getDefault()
            .post(LendingToolbarTitleEventV2(getCustomString(MR.strings.feature_lending_complete_kyc)))

    }

    private fun initClickListener() {
        binding.btnConfirm.setDebounceClickListener {
            if (isReadOnly) {
                goToNextScreen()
            } else {
                analyticsHandler.postEvent(LendingEventKeyV2.Lending_eKYCScreenConfirmClicked)
                viewModel.updateKycConsent(
                    UpdateLoanDetailsBodyV2(
                        applicationId = args.loanId.orEmpty(),
                        kycVerificationConsent = true
                    )
                )
            }
        }
        binding.checkboxConsent.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                analyticsHandler.postEvent(
                    LendingKycEventKey.Lending_Checkbox_Clicked,
                    mapOf(
                        LendingKycEventKey.screen_name to LendingKycEventKey.LendingConfirmKycScreen,
                        LendingKycEventKey.check_box to LendingKycEventKey.KYC_Consent
                    )
                )
            }
            binding.btnConfirm.setDisabled(!isChecked)
        }
        binding.tvIHereByConsent.setDebounceClickListener {
            binding.checkboxConsent.isChecked = !binding.checkboxConsent.isChecked
            binding.btnConfirm.setDisabled(!binding.checkboxConsent.isChecked)
        }
    }

    private fun goToNextScreen() {
        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.nextScreen,
                    source = args.screenName,
                    popupToId = R.id.confirmLendingKycFragment
                )
            )
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.kycInfoFlow.collect(
                    onLoading = {
                        uiScope.launch {
                            showProgressBar()
                        }
                    },
                    onSuccess = {
                        uiScope.launch {
                            dismissProgressBar()
                            it?.applicationDetails?.let {
                                setInfoOnUi(it)
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        uiScope.launch {
                            dismissProgressBar()
                        }
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.kycConsentUpdateFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        analyticsHandler.postEvent(LendingEventKeyV2.Lending_eKYCVerificationSuccessful)
                        goToNextScreen()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                parentViewModel.staticContentFlow.collect(
                    onLoading = {},
                    onSuccess = {
                        showConsentOrHide(it?.kycConfirmationConsent)
                    },
                    onError = { _, _ -> }
                )
            }
        }
    }

    private fun showConsentOrHide(consent: String?) {
        consent?.let {
            binding.checkboxConsent.isVisible = true
            binding.tvIHereByConsent.isVisible = true
            binding.tvIHereByConsent.text = it
            binding.checkboxConsent.isChecked = true
            binding.btnConfirm.setDisabled(!binding.checkboxConsent.isChecked)
        } ?: run {
            binding.checkboxConsent.isVisible = false
            binding.tvIHereByConsent.isVisible = false
            binding.btnConfirm.setDisabled(false)
        }
    }

    private fun setInfoOnUi(applicationDetails: ApplicationDetails) {
        val maskedAadhaarNumber =
            applicationDetails.aadhaar?.aadhaarNo?.getMaskedString(0, 7, "X")
                ?.chunked(4)
                ?.joinToString(separator = " ")
        binding.tvNameValue.text = applicationDetails.aadhaar?.name.orEmpty()
        binding.tvDobValue.text = applicationDetails.aadhaar?.dob.orEmpty()
        binding.tvAadhaarValue.text = maskedAadhaarNumber.orEmpty()
        binding.tvAddressValue.text = applicationDetails.aadhaar?.address.orEmpty()
        applicationDetails.selfie?.selfie?.let {
            Glide.with(requireContext()).load(it).into(binding.ivUserImage)
        }
        val btnText =
            if (isReadOnly) getCustomString(MR.strings.feature_lending_continue) else getString(
                com.jar.app.core_ui.R.string.core_ui_confirm
            )
        binding.btnConfirm.setText(btnText)
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

    private fun handleBackNavigation() {
        EventBus.getDefault()
            .post(LendingBackPressEvent(LendingEventKeyV2.EKYC_RECORDS_SHOWN_SCREEN))

        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.backScreen,
                    source = args.screenName,
                    popupToId = R.id.confirmLendingKycFragment,
                    isBackFlow = true
                )
            )
        }
    }
}
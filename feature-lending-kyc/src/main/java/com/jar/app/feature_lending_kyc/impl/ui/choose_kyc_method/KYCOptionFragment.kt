package com.jar.app.feature_lending_kyc.impl.ui.choose_kyc_method

import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarTitleEventV2
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.dp
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.domain.model.KycEmailAndAadhaarProgressStatus
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycOptionBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.impl.domain.model.KYCScreenArgs
import com.jar.app.feature_lending_kyc.shared.MR
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.DigiLockerScreenContent
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class KYCOptionFragment : BaseFragment<FeatureLendingKycOptionBinding>() {

    private val viewModelProvider: ChooseKYCOptionViewModelAndroid by viewModels()
    private val viewModel by lazy { viewModelProvider.getInstance() }

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val arguments by navArgs<KYCOptionFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<KYCScreenArgs>(decodeUrl(arguments.screenArgs))
    }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycOptionBinding
        get() = FeatureLendingKycOptionBinding::inflate

    override fun setupAppBar() {

    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                EventBus.getDefault()
                    .post(
                        LendingBackPressEvent(
                            LendingKycEventKey.AADHAR_MANUAL_ENTRY_SCREEN,
                            shouldNavigateBack = true
                        )
                    )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setup(savedInstanceState: Bundle?) {
        args.applicationId?.let {
            viewModel.fetchContent(it,args.kycFeatureFlowType)
        }

        setupUI()
        setUpObservers()
        setupClickListners()
        registerBackPressDispatcher()

    }

    private fun setUpObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.digiLockerRedirectionUrl.collect(
                    onLoading = {
                        showProgressBar()

                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let { data ->
                            val encodedData = encodeUrl(serializer.encodeToString(
                                KYCScreenArgs(
                                fromScreen= args.fromScreen,
                                lenderName = args.lenderName,
                                kycFeatureFlowType = args.kycFeatureFlowType,
                                applicationId= args.applicationId,
                                url = data.redirectionUrl,
                                webhookUrl = data.webhookUrl
                            )
                            ))
                            findNavController().navigate(
                                Uri.parse("android-app://com.jar.app/DigiLockerWebViewFragment/$encodedData"),
                                getNavOptions(shouldAnimate = true)
                            )
                        }
                    },
                    onError = { message, _ ->
                        dismissProgressBar()
                        message.snackBar(binding.root)
                    }

                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.digiLockerVerificationStatus.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (it?.status == KycEmailAndAadhaarProgressStatus.VERIFIED.name) {
                            uiScope.launch(Dispatchers.Main) {
                                navigateTo(
                                    FeatureLendingKycStepsNavigationDirections.actionToSuccessStepDialog(
                                        flowType = LendingKycFlowType.AADHAAR,
                                        fromScreen = LendingKycEventKey.DigiLocker_Webview,
                                        lenderName = args.lenderName,
                                        kycFeatureFlowType = args.kycFeatureFlowType.name
                                    ),
                                    popUpTo = R.id.KYCOptionFragment,
                                    inclusive = true
                                )
                            }
                        } else {
                            openDigiLockerWebView()
                        }

                    },
                    onError = { message, _ ->
                        dismissProgressBar()
                        message.snackBar(binding.root)
                    }

                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                viewModel.isManualKycEnabled.collect {
                    if (it) {
                        binding.aadharLayout.alpha = 1f
                        binding.aadharQuickerApproval.alpha = 1f
                        binding.aadharErrorMessage.alpha = 1f
                        binding.aadharLayout.isEnabled = true

                    } else {
                        binding.aadharLayout.alpha = 0.5f
                        binding.aadharErrorMessage.alpha = 0.5f
                        binding.aadharLayout.isEnabled = false
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                viewModel.isDigiLockerEnabled.collect {
                    if (it) {
                        binding.digiLockerLayout.alpha = 1f
                        binding.digiLockerErrorMessage.alpha = 1f
                        binding.digiLockerLayout.isEnabled = true
                        binding.digiLockerLayout.isVisible = true
                    } else {
                        binding.digiLockerLayout.alpha = 0.5f
                        binding.digiLockerErrorMessage.alpha = 0.5f
                        binding.digiLockerLayout.isEnabled = false

                    }


                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isConsentAvailable.collect {
                    if (it) {
                        binding.tvScreenTitle.alpha = 1f
                        binding.tvOR.alpha = 1f
                    } else {
                        binding.tvScreenTitle.alpha = 0.5f
                        binding.tvOR.alpha = 0.5f
                    }

                    if (it && viewModel.isDigiLockerEnabled.value) {
                        binding.digiLockerLayout.alpha = 1f
                        binding.digiLockerLayout.isEnabled = true
                        binding.digiLockerQuickerApproval.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.white
                            )
                        )

                        val roundedDrawable = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = 2f.dp
                            setColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    com.jar.app.core_ui.R.color.color_7745FF
                                )
                            )
                        }
                        binding.digiLockerQuickerApproval.background = roundedDrawable
                        binding.digiLockerErrorMessage.alpha = 1f

                    } else {
                        binding.digiLockerLayout.alpha = 0.3f
                        binding.digiLockerLayout.isEnabled = false
                        binding.digiLockerQuickerApproval.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.color_ACA1D3
                            )
                        )

                        val roundedDrawable = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = 2f.dp
                            setColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    com.jar.app.core_ui.R.color.color_321D6C
                                )
                            )
                        }
                        binding.digiLockerQuickerApproval.background = roundedDrawable
                        binding.digiLockerErrorMessage.alpha = 0.3f

                    }
                    if (it && viewModel.isManualKycEnabled.value) {
                        binding.aadharLayout.alpha = 1f
                        binding.aadharQuickerApproval.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.white
                            )
                        )
                        val roundedDrawable = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = 2f.dp
                            setColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    com.jar.app.core_ui.R.color.color_7745FF
                                )
                            )
                        }
                        binding.aadharQuickerApproval.background = roundedDrawable
                        binding.aadharErrorMessage.alpha = 1f
                        binding.aadharLayout.isEnabled = true

                    } else {
                        binding.aadharLayout.alpha = 0.3f
                        binding.aadharQuickerApproval.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.color_ACA1D3
                            )
                        )

                        val roundedDrawable = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = 2f.dp
                            setColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    com.jar.app.core_ui.R.color.color_321D6C
                                )
                            )
                        }
                        binding.aadharQuickerApproval.background = roundedDrawable
                        binding.aadharErrorMessage.alpha = 0.3f
                        binding.aadharLayout.isEnabled = false

                    }
                }
            }
        }


    }

    private fun setupClickListners() {
        binding.checkboxConsent.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.updateConsentStatus(isChecked)
            analyticsHandler.postEvent(
                LendingKycEventKey.Lending_KYCScreenLaunched,
                mapOf(
                    LendingKycEventKey.action to if (isChecked)LendingKycEventKey.Consent_Selected else LendingKycEventKey.Consent_Deselected,
                    LendingKycEventKey.lenderName to args.lenderName.orEmpty(),
                    LendingKycEventKey.fromScreen to args.fromScreen
                )
            )
        }
        binding.digiLockerLayout.setDebounceClickListener {
            viewModel.fetchDigiLockerVerificationStatus(
                args.kycFeatureFlowType,
                remoteConfigApi.shouldEnablePinlessDigilocker()
            )
        }
        binding.aadharLayout.setDebounceClickListener {

           openManualKycFlow(args.kycFeatureFlowType)
        }
        binding.consentCl.setDebounceClickListener {
            if (!viewModel.isConsentAvailable.value) {
                binding.checkboxConsent.isChecked = true
                viewModel.updateConsentStatus(true)
            }
        }
    }

    private fun openManualKycFlow(kycFeatureFlowType: KycFeatureFlowType) {
        if (args.kycFeatureFlowType == KycFeatureFlowType.LENDING) {
            analyticsHandler.postEvent(
                LendingKycEventKey.Lending_KYCScreenLaunched,
                mapOf(
                    LendingKycEventKey.action to LendingKycEventKey.Okyc_Selected,
                    LendingKycEventKey.lenderName to args.lenderName.orEmpty(),
                    LendingKycEventKey.fromScreen to args.fromScreen
                )
            )
        }

        findNavController().navigate(
            Uri.parse("android-app://com.jar.app/manual_aadhar_entry/${kycFeatureFlowType.name}/${args.lenderName}"),
            getNavOptions(shouldAnimate = true)
        )


    }

    private fun openDigiLockerWebView() {
        analyticsHandler.postEvent(
            LendingKycEventKey.Lending_KYCScreenLaunched,
            mapOf(
                LendingKycEventKey.action to LendingKycEventKey.DigiLocker_Selected,
                LendingKycEventKey.lenderName to args.lenderName.orEmpty(),
                LendingKycEventKey.fromScreen to args.fromScreen
            )
        )
        viewModel.fetchRedirectionUrl(args.kycFeatureFlowType,remoteConfigApi.shouldEnablePinlessDigilocker())
    }

    private fun setupUI() {
        showProgressBar()
        EventBus.getDefault()
            .post(LendingToolbarTitleEventV2(getCustomString(MR.strings.feature_lending_kyc_complete_kyc)))

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.screenData.collect(
                    onLoading = {
                    },
                    onSuccess = {
                        setupUIWithData(it)
                    },
                    onError = { message, _ ->
                        dismissProgressBar()
                        message.snackBar(binding.root)
                    }


                )
            }
        }


    }

    private fun setupUIWithData(screenContent: DigiLockerScreenContent?) {
        val isDigiLockerAvailable =
            screenContent?.digiLockerScreenContent?.isDigiLockerAvailable.orFalse()
        val isOkycAvailable = screenContent?.digiLockerScreenContent?.isManualKycAvailable.orFalse()
        Glide.with(requireContext())
            .load(screenContent?.digiLockerScreenContent?.digiLockerImageURL)
            .into(binding.digiLockerImage)

        Glide.with(requireContext())
            .load(screenContent?.digiLockerScreenContent?.manualKycImageURL)
            .into(binding.aadharImage)

        binding.tvScreenTitle.text = screenContent?.digiLockerScreenContent?.heading.orEmpty()
        binding.aadharTitle.text = screenContent?.digiLockerScreenContent?.manualKycTitle.orEmpty()
        binding.aadharSubTitle.text =
            screenContent?.digiLockerScreenContent?.manualKycDesc.orEmpty()
        binding.digiLockerTitle.text =
            screenContent?.digiLockerScreenContent?.digiLockerTitle.orEmpty()
        binding.digiLockerSubTitle.text =
            screenContent?.digiLockerScreenContent?.digiLockerDesc.orEmpty()
        binding.tvUserConsent.text = screenContent?.digiLockerScreenContent?.consentString.orEmpty()


        if (isDigiLockerAvailable.not()) {
            binding.digiLockerErrorMessage.text =
                screenContent?.digiLockerScreenContent?.digiLockerDownMessage
            binding.digiLockerErrorMessage.isVisible = true
            viewModel.updateDigiLockerStatus(false)
            binding.digiLockerQuickerApproval.isVisible = false
        } else {
            binding.digiLockerQuickerApproval.isVisible = true
            viewModel.updateDigiLockerStatus(true)
        }
        if (isOkycAvailable.not()) {
            binding.aadharErrorMessage.text =
                screenContent?.digiLockerScreenContent?.manualKycDownMessage
            binding.aadharErrorMessage.isVisible = true
            binding.aadharQuickerApproval.isVisible = false
            viewModel.updateManualKycStatus(false)
        } else {
            binding.aadharQuickerApproval.isVisible = true
            viewModel.updateManualKycStatus(true)


        }
        binding.aadharQuickerApproval.isVisible = isOkycAvailable && (
                isDigiLockerAvailable.not() &&
                        screenContent?.digiLockerScreenContent?.isDigilockerPreferred.orFalse().not())
        binding.digiLockerQuickerApproval.isVisible = isDigiLockerAvailable &&
                screenContent?.digiLockerScreenContent?.isDigilockerPreferred.orFalse()


        val digiLockerStatus = if (isDigiLockerAvailable) LendingKycEventKey.Enabled
        else LendingKycEventKey.Server_Down

        val okcStatus = if (isOkycAvailable) LendingKycEventKey.Enabled
        else LendingKycEventKey.Server_Down

        analyticsHandler.postEvent(
            LendingKycEventKey.Lending_KYCScreenLaunched,
            mapOf(
                LendingKycEventKey.action to LendingKycEventKey.Shown_KYC_Screen,
                LendingKycEventKey.lenderName to args.lenderName.orEmpty(),
                LendingKycEventKey.Both_Down to screenContent?.digiLockerScreenContent?.areBothFlowDown.orFalse(),
                LendingKycEventKey.DigiLocker_Status to digiLockerStatus,
                LendingKycEventKey.fromScreen to args.fromScreen,
                LendingKycEventKey.Okyc_Status to okcStatus,
                LendingKycEventKey.IsDigilockerPreferred to screenContent?.digiLockerScreenContent?.isDigilockerPreferred.orFalse()
            )
        )

        binding.clLayout.isVisible = true
        dismissProgressBar()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
        EventBus.getDefault()
            .post(LendingToolbarVisibilityEventV2(shouldHide = false))
        EventBus.getDefault().post(
            ToolbarStepsVisibilityEvent(shouldShowSteps = true, Step.AADHAAR, true)
        )


    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()

    }

}
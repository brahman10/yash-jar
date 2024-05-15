package com.jar.app.feature_lending_kyc.impl.ui.pan.loading_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.data.dto.getKycFeatureFlowType
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycDialogPanFromJarLoadingBinding
import com.jar.app.feature_lending_kyc.shared.domain.arguments.CreditReportScreenArguments
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class PanFromJarLoadingDialog :
    BaseDialogFragment<FeatureLendingKycDialogPanFromJarLoadingBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycDialogPanFromJarLoadingBinding
        get() = FeatureLendingKycDialogPanFromJarLoadingBinding::inflate
    override val dialogConfig: DialogFragmentConfig
        get() = DEFAULT_CONFIG

    @Inject
    lateinit var serializer: Serializer

    private val args: PanFromJarLoadingDialogArgs by navArgs()
    companion object{
        const val FETCHING_FROM_JAR_RECORD_SCREEN = "Fetching From Jar Records Screen"
    }
    private val viewModelProvider: PanFromJarLoadingViewModelAndroid by viewModels()
    private val viewModel by lazy { viewModelProvider.getInstance() }


    override fun setup() {
        setupUI()
        observeFlow()
    }

    private fun setupUI() {
        binding.lottie.playLottieWithUrlAndExceptionHandling(requireContext(),BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.VERIFYING)
        viewModel.fetchJarVerifiedPan(getKycFeatureFlowType(args.kycFeatureFlowType))
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchJarVerifiedPanFlow.collect(
                    onLoading = {},
                    onSuccess = {
                        uiScope.launch {
                            delay(2000)
                            val args = encodeUrl(
                                serializer.encodeToString(
                                    CreditReportScreenArguments(
                                        it,
                                        true,
                                        LendingKycConstants.PanFlowType.JAR_VERIFIED,
                                        isBackNavOrViewOnlyFlow = false,
                                        primaryAction = PanErrorScreenPrimaryButtonAction.YES_USE_THIS_PAN,
                                        secondaryAction = PanErrorScreenSecondaryButtonAction.NO_ENTER_DETAILS_MANUALLY,
                                        fromScreen = FETCHING_FROM_JAR_RECORD_SCREEN,
                                        description = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_confirm_if_you_want_to_proceed_with_jar_pan),
                                        kycFeatureFlowType = getKycFeatureFlowType(args.kycFeatureFlowType)
                                    )
                                )
                            )
                            navigateTo(
                                FeatureLendingKycStepsNavigationDirections.actionToCreditReportFetchedStep(
                                    args
                                ),
                                true,
                                popUpTo = R.id.panFromJarLoadingDialog,
                                inclusive = true
                            )
                        }
                    },
                    onError = { errorMessage, _ ->

                    }
                )
            }
        }
    }
}
package com.jar.app.feature_lending.impl.ui.mandate.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.LendingToolbarTitleEventV2
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.playLottieUrlSequentially
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentMadateStatusBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.domain.model.experiment.ReadyCashScreen
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.temp.MandateStatus
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.ui.mandate.status.MandateViewModel
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class MandateStatusFragment : BaseFragment<FragmentMadateStatusBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var delayInApiCall = 5000L //default
    private val arguments by navArgs<MandateStatusFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    private val viewModelProvider by viewModels<MandateViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val parentViewModelProvider by activityViewModels<LendingHostViewModelAndroid> { defaultViewModelProviderFactory }
    private val parentViewModel by lazy {
        parentViewModelProvider.getInstance()
    }


    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMadateStatusBinding
        get() = FragmentMadateStatusBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
        EventBus.getDefault()
            .post(LendingToolbarVisibilityEventV2(shouldHide = true))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args.loanId?.let {
            viewModel.fetchStaticContent(it)
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        observeFlow()
    }

    private fun updateUI() {
        var lottieUrl = ""
        when (viewModel.currentState) {
            MandateViewModel.MANDATE_STATE_REDIRECTION -> {
                lottieUrl = LendingConstants.LottieUrls.GENERIC_LOADING
                parentViewModel.preApprovedData?.let {
                    binding.tvGeneral.text = getCustomStringFormatted(
                        MR.strings.feature_lending_you_being_redirected_to_mandate_setup,
                        it.lenderName.orEmpty()
                    )
                }
                getData()
                sendTransitionEvent(LendingEventKeyV2.redirecting_screen_shown,0L)
            }
            MandateViewModel.MANDATE_STATE_VERIFYING -> {
                lottieUrl = LendingConstants.LottieUrls.GENERIC_LOADING
                binding.tvGeneral.text = getCustomString(MR.strings.feature_lending_verify_mandate)
                uiScope.launch {
                    delay(delayInApiCall)
                    getData()
                }
                sendTransitionEvent(LendingEventKeyV2.verifying_screen_shown, delayInApiCall)
            }
        }
        binding.lottieView.playLottieUrlSequentially(lottieUrl)
        EventBus.getDefault().post(LendingToolbarTitleEventV2(getCustomString(MR.strings.feature_lending_complete_agreement)))

    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.staticContentFlow.collect(
                    onSuccess = {
                        it?.mandateSetupUpdatedContent?.pollingTimeInMillis?.let {
                            delayInApiCall = it
                        }
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.mandateFlow.collect(
                    onSuccess = {
                        redirectUserToMandatePageWebView(it.mandateLink.orEmpty())
                    },
                    onError = { message, errorCode->
                        backPressCallback.isEnabled = false
                        popBackStack()
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loanApplicationsFlow.collect(
                    onSuccess = {
                        it?.let {
                            when (it.applicationDetails?.mandateDetails?.status) {
                                MandateStatus.PENDING.name -> {
                                    redirectUserToMandatePageWebView(
                                        it.applicationDetails?.mandateDetails?.mandateLink.orEmpty()
                                    )
                                }

                                MandateStatus.VERIFIED.name -> {
                                    EventBus.getDefault()
                                        .post(LendingToolbarVisibilityEventV2(shouldHide = !args.screenData?.shouldShowProgress.orFalse()))
                                    analyticsHandler.postEvent(
                                        LendingEventKeyV2.Lending_EMISetupSuccessful,
                                        mapOf(
                                            LendingEventKeyV2.lenderName to args.lender.orEmpty(),
                                            LendingEventKeyV2.selected_automation_mode to it.applicationDetails?.mandateDetails?.mandateAuthType.orEmpty()

                                        ))
                                    args.screenData?.let {
                                        EventBus.getDefault().postSticky(
                                            ReadyCashNavigationEvent(
                                                whichScreen = it.nextScreen,
                                                source = ReadyCashScreen.MANDATE_SETUP,
                                                popupToId = R.id.mandateStatusFragment
                                            )
                                        )
                                    }
                                }

                                MandateStatus.FAILED.name, null -> {
                                    val argsData = encodeUrl(serializer.encodeToString(args))
                                    navigateTo(
                                        LendingStepsNavigationDirections.actionToLoanMandateFailureFragment(
                                            screenArgs = argsData
                                        ),
                                        popUpTo = R.id.mandateStatusFragment,
                                        inclusive = true
                                    )
                                }

                                MandateStatus.IN_PROGRESS.name -> {
                                    val argsData = encodeUrl(serializer.encodeToString(args))
                                    navigateTo(
                                        LendingStepsNavigationDirections.actionToLoanPendingFragment(
                                            screenArgs = argsData
                                        ),
                                        popUpTo = R.id.mandateStatusFragment,
                                        inclusive = true
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    private fun sendTransitionEvent(action:String, delay:Long){
        analyticsHandler.postEvent(
            LendingEventKeyV2.Lending_MandateTransitionScreens,
            mapOf(
                LendingEventKeyV2.lenderName to args.lender.orEmpty(),
                LendingEventKeyV2.action to action,
                LendingEventKeyV2.delay_in_api_call to delay
            )
        )
    }
    private fun redirectUserToMandatePageWebView(mandateUrl: String) {
        viewModel.currentState = MandateViewModel.MANDATE_STATE_VERIFYING
        navigateTo(
            LendingStepsNavigationDirections.actionGlobalLendingWebViewFragment(
                url = mandateUrl,
                isMandateFlow = true,
                fromStepName = LendingStep.BANK_DETAILS.name
            )
        )
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun getData() {
        viewModel.fetchLendingProgress(args.loanId.orEmpty())
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
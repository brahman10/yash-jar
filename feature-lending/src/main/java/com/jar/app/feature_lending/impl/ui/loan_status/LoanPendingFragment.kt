package com.jar.app.feature_lending.impl.ui.loan_status

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
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.doRepeatingTask
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLoanPendingBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.domain.model.experiment.ReadyCashScreen
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.impl.ui.mandate.status.MandateStatusFragmentArgs
import com.jar.app.feature_lending.impl.ui.mandate.status.MandateViewModelAndroid
import com.jar.app.feature_lending.shared.domain.LendingEventKey
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.temp.MandateStatus
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class LoanPendingFragment : BaseFragment<FragmentLoanPendingBinding>() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val arguments by navArgs<LoanPendingFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    private var pollingJob: Job? = null

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

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoanPendingBinding
        get() = FragmentLoanPendingBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
        EventBus.getDefault()
            .post(LendingToolbarVisibilityEventV2(shouldHide = true))
    }

    override fun setup(savedInstanceState: Bundle?) {
        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            LendingConstants.LottieUrls.GENERIC_LOADING
        )
        registerBackPressDispatcher()
        setupListener()
        analyticsHandler.postEvent(
            LendingEventKey.Lending_MandateApplicationPendingShown,
            mapOf(
                LendingEventKeyV2.lenderName to args.lender.orEmpty()
            ))
        observeFlow()
        startPollingTimer()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loanApplicationsFlow.collect(
                    onSuccess = {
                        it?.let {
                            when (it.applicationDetails?.mandateDetails?.status) {
                                MandateStatus.VERIFIED.name -> {
                                    args.screenData?.let {
                                        EventBus.getDefault().postSticky(
                                            ReadyCashNavigationEvent(
                                                whichScreen = it.nextScreen,
                                                source = ReadyCashScreen.MANDATE_SETUP,
                                                popupToId = R.id.loanPendingFragment
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
                                        popUpTo = R.id.loanPendingFragment,
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

    private fun startPollingTimer() {
        pollingJob?.cancel()
        pollingJob = uiScope.doRepeatingTask(5_000) {
            viewModel.fetchLendingProgress(parentViewModel.getLoanId())
        }
    }

    private fun setupListener() {
        binding.btnAction.setDebounceClickListener {
            EventBus.getDefault().post(GoToHomeEvent("MANDATE_PENDING"))
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        pollingJob?.cancel()
        super.onDestroyView()
    }
}
package com.jar.app.feature_lending.impl.ui.withdrawal_wait

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.playLottieUrlSequentially
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingFragmentWithdrawalWaitBinding
import com.jar.app.feature_lending.impl.domain.model.TransitionStateScreenArgs
import com.jar.app.feature_lending.impl.domain.model.experiment.ReadyCashScreen
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationStatusV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.app.feature_lending.shared.util.LendingUtil
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class LendingWithdrawalWaitFragment :
    BaseFragment<FeatureLendingFragmentWithdrawalWaitBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private val flowType = BaseConstants.FROM_LENDING

    private val viewModelProvider: LendingWithdrawalWaitViewModelAndroid by viewModels { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private val arguments by navArgs<LendingWithdrawalWaitFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }
    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //Do nothing, user will navigate automatically
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingFragmentWithdrawalWaitBinding
        get() = FeatureLendingFragmentWithdrawalWaitBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setLoadingUI()
        observeFlow()
        registerBackPressDispatcher()
        viewModel.makeWithdrawal(UpdateLoanDetailsBodyV2(applicationId = args.loanId))
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.makeWithdrawalFlow.collect(
                    onSuccess = {
                        viewModel.fetchLendingProgress()
                    },
                    onError = { errorMessage, _ ->
                        navigateOnError()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.readyCashJourneyFlow.collect(
                    onSuccess = {
                        it?.let {
                            val status = it.screenData?.get(ReadyCashScreen.DISBURSAL)?.status
                            if (LendingUtil.isWithdrawalSuccess(status)) {
                                navigateOnSuccess()
                            } else if (status == LoanApplicationStatusV2.FAILED.name) {
                                navigateOnFailed()
                            } else {
                                navigateOnError()
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        navigateOnError()
                    }
                )
            }
        }
    }

    private fun navigateOnSuccess() {
        navigateTo(
            LendingStepsNavigationDirections.actionGlobalTransitionFragmentState(
                TransitionStateScreenArgs(
                    transitionType = LendingConstants.TransitionType.APPLICATION_SUCCESS,
                    destinationDeeplink = null,
                    flowType = flowType,
                    loanId = args.loanId.orEmpty(),
                    isFromRepeatWithdrawal = true,
                    lender = args.lender
                )
            ),
            popUpTo = R.id.lendingWithdrawalWaitFragment,
            inclusive = true
        )
    }

    private fun navigateOnError() {
        val argsData = encodeUrl(
            serializer.encodeToString(
                ReadyCashScreenArgs(
                    loanId = args.loanId,
                    source = args.source,
                    type = args.type,
                    screenName = args.screenName,
                    screenData = args.screenData,
                    isRepeatWithdrawal = args.isRepeatWithdrawal,
                    isRepayment = args.isRepayment
                )
            )
        )
        navigateTo(
            LendingStepsNavigationDirections.actionGlobalServerTimeOutOrPending(
                flowType = LendingServerTimeOutOrPendingFragment.FLOW_TYPE_WITHDRAWAL_SERVER_TIME_OUT,
               screenArgs = argsData
            ),
            popUpTo = R.id.lendingWithdrawalWaitFragment,
            inclusive = true
        )
    }

    private fun navigateOnFailed() {
        navigateTo(
            LendingStepsNavigationDirections.actionGlobalBankApplicationRejectedFragment(args.lender),
            popUpTo = R.id.lendingWithdrawalWaitFragment,
            inclusive = true
        )
    }


    private fun setLoadingUI() {
        binding.lottieView.playLottieUrlSequentially(LendingConstants.LottieUrls.GENERIC_LOADING)
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressCallback)
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}
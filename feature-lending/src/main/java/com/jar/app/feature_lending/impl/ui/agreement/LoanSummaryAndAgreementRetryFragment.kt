package com.jar.app.feature_lending.impl.ui.agreement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingFragmentLoanSummaryAndAgreementRetryBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateStatus
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class LoanSummaryAndAgreementRetryFragment :
    BaseFragment<FeatureLendingFragmentLoanSummaryAndAgreementRetryBinding>() {


    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val viewModel by viewModels<LoanSummaryAndAgreementViewModel> { defaultViewModelProviderFactory }
    private val arguments by navArgs<LoanSummaryAndAgreementRetryFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingFragmentLoanSummaryAndAgreementRetryBinding
        get() = FeatureLendingFragmentLoanSummaryAndAgreementRetryBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUi()
        setupListeners()
        observeFlow()
        registerBackPressDispatcher()
    }

    private fun setupUi() {
        binding.lendingToolbar.btnNeedHelp.isVisible = false
        binding.lendingToolbar.root.isVisible = args.screenData?.shouldShowProgress.orFalse().not()
        binding.lendingToolbar.tvTitle.text =
            getCustomString(MR.strings.feature_lending_back_to_home)
    }

    private fun setupListeners() {
        binding.lendingToolbar.btnBack.setDebounceClickListener {
            handleBackNavigation()
        }
        binding.btnRetry.setDebounceClickListener {
            viewModel.updateCheckpoint(
                UpdateLoanDetailsBodyV2(
                    applicationId = args.loanId,
                    loanAgreement = UpdateStatus(LoanStatus.IN_PROGRESS.name)
                ),
                LendingConstants.LendingApplicationCheckpoints.LOAN_AGREEMENT
            )
        }
    }

    private fun handleBackNavigation() {
        //Back to Home
        val fragment = (parentFragment as NavHostFragment).parentFragment
        fragment?.popBackStack()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateCheckpointFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            goToNextScreen()
                        }
                    },
                    onError = { message, errorCode ->
                        dismissProgressBar()
                        message.snackBar(binding.root)
                    }
                )
            }
        }
    }

    private fun goToNextScreen() {
        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = args.screenName, //same screen checkpoint
                    source = args.screenName,
                    popupToId = R.id.loanSummaryAndAgreementRetryFragment
                )
            )
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
        super.onDestroyView()
    }
}
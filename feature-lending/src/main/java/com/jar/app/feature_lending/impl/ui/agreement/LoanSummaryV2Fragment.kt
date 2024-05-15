package com.jar.app.feature_lending.impl.ui.agreement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnScrollChangedListener
import androidx.activity.OnBackPressedCallback
import androidx.core.text.HtmlCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.handleUrlClicks
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLoanSummaryV2Binding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.domain.model.TransitionStateScreenArgs
import com.jar.app.feature_lending.impl.domain.model.experiment.ReadyCashScreen
import com.jar.app.feature_lending.impl.ui.common.KeyValueAdapter
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.impl.ui.withdrawal_wait.LendingServerTimeOutOrPendingFragment
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus
import com.jar.app.feature_lending.shared.domain.model.v2.BankAccount
import com.jar.app.feature_lending.shared.domain.model.v2.Drawdown
import com.jar.app.feature_lending.shared.domain.model.v2.LoanSummaryV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateStatus
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
internal class LoanSummaryV2Fragment : BaseFragment<FragmentLoanSummaryV2Binding>() {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val parentViewModelProvider by activityViewModels<LendingHostViewModelAndroid> { defaultViewModelProviderFactory }
    private val parentViewModel by lazy {
        parentViewModelProvider.getInstance()
    }

    private val viewModel by viewModels<LoanSummaryAndAgreementViewModel> { defaultViewModelProviderFactory }
    private val arguments by navArgs<LoanSummaryV2FragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    private var summaryAdapter: KeyValueAdapter? = null
    private var breakDownAdapter: KeyValueAdapter? = null
    private var chargesAdapter: KeyValueAdapter? = null
    private var isScrollEventSent = false

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        }

    private val scrollListener = OnScrollChangedListener {
        if (isBindingInitialized().not()) return@OnScrollChangedListener

        if (binding.svContent.getChildAt(0).bottom
            <= binding.svContent.height + binding.svContent.scrollY) {
            //scroll view is at bottom
            if (isScrollEventSent.not()){
                isScrollEventSent = true
                analyticsApi.postEvent(
                    LendingEventKeyV2.Lending_SummaryScreenAction,
                    mapOf(
                        LendingEventKeyV2.user_type to getUserType(),
                        LendingEventKeyV2.action to LendingEventKeyV2.scrolled,
                        LendingEventKeyV2.screen_name to LendingEventKeyV2.summary_screen,
                        LendingEventKeyV2.lender to args.lender.orEmpty()
                    )
                )

            }
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoanSummaryV2Binding
        get() = FragmentLoanSummaryV2Binding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            LendingEventKeyV2.Lending_SummaryScreenShown,
            mapOf(
                LendingEventKeyV2.isFromRepayment to args.isRepayment,
                LendingEventKeyV2.isFromRepeatWithdrawal to args.isRepeatWithdrawal,
                LendingEventKeyV2.user_type to getUserType(),
                LendingEventKeyV2.lender to args.lender.orEmpty()
            )

        )
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        if (args.isRepayment) {
            binding.lendingToolbar.root.isVisible = true
            binding.lendingToolbar.tvTitle.text =
                getCustomString(MR.strings.feature_lending_jar_loan_details)
            binding.lendingToolbar.separator.isVisible = true
            binding.lendingToolbar.btnNeedHelp.isVisible = false
            binding.btnAction.isVisible = false
            binding.llBottom.isVisible = false
            binding.tvEmiTitle.text = getCustomString(MR.strings.feature_lending_loan_summary)
        } else if (args.isRepeatWithdrawal || args.screenData?.shouldShowProgress.orFalse().not()) {
            binding.lendingToolbar.root.isVisible = true
            binding.lendingToolbar.tvTitle.text =
                getCustomString(MR.strings.feature_lending_loan_details)
            binding.tvEmiTitle.text =
                getCustomString(MR.strings.feature_lending_confirm_your_ready_cash_details)
            binding.btnAction.setText(getCustomString(MR.strings.feature_lending_confirm_details))
            binding.btnAction.setButtonTextAllCaps(false)
        } else {
            binding.lendingToolbar.root.isVisible = false
        }

        summaryAdapter = KeyValueAdapter()
        breakDownAdapter = KeyValueAdapter()
        chargesAdapter = KeyValueAdapter()


        summaryAdapter?.setColor(keyColor=com.jar.app.core_ui.R.color.commonTxtColor,valueColor=com.jar.app.core_ui.R.color.commonTxtColor)
        breakDownAdapter?.setColor(keyColor=com.jar.app.core_ui.R.color.commonTxtColor,valueColor=com.jar.app.core_ui.R.color.commonTxtColor)
        chargesAdapter?.setColor(keyColor=com.jar.app.core_ui.R.color.commonTxtColor,valueColor=com.jar.app.core_ui.R.color.commonTxtColor)

        binding.rvBreakdown.adapter = breakDownAdapter
        binding.rvCharges.adapter = chargesAdapter
        binding.rvDetails.adapter = summaryAdapter

        if (isLoanSummaryAndAgreement()) {
            binding.llBottom.isVisible = false
            binding.tvEmiTitle.text =
                getCustomString(MR.strings.feature_lending_confirm_your_ready_cash_details)
        } else {
            binding.llBottom.isVisible = true
            parentViewModel.preApprovedDataFlow.asLiveData().value?.data?.data?.lenderLogoUrl?.let {
                Glide.with(requireContext()).load(it).into(binding.ivLender)
            } ?: kotlin.run {
                parentViewModel.fetchPreApprovedData()
            }
        }
        registerOtpListener()
    }

    private fun getUserType() = if (args.isRepeatWithdrawal) "Repeat" else "New"

    private fun isLoanSummaryAndAgreement() =
        args.screenName == ReadyCashScreen.LOAN_SUMMARY_AND_AGREEMENT

    private fun setupListeners() {
        binding.lendingToolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_SummaryScreenBackClicked,
                mapOf(
                    LendingEventKeyV2.isFromRepayment to args.isRepayment,
                    LendingEventKeyV2.isFromRepeatWithdrawal to args.isRepeatWithdrawal,
                    LendingEventKeyV2.user_type to getUserType(),
                    LendingEventKeyV2.lender to args.lender.orEmpty()
                )
            )
            handleBackNavigation()
        }

        binding.lendingToolbar.btnNeedHelp.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_SummaryScreenNeedHelpClicked,
                mapOf(
                    LendingEventKeyV2.user_type to getUserType(),
                    LendingEventKeyV2.screen_name to LendingEventKeyV2.summary_screen,
                    LendingEventKeyV2.lender to args.lender.orEmpty()
                )
            )
            val message = getCustomStringFormatted(
                MR.strings.feature_lending_jar_ready_cash_need_help_template,
                getCustomString(MR.strings.feature_lending_i_need_help_regarding_ready_cash),
                prefs.getUserName().orEmpty(),
                prefs.getUserPhoneNumber().orEmpty()
            )
            requireContext().openWhatsapp(remoteConfigManager.getWhatsappNumber(), message)
        }

        binding.ivBreakdownInfo.setDebounceClickListener {
            navigateTo(
                LoanSummaryV2FragmentDirections.actionLoanSummaryV2FragmentToBreakdownInfoBottomSheetFragment(
                    args.loanId.orEmpty(), BreakdownInfoBottomSheetFragment.ARGS_INFO
                )
            )
        }

        binding.ivChargesInfo.setDebounceClickListener {
            navigateTo(
                LoanSummaryV2FragmentDirections.actionLoanSummaryV2FragmentToBreakdownInfoBottomSheetFragment(
                    args.loanId.orEmpty(), BreakdownInfoBottomSheetFragment.ARGS_CHARGES
                )
            )
        }

        binding.btnAction.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_SummaryScreenProceedClicked,
                mapOf(LendingEventKeyV2.user_type to getUserType(),
                    LendingEventKeyV2.lenderName to args.lender.orEmpty()
                    )
            )
            if (isLoanSummaryAndAgreement()) {
                viewModel.updateCheckpoint(
                    UpdateLoanDetailsBodyV2(
                        applicationId = args.loanId,
                        withdrawalDetails = UpdateStatus(LoanStatus.IN_PROGRESS.name)
                    ),
                    LendingConstants.LendingApplicationCheckpoints.WITHDRAWAL
                )
            } else if (args.isRepeatWithdrawal) {
                navigateTo(
                    LendingStepsNavigationDirections.actionGlobalOtpVerificationFragment(
                        flowType = BaseConstants.FROM_LENDING,
                        loanId = args.loanId.orEmpty(),
                        isFromRepeatWithdrawal = true
                    )
                )
            } else {
                parentViewModel.updateLoanSummaryVisited(
                    UpdateLoanDetailsBodyV2(
                        applicationId = args.loanId,
                        loanSummary = UpdateStatus(LoanStatus.VERIFIED.name)
                    )
                )
            }
        }
        binding.checkboxConsent.setOnCheckedChangeListener { _, isChecked ->
            binding.btnAction.setDisabled(!isChecked)
        }
        binding.btnRetry.setDebounceClickListener {
            getData()
        }
        binding.svContent.viewTreeObserver.addOnScrollChangedListener(scrollListener)
    }

    private fun goToNextScreen() {
        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.nextScreen,
                    source = args.screenName,
                    popupToId = R.id.loanSummaryV2Fragment
                )
            )
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.readyCashJourneyFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            val status = it.screenData?.get(ReadyCashScreen.DISBURSAL)?.status
                            if (LendingUtil.isWithdrawalSuccess(status)) {
                                navigateAfterWithdrawalSuccess()
                            }// else hold user to current screen until status get verified.
                        }
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.loanDetailsFlow.collect(
                    onLoading = {
                        binding.somethingWentWrongHolder.isVisible = false
                        binding.clContent.isInvisible = true
                        binding.shimmerLayout.isVisible = true
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        binding.somethingWentWrongHolder.isVisible = false
                        binding.clContent.isVisible = true
                        binding.shimmerLayout.isVisible = false
                        it?.applicationDetails?.let {
                            setLoanSummary(it.loanSummary)
                            setEmiDetails(it.drawdown)
                            setBankData(it.bankAccount)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        binding.clContent.isInvisible = true
                        binding.shimmerLayout.isVisible = false
                        binding.btnAction.setDisabled(true)
                        binding.somethingWentWrongHolder.isVisible = true
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.preApprovedDataFlow.collect(
                    onSuccess = {
                        Glide.with(requireContext()).load(it?.lenderLogoUrl).into(binding.ivLender)
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.updateLoanSummaryFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        goToNextScreen()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateCheckpointFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.fetchLendingProgress()
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        viewModel.fetchLendingProgress()
                    },
                    onError = { errorMessage, errorCode ->
                        dismissProgressBar()
                        if (errorCode == LendingConstants.WITHDRAWAL_ERROR_CODE) {
                            navigateToWithdrawalRetryScreen()
                        } else {
                            errorMessage.snackBar(binding.root)
                        }
                    }
                )
            }
        }
    }

    private fun navigateToWithdrawalRetryScreen() {
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
            )
        )
    }

    private fun navigateAfterWithdrawalSuccess() {
        navigateTo(
            LendingStepsNavigationDirections.actionGlobalTransitionFragmentState(
                TransitionStateScreenArgs(
                    transitionType = LendingConstants.TransitionType.APPLICATION_SUCCESS,
                    destinationDeeplink = null,
                    flowType = BaseConstants.FROM_LENDING,
                    loanId = args.loanId.orEmpty(),
                    isFromRepeatWithdrawal = args.isRepeatWithdrawal,
                    lender = args.lender
                )
            ),
            popUpTo = R.id.loanSummaryV2Fragment,
            inclusive = true
        )
    }

    private fun getData() {
        parentViewModel.fetchLoanDetails(
            LendingConstants.LendingApplicationCheckpoints.LOAN_SUMMARY + "," +
                    LendingConstants.LendingApplicationCheckpoints.BANK_ACCOUNT_DETAILS + "," +
                    LendingConstants.LendingApplicationCheckpoints.DRAW_DOWN,
            true,
            args.loanId.orEmpty()
        )
    }

    private fun setLoanSummary(loanSummaryV2: LoanSummaryV2?) {
        loanSummaryV2 ?: return
        summaryAdapter?.submitList(loanSummaryV2.readyCashDetails)
        breakDownAdapter?.submitList(loanSummaryV2.readyCashBreakdown)
        chargesAdapter?.submitList(loanSummaryV2.readyCashCharges)
        binding.tvCashToBeCreated.text = getCustomStringFormatted(
            MR.strings.feature_lending_rupee_prefix_string,
            loanSummaryV2.amountToBeCredited.orZero().getFormattedAmount()
        )
        loanSummaryV2.withdrawalConsent?.let {
            binding.consentHolder.isVisible = true
            val consent = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.tvIHereByConsent.text = consent.trim()
            binding.btnAction.setText(getString(com.jar.app.core_ui.R.string.core_ui_confirm))
        } ?: run {
            binding.consentHolder.isVisible = false
            binding.btnAction.setDisabled(false)
        }
        binding.tvIHereByConsent.handleUrlClicks { url, text ->
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_SummaryScreenAction,
                mapOf(
                    LendingEventKeyV2.isFromRepayment to args.isRepayment,
                    LendingEventKeyV2.isFromRepeatWithdrawal to args.isRepeatWithdrawal,
                    LendingEventKeyV2.user_type to getUserType(),
                    LendingEventKeyV2.lender to args.lender.orEmpty(),
                    LendingEventKeyV2.action to LendingEventKeyV2.consent_text_clicked,
                    LendingEventKeyV2.consent_text to text,
                    LendingEventKeyV2.url to url
                )

            )
        }
    }

    private fun setEmiDetails(drawdown: Drawdown?) {
        drawdown ?: return
        binding.tvRoi.text =
            "${drawdown.roi.orZero()}% p.a"      //Done intentionally, won't be translated
        binding.tvEmiAmount.text =
            getCustomStringFormatted(
                MR.strings.feature_lending_rupee_prefix_string_per_month,
                drawdown.emiAmount.orZero().toString()
            )
        binding.tvDuration.text =
            getCustomStringFormatted(
                MR.strings.feature_lending_x_months, drawdown.tenure.orZero()
            )
        binding.tvFirstEmiDate.text = drawdown.firstEMIDate
        binding.tvLastEmiDate.text = drawdown.lastEMIDate
    }

    private fun setBankData(bankAccount: BankAccount?) {
        bankAccount?.let {
            binding.cardBankDetails.isVisible = true
            Glide.with(requireContext()).load(bankAccount.bankLogo).into(binding.ivBankLogo)
            binding.tvBankName.text = bankAccount.bankName
            binding.tvAccountNumber.text = bankAccount.accountNumber
        } ?: kotlin.run {
            binding.cardBankDetails.isVisible = false
        }
    }

    private fun registerOtpListener() {
        setFragmentResultListener(
            LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_KEY
        ) { _, bundle ->
            when (bundle.getString(LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_RESULT)) {
                LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_SUCCESS -> {
                    navigateTo(
                        LendingStepsNavigationDirections.actionGlobalTransitionFragmentState(
                            TransitionStateScreenArgs(
                                transitionType = LendingConstants.TransitionType.OTP_SUCCESS,
                                destinationDeeplink = null,
                                flowType = args.type,
                                loanId = args.loanId.orEmpty(),
                                isFromRepeatWithdrawal = args.isRepeatWithdrawal,
                                lender = args.lender
                            )
                        )
                    )
                }

                LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_WITHDRAWAL_ERROR -> {
                    navigateToWithdrawalRetryScreen()
                }

                LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_EXHAUSTED -> {
                    EventBus.getDefault().post(GoToHomeEvent(ReadyCashScreen.LOAN_SUMMARY))
                }

                else -> {
                    /*Do Nothing*/
                }
            }
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
        binding.svContent.viewTreeObserver.removeOnScrollChangedListener(scrollListener)
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(
            LendingToolbarVisibilityEventV2(
                args.screenData?.shouldShowProgress.orFalse().not()
            )
        )
    }

    private fun handleBackNavigation() {
        if (args.isRepayment) {
            popBackStack()
            return
        }
        EventBus.getDefault().post(LendingBackPressEvent(LendingEventKeyV2.RCASH_SUMMARY_SCREEN))
        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.backScreen,
                    source = args.screenName,
                    popupToId = R.id.loanSummaryV2Fragment,
                    isBackFlow = true
                )
            )
        }
    }

    private fun sendEvent(action: String) {
        analyticsApi.postEvent(
            LendingEventKeyV2.Lending_SummaryScreenAction,
            mapOf(
                LendingEventKeyV2.isFromRepayment to args.isRepayment,
                LendingEventKeyV2.isFromRepeatWithdrawal to args.isRepeatWithdrawal,
                LendingEventKeyV2.user_type to getUserType(),
                LendingEventKeyV2.lender to args.lender.orEmpty(),
                LendingEventKeyV2.action to action,
            )

        )
    }
}
package com.jar.app.feature_lending.impl.ui.final_details

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.RefreshLendingEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.openUrlInChromeTab
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_lending.LendingNavigationDirections
import com.jar.app.feature_lending.databinding.FragmentLoanFinalDetailsBinding
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.v2.*
import com.jar.app.feature_lending.impl.ui.common.KeyValueAdapter
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR
import com.jar.internal.library.jar_core_network.api.util.collect

@AndroidEntryPoint
internal class LoanFinalDetailsFragment : BaseFragment<FragmentLoanFinalDetailsBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val viewModelProvider: LoanFinalDetailsViewModelAndroid by viewModels { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private var adapter: KeyValueAdapter? = null

    private val arguments by navArgs<LoanFinalDetailsFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    private var lastState = LoanApplicationStatusV2.IN_PROGRESS.name

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoanFinalDetailsBinding
        get() = FragmentLoanFinalDetailsBinding::inflate

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goToHome()
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        observeLiveData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.feature_lending_jar_ready_cash)
        adapter = KeyValueAdapter()
        binding.rvDetails.adapter = adapter
    }

    private fun setHeaderTitle(isLoanDisbursed: Boolean) {
        if (isLoanDisbursed) {
            binding.tvStatusMsg.isVisible = true
            binding.tvStatusMsg.text =
                getCustomString(MR.strings.feature_lending_your_ready_cash_has_been_credited)
            binding.timeLineGroup.isVisible = false
            binding.timerView.isVisible = false
        } else {
            binding.timeLineGroup.isVisible = true
        }
    }

    private fun setupTimelineUi(isLoanDisbursed: Boolean) {
        binding.timeLineGroup.isVisible = true
        binding.timeLine.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (isLoanDisbursed) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.lightBgColor2
            )
        )

        binding.tvStep2.setBackgroundResource(if (isLoanDisbursed) com.jar.app.core_ui.R.drawable.core_ui_circle_white_border else com.jar.app.core_ui.R.drawable.core_ui_bg_circle_white)
        binding.tvStep2.backgroundTintList = if (isLoanDisbursed) null
        else ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.lightBgColor2
            )
        )
        binding.tvStep2.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isLoanDisbursed) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
            )
        )

        binding.tvStep2Title.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isLoanDisbursed) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_776E94
            )
        )
    }

    private fun startCountDownTimer(millis: Long) {
        binding.timerView.startTimer(
            durationInMillis = millis,
            uiScope = uiScope,
            onInterval = {

            },
            onFinished = {
                getData()
            }
        )
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loanDetailsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setLoanDetails(it)
                            setHeaderTitle(isLoanDisbursed(it))
                            setupTimelineUi(isLoanDisbursed(it))
                            if (isLoanForeclosed(it))
                                toggleUiToForeclosed()
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    fun isLoanDisbursed(loanDetailsV2: LoanDetailsV2) =
        loanDetailsV2?.status == LoanApplicationStatusV2.DISBURSED.name

    fun isLoanForeclosed(loanDetailsV2: LoanDetailsV2) =
        loanDetailsV2?.status == LoanApplicationStatusV2.FORECLOSED.name
    private fun setLoanDetails(loanDetailsV2: LoanDetailsV2) {
        uiScope.launch {
            loanDetailsV2.applicationDetails?.bankAccount?.let {
                setBankData(it)
            }
            loanDetailsV2.applicationDetails?.withdrawal?.authorizedPartner?.let {
                    keyValue->
                binding.linkHolder.isVisible = true
                binding.tvAbflAuthorisedPartners.text = keyValue.key
                binding.tvAbflAuthorisedPartners.paintFlags = binding.tvAbflAuthorisedPartners.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                binding.tvAbflAuthorisedPartners.setDebounceClickListener {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(keyValue.value)))
                    }catch (ignore:Exception){ }
                }
            }?:run{
                binding.linkHolder.isVisible = false
            }
            loanDetailsV2.applicationDetails?.withdrawal?.countDownTimeInMillis?.let {
                if (it > 0) {
                    binding.timerView.isVisible = true
                    startCountDownTimer(it)
                } else {
                    binding.timerView.isVisible = false
                    binding.tvStep1SubTitle.isVisible = isLoanDisbursed(loanDetailsV2).not()
                    binding.tvStep1SubTitle.text =
                        getCustomString(MR.strings.feature_lending_this_is_taking_longer_than_expected)
                }
            } ?: run {
                binding.timerView.isVisible = false
                binding.tvStep1SubTitle.isVisible = isLoanDisbursed(loanDetailsV2).not()
                binding.tvStep1SubTitle.text =
                    getCustomString(MR.strings.feature_lending_this_is_taking_longer_than_expected)
            }
            loanDetailsV2.applicationDetails?.loanSummary?.let {
                setReadyCashData(it)
            }
            loanDetailsV2.applicationDetails?.foreclosure?.let {
                setForeclosureOnUi(it)
            }
            if (isLoanForeclosed(loanDetailsV2))
                toggleUiToForeclosed()
        }

    }

    private fun toggleUiToForeclosed() {
        analyticsApi.postEvent(LendingEventKeyV2.Lending_ForeclosedloanScreenShown)
        binding.timeLineGroup.isVisible = false
        binding.timerView.isVisible = false
        binding.btnAction.isVisible = false
        binding.clCardForeclose.isVisible = false
        binding.cardBankDetails.isVisible = false
        binding.clAnotherLoanHolder.isVisible = true
        binding.tvStatusMsg.text = getCustomString(MR.strings.feature_lending_your_loan_has_been_foreclosed)
    }

    private fun setForeclosureOnUi(foreclosureData: ForeclosureData) {
        binding.timeLineGroup.isVisible = false
        binding.timerView.isVisible = false
        binding.btnAction.isVisible = false
        binding.clCardForeclose.isVisible = true
        binding.tvTotalAmountValue.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string,
            foreclosureData.totalAmount?.getFormattedAmount().orEmpty()
        )
        foreclosureData.message?.let {
            binding.tvForecloseMessage.text = it
        }
        val isCtaEnabled = foreclosureData.foreclosureEnabled.orFalse()
        binding.btnForeclose.setDisabled(isCtaEnabled.not())
        analyticsApi.postEvent(
            if (isCtaEnabled)
                LendingEventKeyV2.Lending_ForeclosureLoanScreenShown
            else
                LendingEventKeyV2.Lending_ForeclosureDisabled
        )
        binding.tvForecloseMessage.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isCtaEnabled) com.jar.app.core_ui.R.color.color_776E94
                else com.jar.app.core_ui.R.color.redAlertText
            )
        )
    }

    private fun setupListener() {
        binding.btnAction.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_YourLoanApprovedScreenClicked,
                mapOf(
                    LendingEventKeyV2.button_type to getCustomLocalizedString(requireContext(),MR.strings.feature_lending_back_to_home_page,prefs.getCurrentLanguageCode()),
                    LendingEventKeyV2.user_type to getUserType(),
                    LendingEventKeyV2.lender to args.lender.orEmpty()
                )
            )
            goToHome()
        }
        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_BackButtonClicked,
                values = mapOf(
                    LendingEventKeyV2.screen_name to LendingEventKeyV2.DISBURSAL_SCREEN,
                    LendingEventKeyV2.lender to args.lender.orEmpty()
                )
            )
            goToHome()
        }
        binding.btnForeclose.setDebounceClickListener {
            analyticsApi.postEvent(LendingEventKeyV2.Lending_ForeclosureLoanClicked)
            navigateTo(
                LendingNavigationDirections.actionToForecloseSummaryFragment(
                    args.loanId.orEmpty(),
                    false
                )
            )
        }
        binding.btnContactUsForMoreLoan.setDebounceClickListener {
            analyticsApi.postEvent(LendingEventKeyV2.Lending_ForeclosedloanScreenContactUsClicked)
            val sendTo = remoteConfigApi.getWhatsappNumber()
            val number = prefs.getUserPhoneNumber()
            val name = prefs.getUserName()
            val message = getCustomStringFormatted(MR.strings.feature_lending_kyc_contact_support_another_loan_s_s,
                name.orEmpty(),
                number.orEmpty()
            )
            requireContext().openWhatsapp(sendTo, message)
        }
        binding.toolbar.btnNeedHelp.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_YourLoanApprovedScreenClicked,
                mapOf(
                    LendingEventKeyV2.button_type to getCustomLocalizedString(requireContext(),com.jar.app.feature_lending.shared.MR.strings.feature_lending_need_help,prefs.getCurrentLanguageCode()),
                    LendingEventKeyV2.user_type to getUserType(),
                    LendingEventKeyV2.lender to args.lender.orEmpty()
                )
            )
            val sendTo = remoteConfigApi.getWhatsappNumber()
            val number = prefs.getUserPhoneNumber()
            val name = prefs.getUserName()
            val message = getCustomStringFormatted(MR.strings.feature_lending_jar_ready_cash_need_help_template,
                getCustomString(MR.strings.feature_lending_i_need_help_regarding_ready_cash),
                name.orEmpty(),
                number.orEmpty()
            )
            requireContext().openWhatsapp(sendTo, message)
        }
    }

    private fun goToHome() {
        EventBus.getDefault().post(LendingBackPressEvent(LendingEventKeyV2.LOAN_APPROVED_SCREEN))
        EventBus.getDefault().post(GoToHomeEvent("LOAN_FINAL_DETAILS"))
    }

    private fun setBankData(bankAccount: BankAccount) {
        Glide.with(requireContext()).load(bankAccount.bankLogo).into(binding.ivBankLogo)
        binding.tvBankName.text = bankAccount.bankName
        binding.tvAccountNumber.text = bankAccount.accountNumber
    }

    private fun setReadyCashData(loanSummaryV2: LoanSummaryV2) {
        adapter?.submitList(loanSummaryV2.readyCashDetails)
        binding.tvCashToBeCreated.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string,
            loanSummaryV2.amountToBeCredited.orZero().getFormattedAmount()
        )
    }

    private fun getData() {
        viewModel.fetchLoanDetails(
            args.loanId.orEmpty(),
            LendingConstants.LendingApplicationCheckpoints.BANK_ACCOUNT_DETAILS + "," +
                    LendingConstants.LendingApplicationCheckpoints.WITHDRAWAL + "," +
                    LendingConstants.LendingApplicationCheckpoints.LOAN_SUMMARY + "," +
                    LendingConstants.LendingApplicationCheckpoints.FORECLOSURE,
        )
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun onDestroyView() {
        EventBus.getDefault().post(RefreshLendingEvent())
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }

    private fun getUserType() = if (args.isRepeatWithdrawal) "Repeat" else "New"
}
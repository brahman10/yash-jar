package com.jar.app.feature_lending.impl.ui.mandate.consent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarTitleEventV2
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_utils.data.NetworkUtil
import com.jar.app.core_ui.extension.setSeeMoreOrLessView
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLoanMandateConsentBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.domain.event.UpdateLendingStepsToolbarEvent
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.v2.BankAccount
import com.jar.app.feature_lending.shared.domain.model.v2.MandateSetupUpdatedContent
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep
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
internal class LoanMandateConsentFragment : BaseFragment<FragmentLoanMandateConsentBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var netUtil: NetworkUtil

    private val parentViewModelProvider by activityViewModels<LendingHostViewModelAndroid> { defaultViewModelProviderFactory }
    private val parentViewModel by lazy {
        parentViewModelProvider.getInstance()
    }

    private val viewModelProvider by viewModels<LoanConsentViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private val arguments by navArgs<LoanMandateConsentFragmentArgs>()
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

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoanMandateConsentBinding
        get() = FragmentLoanMandateConsentBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateLendingStepsToolbarEvent(shouldShowSteps = true, LendingStep.LOAN_AGREEMENT)
        )
    }


    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
        getData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        binding.btnAction.setDisabled(true)
        EventBus.getDefault()
            .post(LendingToolbarTitleEventV2(getCustomStringFormatted(MR.strings.feature_lending_complete_agreement)))
    }

    private fun setupListeners() {

        binding.tvNetBanking.setOnButtonClickListener {
            viewModel.currentAuthType = LendingConstants.MandateAuthType.NET_BANKING
            binding.tvNetBanking.setButtonSelected(true)
            binding.tvDebitCard.setButtonSelected(false)
        }

        binding.tvDebitCard.setOnButtonClickListener {
            viewModel.currentAuthType = LendingConstants.MandateAuthType.DEBIT_CARD
            binding.tvDebitCard.setButtonSelected(true)
            binding.tvNetBanking.setButtonSelected(false)
        }
        binding.btnAction.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_SummaryScreenSetupEMIClicked,
                values = mapOf(
                    LendingEventKeyV2.selected_automation_mode to viewModel.currentAuthType
                )
            )
            viewModel.updateMandateConsent(
                args.loanId.orEmpty(),
                viewModel.currentAuthType,
                netUtil.getLocalIpAddress()
            )
        }
        binding.checkboxConsent.setOnCheckedChangeListener { _, isChecked ->
            binding.btnAction.setDisabled(!isChecked)
            if (isChecked){
                analyticsApi.postEvent(
                    LendingKycEventKey.Lending_Checkbox_Clicked,
                    mapOf(
                        LendingKycEventKey.screen_name to LendingKycEventKey.Lending_MandateSetupScreen,
                        LendingKycEventKey.check_box to LendingKycEventKey.MandateSetupcheck
                    )
                )
            }
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.staticContentFlow.collect(
                    onSuccess = {
                        it?.mandateSetupUpdatedContent?.let {
                            setUpdatedContent(it)
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.loanDetailsFlow.collect(
                    onSuccess = {
                        it?.let {
                            it.applicationDetails?.bankAccount?.let {
                                setBankData(it)
                            }
                        }
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.preApprovedDataFlow.collect(
                    onSuccess = {
                        it?.lenderLogoUrl?.let {
                            Glide.with(requireContext()).load(it).into(binding.ivLender)
                        }
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateMandateDetailsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
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
                            LendingStepsNavigationDirections.actionToMadateStatusFragment(argsData),
                            popUpTo = R.id.loanMandateConsentFragment,
                            inclusive = true
                        )
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun setUpdatedContent(content: MandateSetupUpdatedContent) {
        binding.tvHeaderInfoTitle.text = content.instruction.orEmpty()
        binding.tvMandateConsent.setSeeMoreOrLessView(
            fullText = content.consent.orEmpty()
        ){
            updateConsentCheckbox()
        }
    }

    private fun updateConsentCheckbox(){
        binding.checkboxConsent.isChecked = !binding.checkboxConsent.isChecked
        binding.btnAction.setDisabled(!binding.checkboxConsent.isChecked)
        if (binding.checkboxConsent.isChecked){
            analyticsApi.postEvent(
                LendingKycEventKey.Lending_Checkbox_Clicked,
                mapOf(
                    LendingKycEventKey.screen_name to LendingKycEventKey.Lending_MandateSetupScreen,
                    LendingKycEventKey.check_box to LendingKycEventKey.MandateSetupcheck
                )
            )
        }
    }

    private fun setBankData(bankAccount: BankAccount) {
        binding.cardBankDetails.setBankName(bankAccount.bankName.orEmpty())
        binding.cardBankDetails.setBankIcon(bankAccount.bankLogo.orEmpty())
        binding.cardBankDetails.setBankAccountNumber(
            getCustomStringFormatted(
                requireContext(),
                MR.strings.feature_lending_saving_account_s,
                bankAccount.accountNumber.orEmpty()
            )
        )
        analyticsApi.postEvent(
            LendingKycEventKey.Lending_MandateSetupScreen,
            mapOf(
                LendingKycEventKey.action to LendingEventKeyV2.shown,
                LendingKycEventKey.linked_bank to bankAccount.bankName.orEmpty(),
                LendingEventKeyV2.lender to args.lender.orEmpty(),
            )
        )
    }

    private fun getData() {
        viewModel.fetchStaticContent(args.loanId.orEmpty())
        parentViewModel.fetchLoanDetails(
            LendingConstants.LendingApplicationCheckpoints.BANK_ACCOUNT_DETAILS
        )
        parentViewModel.preApprovedDataFlow.asLiveData().value?.data?.data?.lenderLogoUrl?.let {
            Glide.with(requireContext()).load(it).into(binding.ivLender)
        } ?: kotlin.run {
            parentViewModel.fetchPreApprovedData()
        }
    }

    private fun handleBackNavigation() {
        EventBus.getDefault()
            .post(LendingBackPressEvent(LendingEventKeyV2.BANK_ACCOUNTS_SHOWN_SCREEN))

        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.backScreen,
                    source = args.screenName,
                    popupToId = R.id.selectEMIFragment,
                    isBackFlow = true
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
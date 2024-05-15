package com.jar.app.feature_lending.impl.ui.bank.confirm_bank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingFragmentConfirmBankDetailBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus
import com.jar.app.feature_lending.shared.domain.model.v2.BankAccount
import com.jar.app.feature_lending.shared.domain.model.v2.BankVerificationDetails
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.impl.ui.bank.enter_account.BankCheckListAdapter
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class ConfirmBankDetailFragment :
    BaseFragment<FeatureLendingFragmentConfirmBankDetailBinding>() {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val viewModelProvider by viewModels<ConfirmBankDetailViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var adapter: BankCheckListAdapter? = null

    private val arguments by navArgs<ConfirmBankDetailFragmentArgs>()
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

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingFragmentConfirmBankDetailBinding
        get() = FeatureLendingFragmentConfirmBankDetailBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
        getData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        binding.lendingToolbar.root.isVisible = true
        binding.lendingToolbar.tvTitle.text =
            getCustomString(MR.strings.feature_lending_bank_accounts)
        binding.rvStaticInfo.layoutManager = LinearLayoutManager(requireContext())
        adapter = BankCheckListAdapter()
        binding.rvStaticInfo.adapter = adapter

    }

    private fun setupListeners() {
        binding.lendingToolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_RepeatWBackButtonClicked,
                mapOf(LendingEventKeyV2.screen_name to LendingEventKeyV2.confirm_bank_screen)
            )
            handleBackNavigation()
        }

        binding.lendingToolbar.btnNeedHelp.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_RepeatWBankDetailScreenNeedHelpClicked,
                mapOf(
                    LendingEventKeyV2.user_type to getUserType(),
                    LendingEventKeyV2.screen_name to LendingEventKeyV2.confirm_bank_screen
                )
            )
            val message = getCustomStringFormatted(
                MR.strings.feature_lending_jar_ready_cash_need_help_template,
                getCustomString(MR.strings.feature_lending_i_need_help_regarding_my_bank_account),
                prefs.getUserName().orEmpty(),
                prefs.getUserPhoneNumber().orEmpty()
            )
            requireContext().openWhatsapp(remoteConfigManager.getWhatsappNumber(), message)
        }

        binding.btnConfirmDetails.setDebounceClickListener {
            viewModel.bankAccount?.let {
                analyticsApi.postEvent(
                    LendingEventKeyV2.Lending_RepeatWBankDetailScreenClicked,
                    mapOf(LendingEventKeyV2.button_title to getCustomLocalizedString(requireContext(),com.jar.app.feature_lending.shared.MR.strings.feature_lending_confirm_details,prefs.getCurrentLanguageCode()),
                        LendingEventKeyV2.user_type to getUserType(),
                        LendingEventKeyV2.bank_details to it.toString()
                    )
                )
                viewModel.confirmBankDetails(
                    UpdateLoanDetailsBodyV2(
                        applicationId = args.loanId,
                        bankVerificationDetails = BankVerificationDetails(
                            bankName = it.bankName,
                            accountNumber = it.accountNumber,
                            ifsc = it.ifsc,
                            bankLogo = it.bankLogo,
                            status = LoanStatus.VERIFIED.name
                        )
                    )
                )
            }
        }

        binding.btnChangeBankAccount.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_RepeatWBankDetailScreenClicked,
                mapOf(LendingEventKeyV2.button_title to getCustomLocalizedString(requireContext(),com.jar.app.feature_lending.shared.MR.strings.feature_lending_change_bank_account,prefs.getCurrentLanguageCode()),
                    LendingEventKeyV2.user_type to getUserType()
                )
            )
            navigateTo(
                ConfirmBankDetailFragmentDirections.actionConfirmBankDetailFragmentToChangeYourBankBottomSheet(),
                shouldAnimate = true
            )
        }
    }

    //This screen only come for repeat withdrawal
    private fun getUserType()= "Repeat"
    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.staticContentFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.bankContent?.let {
                            adapter?.submitList(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    viewModel.loanDetailsFlow.collect(
                        onSuccess = {
                            it?.applicationDetails?.bankAccount?.let {
                                setBankData(it)
                                analyticsApi.postEvent(
                                    LendingEventKeyV2.Lending_RepeatWBankDetailScreenLaunched,
                                    mapOf(
                                        LendingEventKeyV2.user_type to getUserType(),
                                        LendingEventKeyV2.bank_details to it.toString()
                                    )
                                )
                            }
                        }
                    )
                }
            }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateResponseFlow.collect(
                    onSuccess = {
                        navigateToNextScreen()
                    }
                )
            }
        }

    }

    private fun navigateToNextScreen() {
        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.nextScreen,
                    source = args.screenName,
                    popupToId = R.id.confirmBankDetailFragment
                )
            )
        }
    }
    private fun handleBackNavigation() {
        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.backScreen,
                    source = args.screenName,
                    popupToId = R.id.confirmBankDetailFragment,
                    isBackFlow = true
                )
            )
        }
    }
    private fun setBankData(bankAccount: BankAccount) {
        viewModel.bankAccount = bankAccount
        binding.clBankDetails.isVisible = true
        Glide.with(requireContext()).load(bankAccount.bankLogo).into(binding.ivBankLogo)
        binding.tvBankName.text = bankAccount.bankName
        binding.tvAccountNumber.text = bankAccount.accountNumber
    }

    private fun getData() {
        viewModel.fetchBankDetail(args.loanId.orEmpty())
        viewModel.fetchStaticContent(args.loanId.orEmpty(), LendingConstants.StaticContentType.BANK_SCREEN)
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        adapter = null
        super.onDestroyView()
    }


}
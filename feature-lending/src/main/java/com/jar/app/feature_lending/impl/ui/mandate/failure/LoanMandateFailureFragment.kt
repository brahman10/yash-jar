package com.jar.app.feature_lending.impl.ui.mandate.failure

import android.graphics.Paint
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
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLoanMandateFailureBinding
import com.jar.app.feature_lending.impl.ui.mandate.consent.LoanMandateConsentFragmentArgs
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.mandate.MandateSetupFailureContent
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class LoanMandateFailureFragment : BaseFragment<FragmentLoanMandateFailureBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    private val viewModelProvider by viewModels<LoanMandateFailureViewModelAndroid> { defaultViewModelProviderFactory }
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
                onCrossClick()
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoanMandateFailureBinding
        get() = FragmentLoanMandateFailureBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            LendingToolbarVisibilityEventV2(shouldHide = true)
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        binding.tvContactSupport.paintFlags =
            binding.tvContactSupport.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun setupListeners() {
        binding.btnDebitCard.setOnButtonClickListener {
            sendEvent(
                mapOf(
                    LendingEventKeyV2.lenderName to args.lender.orEmpty(),
                    LendingEventKeyV2.action to LendingEventKeyV2.debit_card_clicked,
                )
            )
            updateMandateType(LendingConstants.MandateAuthType.DEBIT_CARD)
        }
        binding.btnNetBanking.setOnButtonClickListener {
            sendEvent(
                mapOf(
                    LendingEventKeyV2.lenderName to args.lender.orEmpty(),
                    LendingEventKeyV2.action to LendingEventKeyV2.net_banking_clicked,
                )
            )
            updateMandateType(LendingConstants.MandateAuthType.NET_BANKING)
        }
        binding.tvContactSupport.setDebounceClickListener {
            sendEvent(
                mapOf(
                    LendingEventKeyV2.lenderName to args.lender.orEmpty(),
                    LendingEventKeyV2.action to LendingEventKeyV2.CONTACT_SUPPORT_CLICKED,
                )
            )
            openNeedHelp()
        }
        binding.ivCross.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_CrossButtonClicked,
                mapOf(
                    LendingEventKeyV2.lenderName to args.lender.orEmpty(),
                    LendingEventKeyV2.screen_name to LendingEventKeyV2.MANDATE_FAILURE_SCREEN,
                )
            )
            onCrossClick()
        }
    }

    private fun onCrossClick() {
        val fragment = (parentFragment as NavHostFragment).parentFragment
        fragment?.popBackStack()
    }

    private fun updateMandateType(mandateType: String) {
        viewModel.updateMandateConsent(
            args.loanId.orEmpty(),
            mandateType
        )
    }

    private fun openNeedHelp() {
        val message = getCustomStringFormatted(
            MR.strings.feature_lending_jar_ready_cash_need_help_template,
            getCustomString(MR.strings.feature_lending_i_need_help_regarding_mandate_failed),
            prefs.getUserName().orEmpty(),
            prefs.getUserPhoneNumber().orEmpty()
        )
        requireContext().openWhatsapp(remoteConfigApi.getWhatsappNumber(), message)
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.staticContentFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.mandateSetupFailureContent?.let { mandateFailureContent ->
                            setFailureData(mandateFailureContent)
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
                            popUpTo = R.id.loanMandateFailureFragment,
                            inclusive = true
                        )
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun setFailureData(content: MandateSetupFailureContent) {
        Glide.with(requireContext()).load(content.iconUrl)
            .into(binding.ivIllustration)
        binding.tvScreenTitle.text = content.title.orEmpty()
        binding.tvDescription.text = content.description.orEmpty()
        binding.btnDebitCard.setTitle(content.debitCardDisplayText.orEmpty())
        binding.btnDebitCard.setStartIcon(content.debitCardIconUrl.orEmpty())
        binding.btnDebitCard.setDisabled(content.isDebitCardEnabled.not())
        content.debitCardAlertMessage?.let {
            binding.tvDebitCardAlert.isVisible = true
            binding.tvDebitCardAlert.text = it
        } ?: {
            binding.tvDebitCardAlert.isVisible = false
        }

        binding.btnNetBanking.setTitle(content.netBankingDisplayText.orEmpty())
        binding.btnNetBanking.setStartIcon(content.netBankingIconUrl.orEmpty())
        binding.btnNetBanking.setDisabled(content.isNetBankingEnabled.not())

        content.netBankingAlertMessage?.let {
            binding.tvNetBankingAlert.isVisible = true
            binding.tvNetBankingAlert.text = it
        } ?: {
            binding.tvNetBankingAlert.isVisible = false
        }
        sendEvent(
            mapOf(
                LendingEventKeyV2.lenderName to args.lender.orEmpty(),
                LendingEventKeyV2.action to LendingEventKeyV2.mandate_failure_screen_shown,
                LendingEventKeyV2.net_banking to content.isNetBankingEnabled,
                LendingEventKeyV2.debit_card to content.isDebitCardEnabled,
                LendingEventKeyV2.failure_title to content.title.orEmpty(),
                LendingEventKeyV2.failure_message to content.description.orEmpty(),
                LendingEventKeyV2.net_banking_disable_message to content.netBankingAlertMessage.orEmpty(),
                LendingEventKeyV2.debit_card_disable_message to content.debitCardAlertMessage.orEmpty()
            )
        )
    }

    private fun getData() {
        viewModel.fetchStaticContent(
            args.loanId.orEmpty(),
            LendingConstants.StaticContentType.MANDATE_SETUP_FAILURE_CONTENT
        )
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }


    private fun sendEvent(properties: Map<String, Any>) {
        analyticsApi.postEvent(
            LendingEventKeyV2.Lending_MandateFailureScreen,
            properties
        )
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}
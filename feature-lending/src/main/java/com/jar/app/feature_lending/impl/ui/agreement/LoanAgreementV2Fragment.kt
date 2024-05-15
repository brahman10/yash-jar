package com.jar.app.feature_lending.impl.ui.agreement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.text.HtmlCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarTitleEventV2
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.openUrlInChromeTab
import com.jar.app.base.util.showToast
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.databinding.FragmentLoanAgreementV2Binding
import com.jar.app.feature_lending.impl.domain.model.TransitionStateScreenArgs
import com.jar.app.feature_lending.impl.domain.model.experiment.ReadyCashScreen
import com.jar.app.feature_lending.impl.ui.common.KeyValueAdapter
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.v2.AgreementDataV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanSummaryV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class LoanAgreementV2Fragment : BaseFragment<FragmentLoanAgreementV2Binding>() {
    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private val parentViewModelProvider by activityViewModels<LendingHostViewModelAndroid> { defaultViewModelProviderFactory }
    private val parentViewModel by lazy {
        parentViewModelProvider.getInstance()
    }
    private var adapter: KeyValueAdapter? = null

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                EventBus.getDefault()
                    .post(LendingBackPressEvent(LendingEventKeyV2.LOAN_AGREEMENT_SCREEN))
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoanAgreementV2Binding
        get() = FragmentLoanAgreementV2Binding::inflate

    private var agreement: AgreementDataV2? = null

    private val arguments by navArgs<LoanAgreementV2FragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    override fun setupAppBar() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            event = LendingEventKeyV2.Lending_LoanAgreementScreenShown,
            values = mapOf(
                LendingEventKeyV2.action to LendingEventKeyV2.shown
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        binding.btnAction.setDisabled(true)
        adapter = KeyValueAdapter()
        binding.rvDetails.adapter = adapter

        parentViewModel.preApprovedDataFlow.asLiveData().value?.data?.data?.lenderLogoUrl?.let {
            Glide.with(requireContext()).load(it).into(binding.ivLender)
        } ?: kotlin.run {
            parentViewModel.fetchPreApprovedData()
        }
        registerOtpListener()

        EventBus.getDefault().post(LendingToolbarTitleEventV2(getCustomString(MR.strings.feature_lending_loan_agreement)))

    }

    private fun setupListeners() {
        binding.cbTnC.setOnCheckedChangeListener { _, isChecked ->
            binding.btnAction.setDisabled(isChecked.not())
        }
        binding.tvTnc.setDebounceClickListener {
            agreement?.agreementLink?.let {
                openUrlInChromeTab(it, it, true)
            } ?: kotlin.run {
                requireContext().showToast(getCustomString(MR.strings.feature_lending_agreement_not_avaialble))
            }
        }

        binding.btnAction.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_LoanAgreementScreenShown,
                values = mapOf(
                    LendingEventKeyV2.action to LendingEventKeyV2.proceed_clicked
                )
            )
            agreement?.let {
                navigateTo(
                    LendingStepsNavigationDirections.actionGlobalOtpVerificationFragment(
                        flowType = BaseConstants.FROM_LENDING,
                        loanId = parentViewModel.getLoanId()
                    )
                )
            } ?: kotlin.run {
                requireContext().showToast(getCustomString(MR.strings.feature_lending_agreement_not_avaialble))
            }
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.loanDetailsFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            it.applicationDetails?.loanAgreement?.let {
                                agreement = it
                                setAgreementData(it)
                            }
                            it.applicationDetails?.loanSummary?.let {
                                setReadyCashData(it)
                            }
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
                parentViewModel.preApprovedDataFlow.collect(
                    onSuccess = {
                        it?.lenderLogoUrl?.let {
                            Glide.with(requireContext()).load(it).into(binding.ivLender)
                        }
                    }
                )
            }
        }
    }

    private fun getData() {
        parentViewModel.fetchLoanDetails(
            LendingConstants.LendingApplicationCheckpoints.LOAN_AGREEMENT + ","
                    + LendingConstants.LendingApplicationCheckpoints.LOAN_SUMMARY,
            true
        )
    }

    private fun setAgreementData(agreementDataV2: AgreementDataV2) {
        binding.tvTnc.text =
            HtmlCompat.fromHtml(agreementDataV2.consent.orEmpty(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setReadyCashData(loanSummaryV2: LoanSummaryV2) {
        adapter?.submitList(loanSummaryV2.readyCashDetails)
        binding.tvCashToBeCreated.text = getCustomStringFormatted(
            MR.strings.feature_lending_rupee_prefix_string,
            loanSummaryV2.amountToBeCredited.orZero().getFormattedAmount()
        )
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
                                loanId = parentViewModel.getLoanId(),
                                isFromRepeatWithdrawal = args.isRepeatWithdrawal,
                                lender = args.lender
                            )
                        )
                    )
                }

                LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_EXHAUSTED -> {
                    EventBus.getDefault().post(GoToHomeEvent(ReadyCashScreen.LOAN_AGREEMENT))
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
        super.onDestroyView()
    }
}
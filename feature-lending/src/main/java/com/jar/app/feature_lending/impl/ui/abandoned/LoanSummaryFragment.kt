package com.jar.app.feature_lending.impl.ui.abandoned

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.underline
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.getEmiAmount
import com.jar.app.base.util.getMaskedString
import com.jar.app.core_base.util.orZero
import com.jar.app.base.util.showToast
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_web_pdf_viewer.api.WEB_TYPE_BASE64
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.databinding.FragmentLoanAggreementBinding
import com.jar.app.feature_lending.impl.domain.event.UpdateLendingStepsToolbarEvent
import com.jar.app.feature_lending.impl.domain.model.TransitionStateScreenArgs
import com.jar.app.feature_lending.impl.ui.abandoned.LoanSummaryFragmentArgs
import com.jar.app.feature_lending.impl.ui.abandoned.LoanSummaryFragmentDirections
import com.jar.app.feature_lending.impl.ui.agreement.LoanSummaryViewModelAndroid
import com.jar.app.feature_lending.shared.domain.model.temp.LoanApplicationDetail
import com.jar.app.feature_lending.impl.ui.common.LendingViewModel
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep
import com.jar.app.feature_lending.shared.domain.LendingEventKey
import com.jar.app.feature_lending.shared.util.LendingConstants
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.lang.ref.WeakReference
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class LoanSummaryFragment : BaseFragment<FragmentLoanAggreementBinding>() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<LoanSummaryFragmentArgs>()

    private val viewModelProvider by viewModels<LoanSummaryViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val lendingViewModel by activityViewModels<LendingViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoanAggreementBinding
        get() = FragmentLoanAggreementBinding::inflate

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val employmentDetails =
                    lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
                val address =
                    lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.ADDRESS
                val drawdown =
                    lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.DRAW_DOWN
                val bankDetails =
                    lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.BANK_ACCOUNT_DETAILS
                analyticsHandler.postEvent(
                    LendingEventKey.OnClick_ReadyCash_Agreement_Back,
                    mapOf(
                        LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                        LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                        LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome()
                            .orZero(),
                        LendingEventKey.entryPoint to args.flowType,
                        LendingEventKey.address to address?.address.orEmpty(),
                        LendingEventKey.emiTenure to drawdown?.tenure.orZero(),
                        LendingEventKey.emiAmount to drawdown?.amount.orZero(),
                        LendingEventKey.accountHolderName to bankDetails?.accountHolderName.orEmpty(),
                        LendingEventKey.accountType to bankDetails?.accountType.orEmpty(),
                        LendingEventKey.ifscCode to bankDetails?.ifsc.orEmpty(),
                        LendingEventKey.verificationStatus to LendingEventKey.success,
                        LendingEventKey.termsAndConditions to if (binding.cbTnC.isChecked) LendingEventKey.yes else LendingEventKey.no
                    )
                )

                lendingViewModel.toolbarBackNavigation(
                    findNavController().currentBackStackEntry,
                    contextRef = WeakReference(requireActivity()),
                    flowType = args.flowType
                )
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateLendingStepsToolbarEvent(shouldShowSteps = true, LendingStep.BANK_DETAILS)
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        lendingViewModel.getLoanApplication()?.let {
            setup()
        } ?: kotlin.run {
            lendingViewModel.fetchLendingProgress(suppressRedirection = true)
        }
    }

    private fun setupUI() {
        binding.btnAction.setDisabled(true)
        registerOtpListener()
        val spannable = buildSpannedString {
            append(getCustomString(MR.strings.feature_lending_by_proceeding_i_accept))
            append(" ")
            color(ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white)) {
                underline {
                    append(getCustomString(MR.strings.feature_lending_loan_terms_and_sanction_letter))
                }
            }
            append(" ")
            append(getCustomString(MR.strings.feature_lending_declare_not_politically_exposed))
        }

        binding.tvTnc.text = spannable

        setData(lendingViewModel.getLoanApplication()?.details)
    }

    private fun setup() {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
        registerBackPressDispatcher()
    }

    private fun setupListeners() {
        binding.ivHelp.setDebounceClickListener {
            navigateTo(
                LendingStepsNavigationDirections.actionGlobalLendingWebViewFragment(
                    remoteConfigApi.getHelpAndSupportUrl(prefs.getCurrentLanguageCode()),
                    isInAppHelp = true,
                    isMandateFlow = false
                )
            )
        }

        binding.btnAction.setDebounceClickListener {
            val employmentDetails =
                lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
            val address =
                lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.ADDRESS
            val drawdown =
                lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.DRAW_DOWN
            val bankDetails =
                lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.BANK_ACCOUNT_DETAILS
            analyticsHandler.postEvent(
                LendingEventKey.OnClick_ReadyCash_Agreement_Proceed,
                mapOf(
                    LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                    LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                    LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                    LendingEventKey.entryPoint to args.flowType,
                    LendingEventKey.address to address?.address.orEmpty(),
                    LendingEventKey.emiTenure to drawdown?.tenure.orZero(),
                    LendingEventKey.emiAmount to drawdown?.amount.orZero(),
                    LendingEventKey.accountHolderName to bankDetails?.accountHolderName.orEmpty(),
                    LendingEventKey.accountType to bankDetails?.accountType.orEmpty(),
                    LendingEventKey.ifscCode to bankDetails?.ifsc.orEmpty(),
                    LendingEventKey.verificationStatus to LendingEventKey.success,
                    LendingEventKey.termsAndConditions to LendingEventKey.yes
                )
            )

            viewModel.loanAgreementFlow.asLiveData().value?.data?.data?.let {
                navigateTo(
                    LoanSummaryFragmentDirections.actionLoanAgreementFragmentToOtpVerificationFragment(
                        flowType = args.flowType,
                        loanId = lendingViewModel.getLoanId()
                    )
                )
            } ?: kotlin.run {
                requireContext().showToast(getCustomString(MR.strings.feature_lending_agreement_not_avaialble))
            }
        }

        binding.cbTnC.setOnCheckedChangeListener { _, isChecked ->
            binding.btnAction.setDisabled(isChecked.not() && viewModel.loanAgreementFlow.asLiveData().value?.data?.data != null)
        }
    }

    private fun registerOtpListener() {
        setFragmentResultListener(
            LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_KEY
        ) { _, bundle ->
            when (bundle.getString(LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_RESULT)) {
                LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_SUCCESS -> {
                    navigateTo(
                        LoanSummaryFragmentDirections.actionGlobalTransitionFragmentState(
                           TransitionStateScreenArgs(
                               transitionType = LendingConstants.TransitionType.OTP_SUCCESS,
                               destinationDeeplink = null,
                               flowType = args.flowType,
                               loanId = lendingViewModel.getLoanId(),
                               lender = null
                           )
                        )
                    )
                }

                LendingConstants.OtpVerificationRequest.OTP_VERIFICATION_REQUEST_EXHAUSTED -> {
                    EventBus.getDefault().post(GoToHomeEvent("LENDING_LOAN_SUMMARY"))
                }

                else -> {
                    /*Do Nothing*/
                }
            }
        }
    }

    private fun getData() {
        viewModel.fetchLoanAgreement(lendingViewModel.getLoanId())
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loanAgreementFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        binding.tvTnc.setDebounceClickListener {
                            webPdfViewerApi.openPdf(lendingViewModel.getLoanId(), WEB_TYPE_BASE64)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        lendingViewModel.loanApplicationsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                setup()
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    /**Any logic change in UI here should also be changed in LendingSummaryViewOnlyFragment**/
    private fun setData(loanApplicationDetails: LoanApplicationDetail?) {
        val drawDown = loanApplicationDetails?.DRAW_DOWN
        val loanAmount = drawDown?.amount
        val tenure = drawDown?.tenure
        val roi = drawDown?.roi
        val monthlyEmiAmount = getEmiAmount(roi?.toDouble()!!, tenure!!, loanAmount?.toDouble()!!)
        val totalRepaymentAmount = monthlyEmiAmount * tenure
        var extraCost = 0f

        drawDown.charges?.forEach {
            extraCost += it.currentAmt.orZero()

            if (it.key == LendingConstants.CHARGE_TYPE_GST) {
                binding.tvGstCut.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_float, it.actualAmt.orZero())
                binding.tvGst.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_float, it.currentAmt.orZero())
                if (it.isDiscountApplicable == true)
                    binding.tvGstCut.paintFlags =
                        binding.tvGstCut.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else
                    binding.tvGst.visibility = View.INVISIBLE
            } else if (it.key == LendingConstants.CHARGE_TYPE_PROCESSING_FEE) {
                binding.tvProcessingFeeCut.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_float, it.actualAmt.orZero())
                binding.tvProcessingFee.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_float, it.currentAmt.orZero())
                if (it.isDiscountApplicable == true)
                    binding.tvProcessingFeeCut.paintFlags =
                        binding.tvProcessingFeeCut.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else
                    binding.tvProcessingFee.visibility = View.INVISIBLE
            }
        }
        val amountToBeCredited = (loanAmount - extraCost)
        binding.tvLoanAmount.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_float, loanAmount)
        binding.tvAmountToCredited.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_float, amountToBeCredited)

        binding.dashedCircleView.setDashCount(tenure)
        binding.tvDuration.text = getCustomStringFormatted(MR.strings.feature_lending_month_prefix, tenure)
        binding.tvEmiAmount.text = getCustomStringFormatted(MR.strings.feature_lending_per_month_prefix, monthlyEmiAmount)
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val date = Instant.ofEpochMilli(lendingViewModel.getFirstEmiDate(drawDown.createdAtEpoch))
            .atZone(ZoneId.systemDefault())
        binding.tvEmiDate.text = date.format(formatter)
        binding.tvRepaymentAmount.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_float, totalRepaymentAmount)

        loanApplicationDetails.BANK_ACCOUNT_DETAILS?.let {
            binding.tvBankName.text = it.bankName
            binding.tvName.text = it.accountHolderName
            binding.tvIfsc.text = it.ifsc
            binding.tvAccountNumber.text = it.accountNumber?.getMaskedString(0, 5)
            Glide.with(this@LoanSummaryFragment)
                .load(it.icon)
                .into(binding.ivBankLogo)
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}
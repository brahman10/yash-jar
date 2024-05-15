package com.jar.app.feature_lending.impl.ui.view_only.summary

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.getEmiAmount
import com.jar.app.base.util.getMaskedString
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLendingSummaryViewOnlyBinding
import com.jar.app.feature_lending.impl.domain.event.UpdateLendingStepsToolbarEvent
import com.jar.app.feature_lending.shared.domain.model.temp.LoanApplicationDetail
import com.jar.app.feature_lending.impl.ui.common.LendingViewModel
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.app.feature_lending.shared.util.LendingFlowType
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.lang.ref.WeakReference
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep

@AndroidEntryPoint
internal class LendingSummaryViewOnlyFragment : BaseFragment<FragmentLendingSummaryViewOnlyBinding>() {

    private val lendingViewModel by activityViewModels<LendingViewModel> { defaultViewModelProviderFactory }

    private val args by navArgs<LendingSummaryViewOnlyFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingSummaryViewOnlyBinding
        get() = FragmentLendingSummaryViewOnlyBinding::inflate

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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
        setupUI(args.loanApplicationDetail)
        initClickListeners()
        registerBackPressDispatcher()
    }

    /**Any logic change in UI here should also be changed in LoanSummaryFragment**/
    private fun setupUI(loanApplicationDetails: LoanApplicationDetail?) {
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
                    binding.tvGstCut.paintFlags = binding.tvGstCut.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else
                    binding.tvGst.visibility = View.INVISIBLE
            } else if (it.key == LendingConstants.CHARGE_TYPE_PROCESSING_FEE) {
                binding.tvProcessingFeeCut.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_float, it.actualAmt.orZero())
                binding.tvProcessingFee.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_float, it.currentAmt.orZero())
                if (it.isDiscountApplicable == true)
                    binding.tvProcessingFeeCut.paintFlags = binding.tvProcessingFeeCut.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
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
        val date = Instant.ofEpochMilli(lendingViewModel.getFirstEmiDate(drawDown.createdAtEpoch)).atZone(ZoneId.systemDefault())
        binding.tvEmiDate.text = date.format(formatter)
        binding.tvRepaymentAmount.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_float, totalRepaymentAmount)

        loanApplicationDetails.BANK_ACCOUNT_DETAILS?.let {
            binding.tvBankName.text = it.bankName
            binding.tvName.text = it.accountHolderName
            binding.tvIfsc.text = it.ifsc
            binding.tvAccountNumber.text = it.accountNumber?.getMaskedString(0, 5)
            Glide.with(requireActivity())
                .load(it.icon)
                .into(binding.ivBankLogo)
        }
    }

    private fun initClickListeners() {
        binding.btnNext.setDebounceClickListener {
            lendingViewModel.viewOnlyNavigationRedirectTo(
                flowType = LendingFlowType.LOAN_APPLICATION,
                isGoToNextStepFlow = true,
                contextRef = WeakReference(requireActivity()),
                currentDestination = R.id.lendingSummaryViewOnlyFragment,
                fromFlow = args.flowType
            )
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
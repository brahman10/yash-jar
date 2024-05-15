package com.jar.app.feature_lending.impl.ui.automate_emi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.getEmiAmount
import com.jar.app.base.util.getMaskedString
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentAutomateEmiBinding
import com.jar.app.feature_lending.shared.domain.model.temp.LoanApplicationDetail
import com.jar.app.feature_lending.impl.ui.bank.enter_account.BankCheckListAdapter
import com.jar.app.feature_lending.impl.ui.common.LendingViewModel
import com.jar.app.feature_lending.shared.domain.LendingEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.lang.ref.WeakReference
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR

@AndroidEntryPoint
internal class AutomateEmiFragment : BaseFragment<FragmentAutomateEmiBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    private val args by navArgs<AutomateEmiFragmentArgs>()

    private val lendingViewModel by activityViewModels<LendingViewModel> { defaultViewModelProviderFactory }

    private var adapter: BankCheckListAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAutomateEmiBinding
        get() = FragmentAutomateEmiBinding::inflate

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
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        getData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        setData(lendingViewModel.getLoanApplication()?.details)

        adapter = BankCheckListAdapter()
        binding.rvStaticInfo.adapter = adapter
    }

    private fun setData(loanApplicationDetails: LoanApplicationDetail?) {
        val drawDown = loanApplicationDetails?.DRAW_DOWN
        val loanAmount = drawDown?.amount
        val tenure = drawDown?.tenure
        val roi = drawDown?.roi
        val monthlyEmiAmount = getEmiAmount(roi?.toDouble()!!, tenure!!, loanAmount?.toDouble()!!)
        val totalRepaymentAmount = monthlyEmiAmount * tenure

        binding.dashedCircleView.setDashCount(tenure)
        binding.tvDuration.text = getCustomStringFormatted(
            MR.strings.feature_lending_month_prefix, tenure)
        binding.tvEmiAmount.text =
            getCustomStringFormatted(
                MR.strings.feature_lending_per_month_prefix, monthlyEmiAmount)
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val date = Instant.ofEpochMilli(lendingViewModel.getFirstEmiDate(drawDown.createdAtEpoch))
            .atZone(ZoneId.systemDefault())
        binding.tvEmiDate.text = date.format(formatter)
        binding.tvRepaymentAmount.text =
            getCustomStringFormatted(
                MR.strings.feature_lending_rupee_prefix_float, totalRepaymentAmount)

        loanApplicationDetails.BANK_ACCOUNT_DETAILS?.let {
            binding.tvBankName.text = it.bankName
            binding.tvAccountNumber.text = it.accountNumber?.getMaskedString(0, 5)
            Glide.with(this@AutomateEmiFragment)
                .load(it.icon)
                .into(binding.ivBankLogo)
        }
    }

    private fun setupListeners() {
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
                LendingEventKey.OnClick_ReadyCash_AutomateEMI,
                mapOf(
                    LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                    LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                    LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                    LendingEventKey.entryPoint to args.flowType,
                    LendingEventKey.address to address?.address.orEmpty(),
                    LendingEventKey.emiTenure to drawdown?.tenure.orZero(),
                    LendingEventKey.emiAmount to drawdown?.amount.orZero(),
                    LendingEventKey.accountHolderName to bankDetails?.accountHolderName.orEmpty(),
                    LendingEventKey.ifscCode to bankDetails?.ifsc.orEmpty()
                )
            )
//            navigateTo(
//                AutomateEmiFragmentDirections.actionAutomateEmiFragmentToMandateStatusFragment(
//                    flowType = args.flowType
//                )
//            )
        }
    }

    private fun getData() {
        uiScope.launch(dispatcherProvider.io) {
            val checkPointList = listOf(
                getCustomString(MR.strings.feature_lending_automate_timely_payment),
                getCustomString(MR.strings.feature_lending_automate_avoid_late_fee),
                getCustomString(MR.strings.feature_lending_automate_easiest_way_to_improve_credit)
            )
            adapter?.submitList(checkPointList)
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
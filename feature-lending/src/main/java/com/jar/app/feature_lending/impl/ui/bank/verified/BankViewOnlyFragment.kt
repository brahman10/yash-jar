package com.jar.app.feature_lending.impl.ui.bank.verified

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.databinding.FragmentBankDetailsViewOnlyBinding
import com.jar.app.feature_lending.impl.domain.event.UpdateLendingStepsToolbarEvent
import com.jar.app.feature_lending.shared.domain.model.v2.BankAccount
import com.jar.app.feature_lending.shared.domain.model.v2.PreApprovedData
import com.jar.app.feature_lending.impl.ui.bank.enter_account.BankCheckListAdapter
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class BankViewOnlyFragment : BaseFragment<FragmentBankDetailsViewOnlyBinding>() {
    private val parentViewModelProvider by activityViewModels<LendingHostViewModelAndroid> { defaultViewModelProviderFactory }
    private val parentViewModel by lazy {
        parentViewModelProvider.getInstance()
    }

    private var adapter: BankCheckListAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBankDetailsViewOnlyBinding
        get() = FragmentBankDetailsViewOnlyBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateLendingStepsToolbarEvent(shouldShowSteps = true, LendingStep.BANK_DETAILS)
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        setClickListener()
    }

    private fun setupUI() {
        adapter = BankCheckListAdapter()
        binding.rvStaticInfo.adapter = adapter

        parentViewModel.preApprovedData?.let {
            setLogo(it)
        } ?: kotlin.run {
            parentViewModel.fetchPreApprovedData()
        }

        parentViewModel.staticContent?.bankContent?.let {
            setCheckList(it)
        } ?: kotlin.run {
            parentViewModel.fetchStaticContent(LendingConstants.StaticContentType.BANK_SCREEN)
        }

        parentViewModel.loanDetailsFlow.asLiveData().value?.data?.data?.applicationDetails?.bankAccount?.let {
            setBankData(it)
        } ?: kotlin.run {
            parentViewModel.fetchLoanDetails(LendingConstants.LendingApplicationCheckpoints.BANK_ACCOUNT_DETAILS)
        }
    }

    private fun setLogo(preApprovedData: PreApprovedData) {
        preApprovedData.creditProviderLogoUrl?.let {
            Glide.with(requireContext()).load(it).into(binding.ivCreditProvider)
        }

        preApprovedData.npciLogoUrl?.let {
            Glide.with(requireContext()).load(it).into(binding.ivNpci)
        }
    }

    private fun setCheckList(list: List<String>) {
        adapter?.submitList(list)
    }

    private fun setBankData(bankAccount: BankAccount) {
        binding.clBankDetails.isVisible = true
        Glide.with(requireContext()).load(bankAccount.bankLogo).into(binding.ivBankLogo)
        binding.tvBankName.text = bankAccount.bankName
        binding.tvAccountNumber.text = bankAccount.accountNumber
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.staticContentFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.bankContent?.let {
                            setCheckList(it)
                        }
                    },
                    onError = {errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.preApprovedDataFlow.collect(
                    onSuccess = {
                        it?.let {
                            setLogo(it)
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.loanDetailsFlow.collect(
                    onSuccess = {
                        it?.applicationDetails?.bankAccount?.let {
                            setBankData(it)
                        }
                    }
                )
            }
        }
    }

    private fun setClickListener() {
        binding.btnAction.setDebounceClickListener {

        }
    }
}
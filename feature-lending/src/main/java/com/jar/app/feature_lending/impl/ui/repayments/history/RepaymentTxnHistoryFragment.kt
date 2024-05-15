package com.jar.app.feature_lending.impl.ui.repayments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_lending.databinding.FragmentRepaymentTxnHistoryBinding
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class RepaymentTxnHistoryFragment : BaseFragment<FragmentRepaymentTxnHistoryBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    private val viewModelProvider by viewModels<RepaymentTxnHistoryViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private var txnAdapter: RepaymentTxnAdapter? = null
    private var spaceItemDecoration = SpaceItemDecoration(12.dp, 6.dp)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRepaymentTxnHistoryBinding
        get() = FragmentRepaymentTxnHistoryBinding::inflate

    override fun setupAppBar() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
        analyticsApi.postEvent(
            event = LendingEventKeyV2.Repay_EMIDetailsScreenLaunched,
            values = mapOf(LendingEventKeyV2.screen_name to LendingEventKeyV2.Transactions_Screen)
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
    }

    private fun setupUI() {
        txnAdapter = RepaymentTxnAdapter {
            navigateTo(
                "android-app://com.jar.app/repaymentTxnDetailFragment/${arguments?.getString(ARGS_LOAN_ID).orEmpty()}/${it.paymentId}",
            )
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Repay_EMITransactionsClicked,
                values = mapOf(LendingEventKeyV2.button_type to LendingEventKeyV2.View_Details)
            )
        }
        binding.rvTxnHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTxnHistory.adapter = txnAdapter
        binding.rvTxnHistory.addItemDecorationIfNoneAdded(spaceItemDecoration)
    }

    private fun setupListeners() {
        binding.llContactSupport.setDebounceClickListener {
            val prefillString = getCustomStringFormatted(
                MR.strings.feature_lending_emi_txn_contact_us,
                prefs.getUserName().orEmpty(),
                prefs.getUserPhoneNumber().orEmpty()
            )
            requireContext().openWhatsapp(remoteConfigManager.getWhatsappNumber(), prefillString)
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Repay_EMITransactionsClicked,
                values = mapOf(LendingEventKeyV2.button_type to LendingEventKeyV2.Contact_Support)
            )
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.staticContentFlow.collect(
                    onSuccess = {
                        it?.repaymentTransactionContent?.let { string ->
                            binding.tvDesc.text =
                                HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.transactionListFlow.collect(
                    onLoading = {
                        binding.dataGroup.isVisible = false
                        binding.shimmerPlaceholder.isVisible = true
                        binding.shimmerPlaceholder.startShimmer()
                    },
                    onSuccess = {
                        uiScope.launch {
                            delay(500) // Done intentionally, Recyclerview has some issues when used inside ViewPager
                            binding.dataGroup.isVisible = true
                            binding.shimmerPlaceholder.isVisible = false
                            binding.shimmerPlaceholder.stopShimmer()
                            if (it.isNullOrEmpty()) {
                                binding.rvTxnHistory.isVisible = false
                                binding.tvEmpty.isVisible = true
                            } else {
                                binding.rvTxnHistory.isVisible = true
                                binding.tvEmpty.isVisible = false
                                txnAdapter?.submitList(it)
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        binding.dataGroup.isVisible = true
                        binding.shimmerPlaceholder.isVisible = false
                        binding.shimmerPlaceholder.stopShimmer()
                    }
                )
            }
        }
    }

    private fun getData() {
        val loanId = arguments?.getString(ARGS_LOAN_ID).orEmpty()
        viewModel.fetchStaticContent(loanId)
        viewModel.fetchTransactionList(loanId)
    }

    companion object {
        private const val ARGS_LOAN_ID = "loan_id"

        @JvmStatic
        fun newInstance(loanId: String) = RepaymentTxnHistoryFragment().apply {
            arguments = Bundle().apply {
                putString(ARGS_LOAN_ID, loanId)
            }
        }
    }
}
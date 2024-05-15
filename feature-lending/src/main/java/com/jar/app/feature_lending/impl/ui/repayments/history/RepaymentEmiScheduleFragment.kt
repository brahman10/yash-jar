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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.feature_lending.databinding.FragmentRepaymentEmiHistoryBinding
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class RepaymentEmiScheduleFragment : BaseFragment<FragmentRepaymentEmiHistoryBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val viewModelProvider by viewModels<RepaymentEmiScheduleViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private var scheduledAdapter: EmiScheduledAdapter? = null
    private var dividerDecorator: DividerItemDecoration? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRepaymentEmiHistoryBinding
        get() = FragmentRepaymentEmiHistoryBinding::inflate

    override fun setupAppBar() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            event = LendingEventKeyV2.Repay_EMIDetailsScreenLaunched,
            values = mapOf(LendingEventKeyV2.screen_name to LendingEventKeyV2.Emi_Details_Screen)
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeFlow()
        getData()
    }

    private fun setupUI() {
        scheduledAdapter = EmiScheduledAdapter()
        dividerDecorator = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.rvEmiSchedule.addItemDecorationIfNoneAdded(dividerDecorator!!)
        binding.rvEmiSchedule.adapter = scheduledAdapter
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.staticContentFlow.collect(
                    onSuccess = {
                        it?.repaymentEmiContent?.let { string ->
                            binding.tvDesc.text =
                                HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.emiListFlow.collect(
                    onLoading = {
                        binding.rootLayout.isVisible = false
                        binding.shimmerPlaceholder.isVisible = true
                        binding.shimmerPlaceholder.startShimmer()
                    },
                    onSuccess = {
                        binding.rootLayout.isVisible = true
                        binding.shimmerPlaceholder.isVisible = false
                        binding.shimmerPlaceholder.stopShimmer()
                        scheduledAdapter?.submitList(it)
                    },
                    onError = { errorMessage, _ ->
                        binding.rootLayout.isVisible = true
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
        viewModel.fetchEmiList(loanId)
    }

    companion object {
        private const val ARGS_LOAN_ID = "loan_id"

        @JvmStatic
        fun newInstance(loanId: String) = RepaymentEmiScheduleFragment().apply {
            arguments = Bundle().apply {
                putString(ARGS_LOAN_ID, loanId)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scheduledAdapter = null
    }
}
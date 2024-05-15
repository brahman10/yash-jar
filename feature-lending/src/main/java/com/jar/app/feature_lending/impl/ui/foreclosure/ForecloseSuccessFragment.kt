package com.jar.app.feature_lending.impl.ui.foreclosure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.copyToClipboard
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.databinding.FragmentForecloseSucessBinding
import com.jar.app.feature_lending.impl.ui.common.KeyValueAdapter
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.v2.ForeCloseTxnData
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class ForecloseSuccessFragment : BaseFragment<FragmentForecloseSucessBinding>() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val args by navArgs<ForecloseSuccessFragmentArgs>()

    private val txnData by lazy {
        serializer.decodeFromString<ForeCloseTxnData>(decodeUrl(args.txnData))
    }


    private var adapter: KeyValueAdapter? = null

    private val viewModelProvider: ForeclosureSuccessViewModelAndroid by viewModels { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentForecloseSucessBinding
        get() = FragmentForecloseSucessBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            event = LendingEventKeyV2.Lending_ForeclosureSuccessfulScreenShown,
            values = mapOf(LendingEventKeyV2.source to args.flowType)
        )
    }

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
        getData()
    }

    private fun setupUI() {
        setTransactionDetails(txnData)
        adapter = KeyValueAdapter()
        binding.rvForeCloseBreakdown.adapter = adapter

        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            LendingConstants.LottieUrls.TICK_WITH_CELEBRATION
        )
    }

    private fun setupListeners() {
        binding.tvTxnId.setDebounceClickListener {
            requireContext().copyToClipboard(
                binding.tvTxnId.text.toString(),
                getString(com.jar.app.core_ui.R.string.copied)
            )
        }

        binding.btnAction.setDebounceClickListener {
            analyticsApi.postEvent(LendingEventKeyV2.Lending_ForeclosureSuccessfulScreenClicked)
            EventBus.getDefault().post(GoToHomeEvent("FORCLOSURE_SUCCESS"))
        }
    }

    private fun setTransactionDetails(txnData: ForeCloseTxnData) {
        binding.tvTxnId.text = txnData.txnId
        binding.tvPaidOn.text = txnData.txnDate
        binding.tvPaidUsing.text =
            getCustomStringFormatted(MR.strings.feature_lending_paid_using, txnData.paidUsing)
        binding.tvPaidDetails.text = txnData.paidUsingDetail
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loanDetailsFlow.collect(
                    onSuccess = {
                        it?.applicationDetails?.foreclosure?.let {
                            adapter?.submitList(it.details)
                            binding.tvTotalAmount.text = getCustomStringFormatted(
                                MR.strings.feature_lending_rupee_prefix_string,
                                it.totalAmount?.getFormattedAmount().orEmpty()
                            )
                        }
                    }
                )
            }
        }
    }

    private fun getData() {
        viewModel.fetchLoanDetails(
            LendingConstants.LendingApplicationCheckpoints.FORECLOSURE,
            loanId = args.loanId
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }
}
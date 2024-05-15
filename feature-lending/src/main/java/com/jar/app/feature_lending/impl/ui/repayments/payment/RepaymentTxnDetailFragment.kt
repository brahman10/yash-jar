package com.jar.app.feature_lending.impl.ui.repayments.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.copyToClipboard
import com.jar.app.base.util.dp
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentPaymentDetailBinding
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentTransactionDetails
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentTxnStatus
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class RepaymentTxnDetailFragment : BaseFragment<FragmentPaymentDetailBinding>() {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val viewModelProvider by viewModels<RepaymentTxnDetailViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private val args by navArgs<RepaymentTxnDetailFragmentArgs>()

    private var adapter: RepaymentTransactionCardAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPaymentDetailBinding
        get() = FragmentPaymentDetailBinding::inflate

    override fun setupAppBar() {
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
        getData()
    }

    private fun setupUI() {
        adapter = RepaymentTransactionCardAdapter()
        binding.rvBreakdown.adapter = adapter
    }

    private fun setupListeners() {
        binding.ivBack.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Repay_BackButtonClicked,
                values = mapOf(LendingEventKeyV2.screen_name to LendingEventKeyV2.Payment_Status_Screen)
            )
            popBackStack()
        }

        binding.ivCopy.setDebounceClickListener {
            requireContext().copyToClipboard(binding.tvTxnId.text?.toString().orEmpty(), toastMessage = getString(com.jar.app.core_ui.R.string.copied))
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Repay_EMITransactionDetailsScreenClicked,
                values = mapOf(
                    LendingEventKeyV2.action to LendingEventKeyV2.Transactions_ID_copied,
                    LendingEventKeyV2.Transactions_Id to args.txnId
                )
            )
        }

        binding.llContactSupport.setDebounceClickListener {
            val prefillString = getCustomStringFormatted(MR.strings.feature_lending_txn_contact_us, args.txnId, prefs.getUserName().orEmpty(), prefs.getUserPhoneNumber().orEmpty())
            requireContext().openWhatsapp(remoteConfigManager.getWhatsappNumber(), prefillString)
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Repay_EMITransactionDetailsScreenClicked,
                values = mapOf(
                    LendingEventKeyV2.action to LendingEventKeyV2.Contact_Support,
                    LendingEventKeyV2.Transactions_Id to args.txnId
                )
            )
        }
    }

    private fun setData(data: RepaymentTransactionDetails) {
        analyticsApi.postEvent(
            event = LendingEventKeyV2.Repay_EMITransactionDetailsScreenLaunched,
            values = mapOf(
                LendingEventKeyV2.Transactions_Id to args.txnId,
                LendingEventKeyV2.payment_status to data.paymentStatusText.orEmpty(),
                LendingEventKeyV2.txn_status to data.transactionStatus.orEmpty(),
                LendingEventKeyV2.emi_count to data.paymentBreakdown?.size.orZero(),
            )
        )

        val statusBgRes: Int
        val statusTextColor: Int
        val statusIconRes: Int
        when (data.transactionStatus) {
            RepaymentTxnStatus.SUCCESS.name -> {
                statusIconRes = com.jar.app.core_ui.R.drawable.core_ui_ic_green_tick
                statusBgRes = (com.jar.app.core_ui.R.drawable.core_ui_round_1a1ea787_bg_4dp)
                statusTextColor = (ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_58DDC8))
            }
            RepaymentTxnStatus.FAILURE.name -> {
                statusIconRes = com.jar.app.core_ui.R.drawable.core_ui_ic_red_cross
                statusBgRes = (com.jar.app.core_ui.R.drawable.core_ui_round_1aeb6a6e_bg_4dp)
                statusTextColor = (ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_EB6A6E))
            }
            else -> {
                statusIconRes = R.drawable.feature_lending_ic_clock
                statusBgRes = (com.jar.app.core_ui.R.drawable.core_ui_round_1aebb46a_bg_4dp)
                statusTextColor = (ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_EBB46A))

                binding.ivGreenTick.updateLayoutParams {
                    width = 16.dp
                    height = 16.dp
                }
                binding.tvDate.isVisible = false
                binding.tvStatusDesc.setTextColor(ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_789BDE))
            }
        }

        statusBgRes.let { binding.tvStatus.setBackgroundResource(it) }
        statusTextColor.let { binding.tvStatus.setTextColor(it) }
        statusIconRes.let { binding.ivGreenTick.setImageResource(it) }

        binding.tvDate.text = data.paymentDate
        binding.tvStatusDesc.text = data.transactionStatusText
        binding.tvPaymentTitle.text = data.transactionHeader

        binding.tvStatus.text = data.paymentStatusText
        binding.tvAmount.text = getCustomStringFormatted(MR.strings.feature_lending_rupee_prefix_string, data.emiAmount?.toFloatOrNull()?.toInt()?.getFormattedAmount().orEmpty())

        data.paymentMode?.let {
            binding.tvPaymentMode.text = getCustomStringFormatted(MR.strings.feature_lending_paid_via, it)
        } ?: kotlin.run {
            binding.tvPaymentMode.isVisible = false
        }

        data.transactionId?.let {
            binding.clTransaction.isVisible = true
            binding.tvTxnId.text = it
        } ?: kotlin.run {
            binding.clTransaction.isVisible = false
        }

        data.emiLottie?.let {
            Glide.with(requireContext()).load(it).into(binding.ivMoneyBag)
        }

        if (data.paymentBreakdown.isNullOrEmpty()) {
            binding.rvBreakdown.isVisible = false
        } else {
            binding.rvBreakdown.isVisible = true
            adapter?.submitList(data.paymentBreakdown)
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.transactionDetailFlow.collect(
                    onLoading = {
                        binding.rootLayout.isVisible = false
                        binding.shimmerPlaceholder.isVisible = true
                        binding.shimmerPlaceholder.startShimmer()
                    },
                    onSuccess = {
                        binding.rootLayout.isVisible = true
                        binding.shimmerPlaceholder.isVisible = false
                        binding.shimmerPlaceholder.stopShimmer()
                        it?.let {
                            setData(it)
                        }
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
        viewModel.fetchTransactionDetail(args.txnId)
    }
}
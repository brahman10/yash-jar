package com.jar.app.feature.transaction.ui.transaction_breakup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.R
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.databinding.FragmentPaymentTransactionBreakupBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class PaymentTransactionBreakupFragment :
    BaseBottomSheetDialogFragment<FragmentPaymentTransactionBreakupBinding>() {

    private val args by navArgs<PaymentTransactionBreakupFragmentArgs>()

    private val adapter = PaymentTransactionBreakupAdapter()

    private val spaceItemDecoration = SpaceItemDecoration(16.dp, 4.dp)

    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()

    private val viewModel by viewModels<PaymentTransactionBreakupFragmentViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPaymentTransactionBreakupBinding
        get() = FragmentPaymentTransactionBreakupBinding::inflate

    override val bottomSheetConfig = DEFAULT_CONFIG

    override fun setup() {
        if (args.orderId.isNullOrBlank() && args.type.isNullOrBlank()) {
            getString(R.string.some_error_occurred).snackBar(binding.root)
            dismissAllowingStateLoss()
            Timber.e("Both orderId & type args cannot be null")
        }
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupUI() {
        binding.tvTitle.text = args.title
        binding.tvDescription.text = args.description

        binding.rvTransactions.layoutManager = LinearLayoutManager(context)
        binding.rvTransactions.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvTransactions.adapter = adapter
        binding.rvTransactions.edgeEffectFactory = baseEdgeEffectFactory
    }

    private fun setupListeners() {
        binding.btnClose.setDebounceClickListener {
            dismiss()
        }
    }

    private fun observeLiveData() {
        viewModel.paymentTransactionBreakupLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                binding.shimmerPlaceholder.stopShimmer()
                binding.shimmerPlaceholder.isVisible = false
                binding.rvTransactions.isVisible = true
                adapter.submitList(it.transactions)
                expandBottomSheet()
            }
        )
    }

    private fun getData() {
        viewModel.fetchPaymentTransactionBreakup(args.orderId, args.type)
    }
}
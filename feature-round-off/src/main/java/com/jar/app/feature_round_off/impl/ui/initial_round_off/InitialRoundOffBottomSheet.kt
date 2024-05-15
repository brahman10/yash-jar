package com.jar.app.feature_round_off.impl.ui.initial_round_off

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_round_off.databinding.FeatureRoundOffInitialRoundOffBottomSheetBinding
import com.jar.app.feature_round_off.shared.util.RoundOffEventKey
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class InitialRoundOffBottomSheet :
    BaseBottomSheetDialogFragment<FeatureRoundOffInitialRoundOffBottomSheetBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffInitialRoundOffBottomSheetBinding
        get() = FeatureRoundOffInitialRoundOffBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args: InitialRoundOffBottomSheetArgs by navArgs()
    private val viewModel: InitialRoundOffViewModel by viewModels()
    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 8.dp)
    private var adapter: InitialRoundOffAdapter? = null

    override fun setup() {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
        viewModel.fetchPaymentTransactionBreakup(args.orderId, null)
        binding.rvRoundOffsDetected.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRoundOffsDetected.addItemDecorationIfNoneAdded(spaceItemDecoration)
        adapter = InitialRoundOffAdapter()
        binding.rvRoundOffsDetected.adapter = adapter
        analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Roundoff_Detection_Screen,
            mapOf(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Hide_Transaction,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.RoundoffAmount to args.roundOffAmount,
            )
        )
    }

    private fun setupListener() {
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
                binding.rvRoundOffsDetected.isVisible = true
                adapter?.submitList(it.transactions)
                expandBottomSheet()
            }
        )
    }
}
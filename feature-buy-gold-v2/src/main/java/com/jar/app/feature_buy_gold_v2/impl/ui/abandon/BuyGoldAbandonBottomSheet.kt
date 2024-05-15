package com.jar.app.feature_buy_gold_v2.impl.ui.abandon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.FeatureBuyGoldBottomSheetAbandonBinding
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldAbandonScreen
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2Constants
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class BuyGoldAbandonBottomSheet :
    BaseBottomSheetDialogFragment<FeatureBuyGoldBottomSheetAbandonBinding>() {

    private val viewModelProvider by viewModels<BuyGoldAbandonBottomSheetViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var adapter: BuyGoldAbandonAdapter? = null

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 6.dp)

    private val args: BuyGoldAbandonBottomSheetArgs by navArgs()
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureBuyGoldBottomSheetAbandonBinding
        get() = FeatureBuyGoldBottomSheetAbandonBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isCancellable = true,
            isDraggable = true,
        )

    override fun setup() {
        viewModel.fetchAbandonData()
        viewModel.sendAnalyticsEvent(BuyGoldV2EventKey.BuyGold_AbandonScreenShown, null)
        observeFlow()
        setupListeners()
    }


    fun setupListeners() {
        binding.btnContinue.setDebounceClickListener {
            viewModel.sendAnalyticsEvent(
                BuyGoldV2EventKey.BuyGold_AbandonScreenClicked,
                mapOf(BuyGoldV2EventKey.Click_action to binding.btnContinue.getText())
            )
            dismiss()
        }
        binding.btnLater.setDebounceClickListener {
            viewModel.sendAnalyticsEvent(
                BuyGoldV2EventKey.BuyGold_AbandonScreenClicked,
                mapOf(BuyGoldV2EventKey.Click_action to binding.btnLater.getText())
            )

            findNavController().getBackStackEntry(R.id.buyGoldV2Fragment).savedStateHandle[BuyGoldV2Constants.EXIT_BUY_GOLD_FLOW] =
                true
            dismiss()
        }
    }

    private fun setData(data: BuyGoldAbandonScreen) {
        binding.tvHeading.text = data.header
        data.footerButton1?.let { binding.btnContinue.setText(it) }
        data.footerButton2?.let { binding.btnLater.setText(it) }
        data.footerText?.let { binding.tvFooter.text = it }
        data.profilePics?.let {
            binding.overlappingView.submitProfilePics(it)
        }
        adapter = BuyGoldAbandonAdapter()
        binding.rvPoints.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPoints.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvPoints.adapter = adapter
        adapter?.submitList(data.stepsList)
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.abandonStepsFlow.collect(
                    onLoading = {
                        binding.shimmerPlaceholder.isVisible = true
                        binding.clContainer.isVisible = false
                        binding.shimmerPlaceholder.startShimmer()
                    },
                    onSuccess = {
                        binding.shimmerPlaceholder.isVisible = false
                        binding.clContainer.isVisible = true
                        binding.shimmerPlaceholder.stopShimmer()
                        setData(it.buyGoldAbandonScreen)
                    },
                    onError = { errorMessage, errorCode ->
                        binding.shimmerPlaceholder.isVisible = false
                        binding.clContainer.isVisible = true
                        binding.shimmerPlaceholder.stopShimmer()
                    }

                )
            }
        }
    }

}
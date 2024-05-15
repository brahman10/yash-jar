package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.abandonScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.isPresentInBackStack
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_daily_investment.shared.domain.model.DailySavingAbandonScreen
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentAbandonScreenBottomSheetBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.AbandonScreen
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class DailySavingsV2AbandonBottomSheet :
    BaseBottomSheetDialogFragment<FeatureDailyInvestmentAbandonScreenBottomSheetBinding>() {

    private val viewModel by viewModels<DailySavingsV2AbandonViewModel> { defaultViewModelProviderFactory }

    private var adapter: DailySavingsV2AbandonAdapter? = null

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 6.dp)

    private val args: DailySavingsV2AbandonBottomSheetArgs by navArgs()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentAbandonScreenBottomSheetBinding
        get() = FeatureDailyInvestmentAbandonScreenBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isCancellable = true,
            isDraggable = true,
        )

    override fun setup() {
        if (args.isOnboardingFlow) {
            viewModel.fetchBottomSheetData(BaseConstants.StaticContentType.DAILY_SAVINGS_ABANDON_SCREEN_V2.name)
        } else {
            viewModel.fetchBottomSheetData(BaseConstants.StaticContentType.DAILY_SAVINGS_ABANDON_SCREEN.name)
        }
        analyticsHandler.postEvent(
            DailySavingsEventKey.Shown_DailySavings_Card,
            mapOf(DailySavingsEventKey.PageName to AbandonScreen)
        )
        observeData()
        setupListeners()
    }


    fun setupListeners() {
        binding.btnContinue.setDebounceClickListener {
            analyticsHandler(binding.btnContinue.getText())
            dismiss()
        }
        binding.btnLater.setDebounceClickListener {
            analyticsHandler(binding.btnLater.getText())
            if (findNavController().isPresentInBackStack(args.backstackId))
                findNavController().getBackStackEntry(args.backstackId)
                    .savedStateHandle[DailySavingConstants.EXIT_DAILY_SAVING_AMOUNT_SELECTION_FLOW] =
                    true
            dismiss()
        }
    }

    private fun setData(data: DailySavingAbandonScreen) {
        binding.tvHeading.text = data.header
        data.footerButton1?.let { binding.btnContinue.setText(it) }
        data.footerButton2?.let { binding.btnLater.setText(it) }
        data.footerText?.let { binding.tvFooter.text = it }
        data.profilePics?.let {
            binding.overlappingView.submitProfilePics(it)
        }
        adapter = DailySavingsV2AbandonAdapter()
        binding.rvPoints.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPoints.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvPoints.adapter = adapter
        adapter?.submitList(data.stepsList)
    }

    private fun observeData() {
        viewModel.bottomSheetLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                binding.shimmerPlaceholder.isVisible = true
                binding.clContainer.isVisible = false
                binding.shimmerPlaceholder.startShimmer()
            },
            onSuccess = {
                binding.shimmerPlaceholder.isVisible = false
                binding.clContainer.isVisible = true
                binding.shimmerPlaceholder.stopShimmer()
                setData(it.dailySavingAbandonScreen)
            },
            onError = {
                binding.shimmerPlaceholder.isVisible = false
                binding.clContainer.isVisible = true
                binding.shimmerPlaceholder.stopShimmer()
            }

        )
    }

    fun analyticsHandler(btnClicked: String) {
        analyticsHandler.postEvent(
            DailySavingsEventKey.Clicked_DailySavings_Card,
            mapOf(
                DailySavingsEventKey.PageName to "Abandon screen",
                DailySavingsEventKey.ButtonType to btnClicked
            ),
        )
    }

}
package com.jar.app.feature_round_off.impl.ui.pause

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.pause_savings.PauseSavingsAdapter
import com.jar.app.core_ui.util.observeNetworkResponseUnwrapped
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffPauseBottomSheetBinding
import com.jar.app.feature_round_off.shared.MR
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.moko.resources.StringResource
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class PauseRoundOffBottomSheet :
    BaseBottomSheetDialogFragment<FeatureRoundOffPauseBottomSheetBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffPauseBottomSheetBinding
        get() = FeatureRoundOffPauseBottomSheetBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    @Inject
    lateinit var coreUiApi: CoreUiApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var adapter: PauseSavingsAdapter? = null
    private val viewModel: PauseRoundOffViewModel by viewModels()
    private val args: PauseRoundOffBottomSheetArgs by navArgs()
    private val spaceItemDecoration = SpaceItemDecoration(4.dp, 0.dp)

    override fun setup() {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
        viewModel.fetchPauseOptions()
        binding.rvPauseDuration.isVisible = true
        binding.rvPauseDuration.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        adapter = PauseSavingsAdapter { pauseRoundOffOption, position ->
            updatePauseListOnClick(position)
        }
        binding.rvPauseDuration.adapter = adapter
        binding.rvPauseDuration.addItemDecorationIfNoneAdded(spaceItemDecoration)
        analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_PauseScreen_RoundoffSettings,
            mapOf(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.PaymentType to args.paymentType
            )
        )
    }

    private fun setupListener() {
        binding.btnPause.setDebounceClickListener {
            viewModel.pauseRoundOffs()
        }

        binding.btnCancel.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_PauseScreen_RoundoffSettings,
                mapOf(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Cancel,
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.PaymentType to args.paymentType
                )
            )
            dismiss()
        }

        binding.ivCross.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_PauseScreen_RoundoffSettings,
                mapOf(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.CrossClicked,
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.PaymentType to args.paymentType
                )
            )
            dismiss()
        }
    }

    private fun observeLiveData() {
        viewModel.pauseOptionsLiveData.observeNetworkResponseUnwrapped(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                binding.btnPause.setDisabled(viewModel.pauseSavingOptionWrapper == null)
                adapter?.submitList(it)
            },
            onError = { _, _ ->
                dismissProgressBar()
            }
        )

        viewModel.roundOffsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                analyticsHandler.postEvent(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_PauseScreen_RoundoffSettings,
                    mapOf(
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Pause,
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.PaymentType to args.paymentType,
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.PauseDuration to viewModel.pauseSavingOptionWrapper?.pauseSavingOption?.timeValue.orZero()
                            .toString() + viewModel.pauseSavingOptionWrapper?.pauseSavingOption?.durationType?.name.orEmpty(),
                    )
                )
                val durationRes =
                    viewModel.pauseSavingOptionWrapper?.pauseSavingOption?.durationType?.durationRes
                EventBus.getDefault()
                    .post(com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent())
                val pauseTimeText =
                    viewModel.pauseSavingOptionWrapper?.pauseSavingOption?.timeValue.toString() + " " + if (durationRes != null) getCustomString(
                        StringResource(durationRes.resourceId)
                    ) else ""
                coreUiApi.openGenericPostActionStatusFragment(
                    GenericPostActionStatusData(
                        postActionStatus = PostActionStatus.DISABLED.name,
                        header = getCustomString(MR.strings.feature_round_off_paused),
                        title = getCustomStringFormatted(
                            MR.strings.feature_round_off_we_will_not_detect_your_spend_for_next_s,
                            pauseTimeText
                        ),
                        imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_pause,
                        headerTextSize = 20f,
                        titleTextSize = 12f,
                        titleColorRes = com.jar.app.core_ui.R.color.color_ACA1D3
                    )
                ) {
                    analyticsHandler.postEvent(
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_StopConfirmation_RoundoffSettings,
                        mapOf(
                            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Status to "Paused",
                            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.PaymentType to args.paymentType
                        )
                    )
                    findNavController().getBackStackEntry(R.id.roundOffDetailsFragment).savedStateHandle[com.jar.app.feature_round_off.shared.util.RoundOffConstants.PAUSE_ROUND_OFFS] =
                        true
                    dismiss()
                }
            },
            onError = { dismissProgressBar() },
        )
    }

    private fun updatePauseListOnClick(position: Int) {
        adapter?.currentList?.let {
            viewModel.updatePauseOptionListOnItemClick(it, position)
        }
    }

}
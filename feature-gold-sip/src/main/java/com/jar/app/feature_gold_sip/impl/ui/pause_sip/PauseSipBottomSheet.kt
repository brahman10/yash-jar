package com.jar.app.feature_gold_sip.impl.ui.pause_sip

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
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.pause_savings.PauseSavingsAdapter
import com.jar.app.core_ui.util.observeNetworkResponseUnwrapped
import com.jar.app.feature_gold_sip.R
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipBottomSheetPauseSipBinding
import com.jar.app.feature_gold_sip.shared.GoldSipMR
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class PauseSipBottomSheet :
    BaseBottomSheetDialogFragment<FeatureGoldSipBottomSheetPauseSipBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGoldSipBottomSheetPauseSipBinding
        get() = FeatureGoldSipBottomSheetPauseSipBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    @Inject
    lateinit var coreUiApi: CoreUiApi

    private var adapter: PauseSavingsAdapter? = null

    private val viewModelProvider by viewModels<PauseSipViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    private val args: PauseSipBottomSheetArgs by navArgs()
    private val spaceItemDecoration = SpaceItemDecoration(4.dp, 0.dp)

    override fun setup() {
        setupUI()
        setupListener()
        observeFlow()
    }

    private fun setupUI() {
        viewModel.fetchPauseOptions(args.sipSubscriptionType)
        when (args.sipSubscriptionType) {
            com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> {
                binding.tvPauseGoldSipFor.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_pause_gold_sip_for_next)
                binding.tvWeWillNotBeDebitingYourSip.isVisible = false
                binding.rvPauseDuration.isVisible = true
                binding.rvPauseDuration.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                adapter = PauseSavingsAdapter { pauseGoldSipOption, position ->
                    updatePauseListOnClick(position)
                }
                binding.rvPauseDuration.adapter = adapter
                binding.rvPauseDuration.addItemDecorationIfNoneAdded(spaceItemDecoration)
            }

            com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> {
                binding.tvPauseGoldSipFor.text =
                    getCustomString(GoldSipMR.strings.feature_gold_sip_pause_sip_for_next_1_month)
                binding.tvWeWillNotBeDebitingYourSip.isVisible = true
                binding.rvPauseDuration.isVisible = false
            }
        }
        viewModel.firePauseSipEvent(
            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_PauseSIPBottomSheet,
            mapOf(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                    args.sipSubscriptionType.textRes
                )
            )
        )
    }

    private fun setupListener() {
        binding.btnPause.setDebounceClickListener {
            viewModel.firePauseSipEvent(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_PauseSIPBottomSheet,
                mapOf(
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Pause,
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                        args.sipSubscriptionType.textRes
                    )
                )
            )
            viewModel.pauseSavingOptionWrapper?.let {
                viewModel.pauseSip(
                    shouldPause = true,
                    pauseType = SavingsType.GOLD_SIPS.name,
                    pauseDuration = it.pauseSavingOption.name
                )
            }
        }

        binding.btnCancel.setDebounceClickListener {
            viewModel.firePauseSipEvent(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_PauseSIPBottomSheet,
                mapOf(
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Cancel,
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                        args.sipSubscriptionType.textRes
                    )
                )
            )
            dismiss()
        }

        binding.ivCross.setDebounceClickListener {
            viewModel.firePauseSipEvent(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_PauseSIPBottomSheet,
                mapOf(
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Cross,
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                        args.sipSubscriptionType.textRes
                    )
                )
            )
            dismiss()
        }
    }

    private fun observeFlow() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.pauseOptionsFlow.collectUnwrapped(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        binding.btnPause.setDisabled(viewModel.pauseSavingOptionWrapper == null)
                        adapter?.submitList(it)
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.sipPausedFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        val durationRes =
                            viewModel.pauseSavingOptionWrapper?.pauseSavingOption?.durationType?.durationRes
                        val pauseTimeText =
                            viewModel.pauseSavingOptionWrapper?.pauseSavingOption?.timeValue.toString() + if (durationRes != null) getCustomString(
                                StringResource(durationRes.resourceId)
                            ) else ""
                        viewModel.firePauseSipEvent(com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIP_PauseStatus)
                        coreUiApi.openGenericPostActionStatusFragment(
                            GenericPostActionStatusData(
                                postActionStatus = PostActionStatus.DISABLED.name,
                                header = getCustomString(GoldSipMR.strings.feature_gold_sip_paused),
                                title = getCustomStringFormatted(
                                    GoldSipMR.strings.feature_gold_sip_we_will_not_debiting_your_s_sip_got_the_next_s,
                                    getCustomString(args.sipSubscriptionType.textRes),
                                    pauseTimeText
                                ),
                                imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_pause
                            )
                        ) {
                            findNavController().getBackStackEntry(R.id.goldSipDetailsFragment).savedStateHandle[com.jar.app.feature_gold_sip.shared.util.GoldSipConstants.PAUSE_SIP] =
                                true
                            dismiss()
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun updatePauseListOnClick(position: Int) {
        adapter?.currentList?.let {
            viewModel.updatePauseOptionListOnItemClick(it, position)
        }
    }
}
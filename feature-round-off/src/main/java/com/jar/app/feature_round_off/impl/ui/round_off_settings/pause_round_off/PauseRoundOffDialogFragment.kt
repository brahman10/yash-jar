package com.jar.app.feature_round_off.impl.ui.round_off_settings.pause_round_off

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.forEachIndexedVisibleHolder
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.app.feature_round_off.databinding.FeatureRoundOffDialogPauseRoundOffBinding
import com.jar.app.feature_round_off.shared.MR
import com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class PauseRoundOffDialogFragment :
    BaseDialogFragment<FeatureRoundOffDialogPauseRoundOffBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var adapter: PauseRoundOffOptionsAdapter? = null

    private var pauseRoundOffOption: com.jar.app.feature_round_off.shared.domain.model.PauseRoundOffOption? = null

    private val viewModel by viewModels<PauseRoundOffViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffDialogPauseRoundOffBinding
        get() = FeatureRoundOffDialogPauseRoundOffBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)

    override fun setup() {
        setupUI()
        initClickListeners()
        observeLiveData()
        analyticsHandler.postEvent(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_PauseRoundOffPopUp)
    }

    private fun setupUI() {
        binding.rvPauseDays.layoutManager =
            GridLayoutManager(context, 4, RecyclerView.VERTICAL, false)
        adapter = PauseRoundOffOptionsAdapter { pauseOption, position ->
            binding.rvPauseDays.forEachIndexedVisibleHolder<PauseRoundOffOptionsAdapter.PauseRoundOffOptionViewHolder> { holder, holderPos ->
                if (holderPos == position)
                    holder.select()
                else
                    holder.deselect()
                this.pauseRoundOffOption = pauseOption
                binding.btnPause.setDisabled(false)
            }
        }
        binding.rvPauseDays.adapter = adapter
        adapter?.submitList(
            listOf(
                com.jar.app.feature_round_off.shared.domain.model.PauseRoundOffOption.ONE,
                com.jar.app.feature_round_off.shared.domain.model.PauseRoundOffOption.FIVE,
                com.jar.app.feature_round_off.shared.domain.model.PauseRoundOffOption.TEN,
                com.jar.app.feature_round_off.shared.domain.model.PauseRoundOffOption.FIFTEEN
            )
        )

        binding.lottieView.playLottieWithUrlAndExceptionHandling(requireContext(),"${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json")
    }

    private fun initClickListeners() {
        binding.btnCancel.setDebounceClickListener {
            analyticsHandler.postEvent(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_Cancel_PauseRoundOffPopUp)
            dismissAllowingStateLoss()
        }

        binding.btnPause.setDebounceClickListener {
            if (pauseRoundOffOption != null) {
                analyticsHandler.postEvent(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_Pause_PauseRoundOffPopUp, mapOf(
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.days to pauseRoundOffOption?.name.orEmpty()
                    )
                )
                viewModel.updateAutoInvestPauseDuration(pauseRoundOffOption!!)
            }
        }
    }

    private fun observeLiveData() {
        viewModel.pauseRoundOffLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                setupSuccessPageData(it)
                binding.clContent.slideToRevealNew(
                    viewToReveal = binding.clSuccess,
                    onAnimationEnd = {
                        binding.lottieView.playAnimation()
                        uiScope.launch {
                            analyticsHandler.postEvent(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_Success_PauseRoundOffPopUp)
                            EventBus.getDefault().post(
                                com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffPauseDurationEvent(
                                    it
                                )
                            )
                            EventBus.getDefault().post(RefreshRoundOffStateEvent())
                            delay(3000)
                            dismissAllowingStateLoss()
                        }
                    }
                )
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    private fun setupSuccessPageData(pauseSavingResponse: PauseSavingResponse) {
        val pausedDays = com.jar.app.feature_round_off.shared.domain.model.PauseRoundOffOption.valueOf(pauseSavingResponse.pausedFor!!)
        binding.tvDays.text = getCustomPlural(
            MR.plurals.feature_round_off_n_days,
            pausedDays.number
        )
    }
}